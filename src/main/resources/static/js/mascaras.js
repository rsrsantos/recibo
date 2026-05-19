// Máscaras de input (local, sem dependência externa).
// Uso: adicionar data-mask="cpf|cnpj|cpfcnpj|telefone|cep" no input.
(function () {

	function soDigitos(v) { return (v || '').replace(/\D/g, ''); }

	function cpf(v) {
		v = soDigitos(v).slice(0, 11);
		return v
			.replace(/(\d{3})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
	}

	function cnpj(v) {
		v = soDigitos(v).slice(0, 14);
		return v
			.replace(/(\d{2})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d)/, '$1.$2')
			.replace(/(\d{3})(\d)/, '$1/$2')
			.replace(/(\d{4})(\d{1,2})$/, '$1-$2');
	}

	function telefone(v) {
		v = soDigitos(v).slice(0, 11);
		if (v.length <= 10) {
			return v.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d{1,4})$/, '$1-$2');
		}
		return v.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d{1,4})$/, '$1-$2');
	}

	function cep(v) {
		return soDigitos(v).slice(0, 8).replace(/(\d{5})(\d{1,3})$/, '$1-$2');
	}

	const FN = { cpf: cpf, cnpj: cnpj, telefone: telefone, cep: cep };

	function aplicar(el) {
		if (el.dataset.maskBound) return;
		el.dataset.maskBound = '1';
		const tipo = el.getAttribute('data-mask');
		function handler() {
			if (tipo === 'cpfcnpj') {
				el.value = soDigitos(el.value).length > 11 ? cnpj(el.value) : cpf(el.value);
			} else if (FN[tipo]) {
				el.value = FN[tipo](el.value);
			}
		}
		el.addEventListener('input', handler);
		if (el.value) handler();

		// Normaliza para apenas dígitos no envio (persistência canônica).
		if (el.form && !el.form.dataset.maskNormalize) {
			el.form.dataset.maskNormalize = '1';
			el.form.addEventListener('submit', function () {
				el.form.querySelectorAll('[data-mask]').forEach(function (campo) {
					campo.value = soDigitos(campo.value);
				});
			});
		}
	}

	// ---------- Máscara de moeda (R$) ----------
	// Uso: <input type="text" data-currency data-hidden="idDoHiddenNumerico">
	//      <input type="hidden" id="idDoHiddenNumerico" th:field="*{campo}">
	function fmtMoeda(num) {
		return num.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
	}

	function aplicarCurrency(el) {
		if (el.dataset.currencyBound) return;
		el.dataset.currencyBound = '1';
		const hiddenId = el.getAttribute('data-hidden');
		const hidden = hiddenId ? document.getElementById(hiddenId) : null;

		function sync() {
			const d = (el.value || '').replace(/\D/g, '');
			const num = d ? parseInt(d, 10) / 100 : 0;
			el.value = d ? fmtMoeda(num) : '';
			if (hidden) hidden.value = num.toFixed(2);
		}
		el.addEventListener('input', sync);

		// Valor inicial vindo do servidor (edição)
		if (hidden && hidden.value) {
			const ini = parseFloat(hidden.value);
			if (!isNaN(ini) && ini > 0) el.value = fmtMoeda(ini);
		}
	}

	function init() {
		document.querySelectorAll('[data-mask]').forEach(aplicar);
		document.querySelectorAll('[data-currency]').forEach(aplicarCurrency);
	}

	// Aplica já (script no fim do body) e também garante via DOMContentLoaded.
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', init);
	} else {
		init();
	}

	window.aplicarMascaras = init;
})();
