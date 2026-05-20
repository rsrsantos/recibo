package com.br.rr.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.ReciboForm;
import com.br.rr.dto.ReciboItemDto;
import com.br.rr.util.ValorExtenso;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Emitente;
import com.br.rr.models.ModeloRecibo;
import com.br.rr.models.Pessoa;
import com.br.rr.models.Recibo;
import com.br.rr.models.ReciboItem;
import com.br.rr.models.Usuario;
import com.br.rr.models.Plano;
import com.br.rr.models.UsuarioPlano;
import com.br.rr.repository.EmitenteRepository;
import com.br.rr.repository.ReciboRepository;
import com.br.rr.service.AssinaturaGuard;
import com.br.rr.service.ContaService;
import com.br.rr.service.PessoaService;
import com.br.rr.service.ReciboService;
import com.br.rr.service.UsuarioPlanoService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
@Transactional
public class ReciboServiceImpl implements ReciboService {

	private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	private static final DateTimeFormatter DATA_BR =
			DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

	private final ReciboRepository repository;
	private final EmitenteRepository emitenteRepository;
	private final ContaService contaService;
	private final PessoaService pessoaService;
	private final UsuarioPlanoService usuarioPlanoService;
	private final AssinaturaGuard assinaturaGuard;

	public ReciboServiceImpl(ReciboRepository repository, EmitenteRepository emitenteRepository,
			ContaService contaService, PessoaService pessoaService,
			UsuarioPlanoService usuarioPlanoService, AssinaturaGuard assinaturaGuard) {
		this.repository = repository;
		this.emitenteRepository = emitenteRepository;
		this.contaService = contaService;
		this.pessoaService = pessoaService;
		this.usuarioPlanoService = usuarioPlanoService;
		this.assinaturaGuard = assinaturaGuard;
	}

