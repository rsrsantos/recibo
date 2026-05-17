// JS global do sistema. Scripts específicos de cada tela ficam em
// /js/<recurso>.js e são incluídos via layout:fragment="scripts".

document.addEventListener('DOMContentLoaded', function () {

	// Exibe automaticamente todos os toasts renderizados pelo servidor.
	if (window.bootstrap && bootstrap.Toast) {
		document.querySelectorAll('.toast').forEach(function (el) {
			new bootstrap.Toast(el).show();
		});
	}

	// Modal genérico de confirmação de exclusão (modais/confirmar-exclusao).
	var modal = document.getElementById('modalExcluir');
	if (modal) {
		modal.addEventListener('show.bs.modal', function (event) {
			var origem = event.relatedTarget;
			var url = origem ? origem.getAttribute('data-url') : null;
			var btn = document.getElementById('btnConfirmarExclusao');
			if (btn && url) btn.setAttribute('href', url);
		});
	}
});
