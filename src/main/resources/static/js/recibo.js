// Tela de Novo Recibo: busca dinâmica de cliente, cadastro rápido (modal),
// valor por extenso e geração de número.
(function () {
	const form = document.getElementById('formRecibo');
	if (!form) return;

	const urlBuscar = form.getAttribute('data-url-buscar');
	const urlRapido = form.getAttribute('data-url-rapido');
	const proximo = form.getAttribute('data-proximo');

	const inputBusca = document.getElementById('clienteBusca');
	const resultados = document.getElementById('clienteResultados');
	const hiddenId = document.getElementById('destinatarioId');
	const selecionado = document.getElementById('clienteSelecionado');

	// ---------- Busca dinâmica (autocomplete) ----------
	let timer = null;
	inputBusca.addEventListener('input', function () {
		hiddenId.value = '';
		selecionado.textContent = '';
		const q = inputBusca.value.trim();
		clearTimeout(timer);
		if (q.length < 2) { resultados.style.display = 'none'; return; }
		timer = setTimeout(function () {
			fetch(urlBuscar + '?q=' + encodeURIComponent(q))
				.then(r => r.json())
				.then(lista => {
					resultados.innerHTML = '';
					if (!lista.length) { resultados.style.display = 'none'; return; }
					lista.forEach(function (p) {
						const a = document.createElement('a');
						a.href = '#';
						a.className = 'list-group-item list-group-item-action';
						a.textContent = p.nome + ' (' + p.tipo + ')';
						a.addEventListener('click', function (e) {
							e.preventDefault();
							selecionarCliente(p);
						});
						resultados.appendChild(a);
					});
					resultados.style.display = 'block';
				});
		}, 250);
	});

	document.addEventListener('click', function (e) {
		if (!resultados.contains(e.target) && e.target !== inputBusca) {
			resultados.style.display = 'none';
		}
	});

	function selecionarCliente(p) {
		hiddenId.value = p.id;
		inputBusca.value = p.nome;
		selecionado.textContent = 'Cliente selecionado: ' + p.nome;
		resultados.style.display = 'none';
	}

	// ---------- Cadastro rápido (modal) ----------
	const btnSalvarCliente = document.getElementById('btnSalvarCliente');
	if (btnSalvarCliente) {
		btnSalvarCliente.addEventListener('click', function () {
			const nome = document.getElementById('nc-nome').value.trim();
			const tipo = document.getElementById('nc-tipo').value;
			const documento = document.getElementById('nc-documento').value.replace(/\D/g, '');
			const erro = document.getElementById('nc-erro');
			erro.textContent = '';
			if (!nome) { erro.textContent = 'Informe o nome.'; return; }

			const body = new URLSearchParams();
			body.set('nome', nome);
			body.set('tipo', tipo);
			body.set('documento', documento);

			fetch(urlRapido, { method: 'POST', body: body })
				.then(r => { if (!r.ok) throw new Error(); return r.json(); })
				.then(p => {
					selecionarCliente(p);
					var modalEl = document.getElementById('modalNovoCliente');
					var modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
					modal.hide();
					document.getElementById('nc-nome').value = '';
					document.getElementById('nc-documento').value = '';
				})
				.catch(() => { erro.textContent = 'Não foi possível salvar o cliente.'; });
		});
	}

	// ---------- Máscara CPF/CNPJ do modal ----------
	const ncTipo = document.getElementById('nc-tipo');
	const ncDoc = document.getElementById('nc-documento');

	function fmtCpf(d) {
		d = d.slice(0, 11);
		if (d.length > 9) return d.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
		if (d.length > 6) return d.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
		if (d.length > 3) return d.replace(/(\d{3})(\d{1,3})/, '$1.$2');
		return d;
	}

	function fmtCnpj(d) {
		d = d.slice(0, 14);
		if (d.length > 12) return d.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{1,2})/, '$1.$2.$3/$4-$5');
		if (d.length > 8) return d.replace(/(\d{2})(\d{3})(\d{3})(\d{1,4})/, '$1.$2.$3/$4');
		if (d.length > 5) return d.replace(/(\d{2})(\d{3})(\d{1,3})/, '$1.$2.$3');
		if (d.length > 2) return d.replace(/(\d{2})(\d{1,3})/, '$1.$2');
		return d;
	}

	function aplicarDoc() {
		if (!ncDoc) return;
		const d = ncDoc.value.replace(/\D/g, '');
		ncDoc.value = (ncTipo && ncTipo.value === 'JURIDICA') ? fmtCnpj(d) : fmtCpf(d);
	}
	if (ncDoc) ncDoc.addEventListener('input', aplicarDoc);
	if (ncTipo) ncTipo.addEventListener('change', aplicarDoc);

	// ---------- Número ----------
	const btnGerar = document.getElementById('btnGerarNumero');
	const inputNumero = document.getElementById('nRecibo');
	if (btnGerar && inputNumero) {
		btnGerar.addEventListener('click', function () { inputNumero.value = proximo; });
		if (!inputNumero.value) inputNumero.value = proximo;
	}

	// ---------- Data padrão (hoje) ----------
	const inputData = document.getElementById('dataRecibo');
	if (inputData && !inputData.value) {
		inputData.value = new Date().toISOString().slice(0, 10);
	}

	// ---------- Valor (máscara de moeda) + por extenso ----------
	const valor = document.getElementById('valorRecibo');     // visível, formatado
	const valorHidden = document.getElementById('vlrTotal');  // canônico (enviado)
	const extenso = document.getElementById('importanciaExtenso');

	const UNID = ['', 'um', 'dois', 'três', 'quatro', 'cinco', 'seis', 'sete', 'oito', 'nove',
		'dez', 'onze', 'doze', 'treze', 'quatorze', 'quinze', 'dezesseis', 'dezessete',
		'dezoito', 'dezenove'];
	const DEZ = ['', '', 'vinte', 'trinta', 'quarenta', 'cinquenta', 'sessenta', 'setenta',
		'oitenta', 'noventa'];
	const CEM = ['', 'cento', 'duzentos', 'trezentos', 'quatrocentos', 'quinhentos',
		'seiscentos', 'setecentos', 'oitocentos', 'novecentos'];

	function trecho(n) {
		if (n === 0) return '';
		if (n === 100) return 'cem';
		let s = '';
		const c = Math.floor(n / 100);
		const d = Math.floor((n % 100) / 10);
		const u = n % 10;
		if (c) s += CEM[c];
		const resto = n % 100;
		if (resto) {
			if (s) s += ' e ';
			if (resto < 20) s += UNID[resto];
			else {
				s += DEZ[d];
				if (u) s += ' e ' + UNID[u];
			}
		}
		return s;
	}

	function porExtenso(valorNum) {
		const inteiro = Math.floor(valorNum);
		const centavos = Math.round((valorNum - inteiro) * 100);
		let txt = '';

		if (inteiro === 0) {
			txt = 'zero real';
		} else {
			const milhoes = Math.floor(inteiro / 1000000);
			const milhares = Math.floor((inteiro % 1000000) / 1000);
			const unidades = inteiro % 1000;
			const partes = [];
			if (milhoes) partes.push(trecho(milhoes) + (milhoes === 1 ? ' milhão' : ' milhões'));
			if (milhares) partes.push((milhares === 1 ? 'mil' : trecho(milhares) + ' mil'));
			if (unidades) partes.push(trecho(unidades));
			txt = partes.join(' e ') + (inteiro === 1 ? ' real' : ' reais');
		}

		if (centavos > 0) {
			txt += ' e ' + trecho(centavos) + (centavos === 1 ? ' centavo' : ' centavos');
		}
		return txt.charAt(0).toUpperCase() + txt.slice(1);
	}

	function formatarMoeda(num) {
		return num.toLocaleString('pt-BR', {
			minimumFractionDigits: 2, maximumFractionDigits: 2
		});
	}

	function aplicarMoeda() {
		const digitos = (valor.value || '').replace(/\D/g, '');
		const num = digitos ? parseInt(digitos, 10) / 100 : 0;
		valor.value = digitos ? formatarMoeda(num) : '';
		if (valorHidden) valorHidden.value = num.toFixed(2);
		extenso.value = num > 0 ? porExtenso(num) : '';
	}

	if (valor) {
		// valor pré-existente (ex.: erro de validação reabrindo o form)
		if (valorHidden && valorHidden.value) {
			const inicial = parseFloat(valorHidden.value);
			if (!isNaN(inicial) && inicial > 0) {
				valor.value = formatarMoeda(inicial);
				extenso.value = porExtenso(inicial);
			}
		}
		valor.addEventListener('input', aplicarMoeda);
	}
})();
