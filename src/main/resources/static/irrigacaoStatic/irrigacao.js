
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
            console.error("Erro ao buscar as próximas irrigações.");
        }
    });
}

// Carrega as próximas irrigacoes quando a página é carregada
atualizarListaIrrigacoes();

// Configura o intervalo para recarregar as próximas irrigacoes a cada 60 segundos
setInterval(function() {
    atualizarListaIrrigacoes(); // Chama a função para recarregar os dados
}, 20000); // 20000 ms = 20 segundos


