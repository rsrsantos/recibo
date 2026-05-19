// Form de cliente: alterna Física/Jurídica e aplica máscara de CPF/CNPJ.
(function () {
	const form = document.getElementById('formPessoa');
	if (!form) return;

	const selectTipo = form.querySelector('[name="tipo"]');
	const blocoFisica = document.getElementById('bloco-fisica');
	const blocoJuridica = document.getElementById('bloco-juridica');

	function alternar() {
		if (!selectTipo || !blocoFisica || !blocoJuridica) return;
		const fisica = selectTipo.value === 'FISICA';
		blocoFisica.style.display = fisica ? '' : 'none';
		blocoJuridica.style.display = fisica ? 'none' : '';
	}
	if (selectTipo) {
		selectTipo.addEventListener('change', alternar);
		alternar();
	}

	function soDigitos(v) { return (v || '').replace(/\D/g, ''); }

	function fmtCpf(d) {
		d = d.slice(0, 11);
		let r = d;
		if (d.length > 9) r = d.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
		else if (d.length > 6) r = d.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
		else if (d.length > 3) r = d.replace(/(\d{3})(\d{1,3})/, '$1.$2');
		return r;
	}

	function fmtCnpj(d) {
		d = d.slice(0, 14);
		let r = d;
		if (d.length > 12) r = d.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{1,2})/, '$1.$2.$3/$4-$5');
		else if (d.length > 8) r = d.replace(/(\d{2})(\d{3})(\d{3})(\d{1,4})/, '$1.$2.$3/$4');
		else if (d.length > 5) r = d.replace(/(\d{2})(\d{3})(\d{1,3})/, '$1.$2.$3');
		else if (d.length > 2) r = d.replace(/(\d{2})(\d{1,3})/, '$1.$2');
		return r;
	}

	function ligarMascara(input, formatar) {
		if (!input) return;
		input.addEventListener('input', function () {
			input.value = formatar(soDigitos(input.value));
		});
		input.value = formatar(soDigitos(input.value)); // valor pré-existente
	}

	// th:field gera id = nome do campo
	ligarMascara(document.getElementById('cpf'), fmtCpf);
	ligarMascara(document.getElementById('cnpj'), fmtCnpj);

	// Persiste só dígitos (canônico) ao salvar.
	form.addEventListener('submit', function () {
		['cpf', 'cnpj'].forEach(function (id) {
			const el = document.getElementById(id);
			if (el) el.value = soDigitos(el.value);
		});
	});
})();
