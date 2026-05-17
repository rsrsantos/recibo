// Itens dinâmicos do formulário de recibo.
// O índice precisa ser contínuo para o binding do Spring (itens[N]).
let indiceItemRecibo = 1;

function adicionarLinhaRecibo() {
	const tbody = document.querySelector('#tabelaItens tbody');
	const modelo = tbody.querySelector('.item-row');
	const nova = modelo.cloneNode(true);
	nova.querySelectorAll('select, input').forEach(function (el) {
		el.name = el.name.replace(/itens\[\d+\]/, 'itens[' + indiceItemRecibo + ']');
		if (el.tagName === 'SELECT') el.selectedIndex = 0;
		if (el.type === 'number') el.value = 1;
	});
	tbody.appendChild(nova);
	indiceItemRecibo++;
}

function removerLinhaRecibo(botao) {
	const linhas = document.querySelectorAll('#tabelaItens tbody .item-row');
	if (linhas.length > 1) {
		botao.closest('tr').remove();
	}
}
