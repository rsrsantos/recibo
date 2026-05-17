package com.br.rr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.rr.dto.ItemReciboForm;
import com.br.rr.dto.ReciboForm;
import com.br.rr.exception.NegocioException;
import com.br.rr.exception.RecursoNaoEncontradoException;
import com.br.rr.models.Cliente;
import com.br.rr.models.Produto;
import com.br.rr.models.Recibo;
import com.br.rr.models.ReciboProduto;
import com.br.rr.repository.ReciboRepository;
import com.br.rr.service.ClienteService;
import com.br.rr.service.ProdutoService;
import com.br.rr.service.ReciboService;

@Service
@Transactional
public class ReciboServiceImpl implements ReciboService {

	private final ReciboRepository reciboRepository;
	private final ClienteService clienteService;
	private final ProdutoService produtoService;

	public ReciboServiceImpl(ReciboRepository reciboRepository, ClienteService clienteService,
			ProdutoService produtoService) {
		this.reciboRepository = reciboRepository;
		this.clienteService = clienteService;
		this.produtoService = produtoService;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Recibo> listar(Pageable pageable) {
		return reciboRepository.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Recibo buscarPorId(long id) {
		return reciboRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Recibo não encontrado: id " + id));
	}

	@Override
	public Recibo emitir(ReciboForm form) {
		if (form.getClienteId() == null) {
			throw new NegocioException("Selecione um cliente para o recibo.");
		}

		Cliente cliente = clienteService.buscarPorId(form.getClienteId());

		Recibo recibo = new Recibo();
		recibo.setCliente(cliente);
		recibo.setObservacao(form.getObservacao());

		// Resolve cada produto e congela o preço unitário no momento da emissão.
		for (ItemReciboForm itemForm : form.getItens()) {
			if (itemForm.getProdutoId() == null || itemForm.getQuantidade() == null
					|| itemForm.getQuantidade() <= 0) {
				continue;
			}
			Produto produto = produtoService.buscarPorId(itemForm.getProdutoId());

			ReciboProduto item = new ReciboProduto();
			item.setProduto(produto);
			item.setQuantidade(itemForm.getQuantidade());
			item.setValor(produto.getValor());
			recibo.getItens().add(item);
		}

		if (recibo.getItens().isEmpty()) {
			throw new NegocioException("Adicione ao menos um produto ao recibo.");
		}

		recibo.recalcularTotal();
		recibo.setNumeroRecibo((int) reciboRepository.count() + 1);

		return reciboRepository.save(recibo);
	}

	@Override
	public void excluir(long id) {
		Recibo recibo = buscarPorId(id);
		reciboRepository.delete(recibo);
	}

	@Override
	@Transactional(readOnly = true)
	public long contar() {
		return reciboRepository.count();
	}

	@Override
	@Transactional(readOnly = true)
	public double somaValorTotal() {
		return reciboRepository.somaValorTotal();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Recibo> ultimos(int quantidade) {
		return reciboRepository.ultimos(PageRequest.of(0, quantidade));
	}

}
