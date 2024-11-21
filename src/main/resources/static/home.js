function atualizarStatus() {
    $.ajax({
        url: '/homeIrriga',
        method: 'GET',
        success: function(data) {
            // Atualiza os elementos HTML com os dados retornados
            $('#proxima-irrigacao').text(
                data.irrigacaoAtual ? data.irrigacaoAtual.horaIrrigacao : 'Nenhuma programada'
            );

            $('#countdown').text(
                data.countdownSegundos !== null ? data.countdownSegundos : '---'
            );
        },
        error: function(xhr, status, error) {
            console.error('Erro ao atualizar status:', error);
        }
    });
}

// Atualiza a cada segundo
setInterval(atualizarStatus, 1000);

// Faz uma atualização inicial
atualizarStatus();