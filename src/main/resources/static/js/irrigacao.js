function atualizarListaIrrigacoes() {
    $.ajax({
        url: '/irrigacao/agendarLista', // URL do endpoint para obter as próximas irrigações
        method: 'GET',
        success: function (data) {
            let tabelaBody = $('#tabela-irrigacoes tbody');
            tabelaBody.empty(); // Limpa a tabela antes de adicionar os novos dados

            data.forEach(irrigacao => {
                let linha = `
                    <tr>
                        <td>${irrigacao.dataIrrigacao}</td>
                        <td>${irrigacao.horaIrrigacao}</td>
                        <td>${irrigacao.intervalo}</td>
                    </tr>
                `;
                tabelaBody.append(linha);
            });
        },
        error: function () {
            Swal.fire({
                            icon: 'error',
                            title: 'Erro pegando próxima irrigação!',
                            text: 'Algo deu errado ao pegar as informações da próxima irrigação. Recarregue a página.',
                            showConfirmButton: false,
                            timer: 6000,
                            timerProgressBar: true,
                            didOpen: (toast) => {
                                toast.onmouseenter = Swal.stopTimer;
                                toast.onmouseleave = Swal.resumeTimer;
                            }
                        });
        }
    });
}

// Carrega as próximas irrigacoes quando a página é carregada
atualizarListaIrrigacoes();

// Configura o intervalo para recarregar as próximas irrigacoes a cada 60 segundos
setInterval(function() {
    atualizarListaIrrigacoes(); // Chama a função para recarregar os dados
}, 10000); // 20000 ms = 20 segundos
