  var irrigacaoAtual = /*[[${statusAtual.irrigacaoAtual}]]*/ null;
        var countdownSegundos = /*[[${statusAtual.countdownSegundos}]]*/ null;

        // Verificar se irrigacaoAtual não é nulo
        if (irrigacaoAtual && irrigacaoAtual.horaIrrigacao) {
            $('#proxima-irrigacao').text(irrigacaoAtual.horaIrrigacao);
        } else {
            $('#proxima-irrigacao').text('Nenhuma programada');
        }

        // Verificar se countdownSegundos não é nulo
        if (countdownSegundos !== null) {
            $('#countdown').text(countdownSegundos);
        } else {
            $('#countdown').text('---');
        }

        // Função para atualizar os dados periodicamente
        function atualizarStatus() {
            $.ajax({
                url: '/homeIrriga',
                method: 'GET',
                success: function(data) {
                    // Atualiza os elementos HTML com os dados retornados
                    if (data.irrigacaoAtual && data.irrigacaoAtual.horaIrrigacao) {
                        $('#proxima-irrigacao').text(data.irrigacaoAtual.horaIrrigacao);
                    } else {
                        $('#proxima-irrigacao').text('Nenhuma programada');
                    }

                    if (data.countdownSegundos !== null) {
                        $('#countdown').text(data.countdownSegundos);
                    } else {
                        $('#countdown').text('---');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Erro ao atualizar status:', error);
                }
            });
        }

        // Atualiza a cada segundo
        setInterval(atualizarStatus, 1000);

        // Chama a função de atualização uma vez ao carregar a página
        atualizarStatus();