	/** Bloqueia se o plano ativo define limite e ele já foi atingido no mês. */
	private void validarLimiteMensal(Usuario usuario) {
		UsuarioPlano ativo = usuarioPlanoService.buscarAtivo(usuario).orElse(null);
		if (ativo == null) {
			return; // sem plano: o paywall já barra o acesso
		}
		Plano plano = ativo.getPlano();
		Integer limite = plano.getLimiteRecibos();
		if (limite == null) {
			return; // ilimitado
		}
		LocalDate hoje = LocalDate.now();
		LocalDate inicio = hoje.withDayOfMonth(1);
		LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
		long usados = repository.countNoPeriodo(usuario, inicio, fim);
		if (usados >= limite) {
			throw new NegocioException("Você atingiu o limite de " + limite
					+ " recibos/mês do plano \"" + plano.getNome()
					+ "\". Faça upgrade de plano em Meu Perfil para emitir mais.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Recibo> listar(Pageable pageable) {
		return repository.findByUsuario(contaService.usuarioLogado(), pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Recibo buscarPorId(Long id) {
		return repository.findByIdAndUsuario(id, contaService.usuarioLogado())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Recibo não encontrado: " + id));
	}

	@Override
	public Recibo emitir(ReciboForm form) {
		if (form.getDestinatarioId() == null) {
			throw new NegocioException("Selecione o destinatário do recibo.");
		}
		if (form.getVlrTotal() == null || form.getVlrTotal().signum() < 0) {
			throw new NegocioException("Informe um valor total válido.");
		}
		Usuario usuario = contaService.usuarioLogado();
		validarLimiteMensal(usuario);

		ModeloRecibo modelo = form.getModelo() != null ? form.getModelo() : ModeloRecibo.PADRAO;
		if (!modelo.isImplementado()) {
			throw new NegocioException("O modelo selecionado ainda não está disponível.");
		}
		if (!assinaturaGuard.podeUsarModelo(usuario, modelo)) {
			throw new NegocioException("Seu plano não inclui o modelo \""
					+ modelo.getDescricao() + "\".");
		}

		Pessoa destinatario = pessoaService.buscarPorId(form.getDestinatarioId());

		Recibo recibo = new Recibo();
		recibo.setUsuario(usuario);
		recibo.setModelo(modelo);
		recibo.setDestinatario(destinatario);
		recibo.setReferente(form.getReferente());
		recibo.setObservacao(form.getObservacao());
		recibo.setDataGeracao(form.getDataGeracao() != null ? form.getDataGeracao() : LocalDate.now());
		recibo.setVlrTotal(form.getVlrTotal());

		String numero = form.getNRecibo();
		if (numero == null || numero.isBlank()) {
			numero = String.valueOf(repository.countByUsuario(usuario) + 1);
		}
		recibo.setNRecibo(numero);

		return repository.save(recibo);
	}

	@Override
	public void excluir(Long id) {
		repository.delete(buscarPorId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return repository.countByUsuario(contaService.usuarioLogado());
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal somaTotal() {
		return repository.somaTotalPorUsuario(contaService.usuarioLogado());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Recibo> ultimos(int quantidade) {
		return repository.ultimos(contaService.usuarioLogado(), PageRequest.of(0, quantidade));
	}

	@Override
	@Transactional(readOnly = true)
	public long proximoNumero() {
		return repository.countByUsuario(contaService.usuarioLogado()) + 1;
	}

	/** Resolve o arquivo JRXML conforme o modelo do recibo. */
	private String jrxmlDoModelo(ModeloRecibo modelo) {
		// Por enquanto apenas o modelo PADRAO possui layout. Os demais herdam o padrão
		// até terem seus próprios JRXML (recursos PREMIUM).
		return "/reports/recibo.jrxml";
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] gerarPdf(Long id, int vias) {
		Recibo recibo = buscarPorId(id); // já valida IDOR
		Usuario usuario = recibo.getUsuario();
		Emitente emitente = emitenteRepository.findByUsuario(usuario).orElse(null);
		Pessoa dest = recibo.getDestinatario();
		int copias = Math.max(1, Math.min(vias, 10));

		try {
			InputStream jrxmlStream = getClass().getResourceAsStream(jrxmlDoModelo(recibo.getModelo()));
			JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

			Map<String, Object> params = new HashMap<>();
			params.put("N_RECIBO", recibo.getNRecibo());
			params.put("DATA_GERACAO", recibo.getDataGeracao() != null
					? recibo.getDataGeracao().format(DATA_BR) : "");
			params.put("REFERENTE", nvl(recibo.getReferente()));
			params.put("OBSERVACAO", nvl(recibo.getObservacao()));
			BigDecimal vlr = recibo.getVlrTotal() != null ? recibo.getVlrTotal() : BigDecimal.ZERO;
			params.put("VLR_TOTAL", BRL.format(vlr));
			params.put("VALOR_EXTENSO", ValorExtenso.converter(vlr));

			if (emitente != null) {
				params.put("EMI_NOME", nvl(emitente.getNome()));
				params.put("EMI_DOC", nvl(emitente.getDocumento()));
				params.put("EMI_FONE", nvl(emitente.getTelefone()));
				params.put("EMI_EMAIL", nvl(emitente.getEmail()));
				params.put("EMI_ENDERECO", montarEndereco(emitente));
				if (emitente.getLogo() != null) {
					params.put("LOGO_IMAGE", new ByteArrayInputStream(emitente.getLogo()));
				}
			} else {
				params.put("EMI_NOME", "");
				params.put("EMI_DOC", "");
				params.put("EMI_FONE", "");
				params.put("EMI_EMAIL", "");
				params.put("EMI_ENDERECO", "");
			}

			params.put("DEST_NOME", dest != null ? nvl(dest.getNome()) : "");
			params.put("DEST_DOC", "");
			params.put("DEST_FONE", "");
			params.put("DEST_EMAIL", "");

			List<ReciboItemDto> itens = new ArrayList<>();
			for (ReciboItem item : recibo.getItens()) {
				String descricao = item.getProdutoServico() != null
						? item.getProdutoServico().getNome() : "";
				String unitario = item.getVlrUnitario() != null
						? BRL.format(item.getVlrUnitario()) : "";
				String subtotal = item.getVlrTotal() != null
						? BRL.format(item.getVlrTotal()) : "";
				itens.add(new ReciboItemDto(descricao, item.getQtde(), unitario, subtotal));
			}
			if (itens.isEmpty()) {
				itens.add(new ReciboItemDto(nvl(recibo.getReferente()),
						1, BRL.format(recibo.getVlrTotal()), BRL.format(recibo.getVlrTotal())));
			}

			JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(itens);
			JasperPrint print = JasperFillManager.fillReport(jasperReport, params, ds);

			// Vias extras: duplica as páginas do recibo (cada via = nova página).
			if (copias > 1) {
				List<net.sf.jasperreports.engine.JRPrintPage> original =
						new ArrayList<>(print.getPages());
				for (int v = 1; v < copias; v++) {
					for (net.sf.jasperreports.engine.JRPrintPage pagina : original) {
						print.addPage(pagina);
					}
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
			exporter.exportReport();
			return out.toByteArray();

		} catch (Exception e) {
			throw new NegocioException("Erro ao gerar PDF do recibo: " + e.getMessage());
		}
	}

	private String nvl(String s) {
		return s != null ? s : "";
	}

	private String montarEndereco(Emitente e) {
		StringBuilder sb = new StringBuilder();
		if (e.getLogradouro() != null && !e.getLogradouro().isBlank()) {
			sb.append(e.getLogradouro());
			if (e.getNumero() != null && !e.getNumero().isBlank())
				sb.append(", ").append(e.getNumero());
		}
		if (e.getBairro() != null && !e.getBairro().isBlank()) {
			if (sb.length() > 0) sb.append(" — ");
			sb.append(e.getBairro());
		}
		if (e.getCidade() != null && !e.getCidade().isBlank()) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(e.getCidade());
			if (e.getEstado() != null && !e.getEstado().isBlank())
				sb.append("/").append(e.getEstado());
		}
		return sb.toString();
	}

}
