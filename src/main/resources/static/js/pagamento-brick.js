(async () => {
  function esconderLoading() {
    document.getElementById('brick-loading').classList.add('d-none');
  }
  function mostrarErroBrick() {
    esconderLoading();
    document.getElementById('brick-error').classList.remove('d-none');
  }

  let mp;
  try {
    mp = new MercadoPago(MP_PUBLIC_KEY, { locale: 'pt-BR' });
  } catch (e) {
    console.error('Falha ao inicializar MercadoPago SDK', e);
    mostrarErroBrick();
    return;
  }

  const bricks = mp.bricks();

  const settings = {
    initialization: {
      amount: parseFloat(VALOR),
    },
    customization: {
      paymentMethods: {
        creditCard: 'all',
        debitCard: 'all',
        bankTransfer: ['pix'],
        maxInstallments: 1,
      },
      visual: { style: { theme: 'default' } },
    },
    callbacks: {
      onReady: () => {
        esconderLoading();
      },
      onError: (err) => {
        console.error('Brick error', err);
        mostrarErroBrick();
      },
      onSubmit: async ({ selectedPaymentMethod, formData }) => {
        mostrarAlerta('info', 'Processando pagamento...');
        desabilitarBrick();

        try {
          const resp = await fetch(`/recibo/pagamento/processar/${PLANO_ID}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ...formData, payment_type_id: selectedPaymentMethod }),
          });

          if (!resp.ok) {
            const err = await resp.json().catch(() => ({}));
            mostrarAlerta('danger', err.mensagem || 'Erro ao processar o pagamento. Tente novamente.');
            habilitarBrick();
            return;
          }

          const data = await resp.json();

          if (data.aprovado) {
            mostrarAlerta('success', '✓ Pagamento aprovado! Seu plano foi ativado. Redirecionando...');
            esconderBrick();
            setTimeout(() => { window.location.href = '/recibo/'; }, 2500);

          } else if (data.qrBase64) {
            // PIX gerado
            esconderBrick();
            mostrarPix(data.pagamentoId, data.qrBase64, data.qrCode);
            mostrarAlerta('info', 'QR Code gerado. Escaneie pelo app do seu banco para confirmar.');

          } else if (data.status === 'pending') {
            mostrarAlerta('warning', 'Pagamento pendente. Aguarde a confirmação.');

          } else {
            mostrarAlerta('danger', 'Pagamento não aprovado. Verifique os dados e tente novamente.');
            habilitarBrick();
          }

        } catch (e) {
          console.error(e);
          mostrarAlerta('danger', 'Falha de comunicação. Verifique sua conexão e tente novamente.');
          habilitarBrick();
        }
      },
    },
  };

  try {
    await bricks.create('payment', 'paymentBrick_container', settings);
  } catch (e) {
    console.error('Falha ao criar Brick', e);
    mostrarErroBrick();
  }
})();

// ---------- PIX ----------

function mostrarPix(pagamentoId, qrBase64, qrCode) {
  document.getElementById('pix-qr').src = 'data:image/png;base64,' + qrBase64;
  document.getElementById('pix-copia-cola').value = qrCode;
  document.getElementById('pix-section').classList.remove('d-none');
  iniciarTimer(pagamentoId, 10 * 60); // 10 minutos
}

function iniciarTimer(pagamentoId, segundosTotal) {
  const timerEl = document.getElementById('pix-timer');
  let restante = segundosTotal;
  let pollingInterval = null;

  function atualizarTimer() {
    const min = String(Math.floor(restante / 60)).padStart(2, '0');
    const sec = String(restante % 60).padStart(2, '0');
    timerEl.textContent = `${min}:${sec}`;

    if (restante <= 60) timerEl.classList.add('text-danger');
    if (restante <= 0) {
      clearInterval(timerInterval);
      clearInterval(pollingInterval);
      timerEl.textContent = '00:00';
      mostrarAlerta('danger', 'Tempo esgotado. O QR Code expirou. Gere um novo pagamento.');
      document.getElementById('pix-section').classList.add('opacity-50');
      document.getElementById('pix-copia-cola').disabled = true;
      return;
    }
    restante--;
  }

  atualizarTimer();
  const timerInterval = setInterval(atualizarTimer, 1000);

  // Polling a cada 5s consultando a API do MP
  pollingInterval = setInterval(async () => {
    try {
      const resp = await fetch(`/recibo/pagamento/status/${pagamentoId}`);
      const data = await resp.json();

      if (data.aprovado) {
        clearInterval(timerInterval);
        clearInterval(pollingInterval);
        document.getElementById('pix-status').textContent = '✓ Pagamento confirmado!';
        mostrarAlerta('success', '✓ PIX aprovado! Seu plano foi ativado. Redirecionando...');
        setTimeout(() => { window.location.href = '/recibo/'; }, 2500);

      } else if (data.expirado) {
        clearInterval(timerInterval);
        clearInterval(pollingInterval);
        mostrarAlerta('danger', 'Pagamento expirado. Gere um novo pagamento.');
      }
    } catch (e) {
      // Ignora falha de rede no polling — tentará novamente em 5s
    }
  }, 5000);
}

// ---------- Auxiliares ----------

function mostrarAlerta(tipo, msg) {
  const el = document.getElementById('resultado');
  el.className = `alert alert-${tipo}`;
  el.textContent = msg;
  el.classList.remove('d-none');
  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function esconderBrick() {
  document.getElementById('paymentBrick_container').innerHTML = '';
}

function desabilitarBrick() {
  document.getElementById('paymentBrick_container').style.pointerEvents = 'none';
  document.getElementById('paymentBrick_container').style.opacity = '0.6';
}

function habilitarBrick() {
  document.getElementById('paymentBrick_container').style.pointerEvents = '';
  document.getElementById('paymentBrick_container').style.opacity = '';
}

function copiarPix() {
  const val = document.getElementById('pix-copia-cola').value;
  navigator.clipboard.writeText(val).then(() => {
    const btn = document.getElementById('btn-copiar');
    btn.textContent = 'Copiado!';
    setTimeout(() => { btn.textContent = 'Copiar'; }, 2000);
  });
}
