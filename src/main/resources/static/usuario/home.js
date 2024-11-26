function carregarProximasIrrigacoes() {
    $.ajax({
        url: "/hometwo",  // URL do controller no backend
        method: "GET",    // Método HTTP
        success: function(response) {
            // Inicializa variáveis para os valores formatados
            var dataFormatada = "Não definida";
            var horaFormatada = "Não definida";
            var countdownSegundos = "0";

            // Data atual para comparação
            var dataAtual = new Date();

            // Verifica se a data recebida é válida e futura
            if (response.irrigacaoAtualData) {
                // Converte a data recebida para um objeto Date, ignorando a hora
                var partesData = response.irrigacaoAtualData.split("-");  // Espera o formato "YYYY-MM-DD"
                var dataRecebida = new Date(partesData[0], partesData[1] - 1, partesData[2]); // Ajuste do mês (0-11)

                // Configura as horas, minutos, segundos e milissegundos para 00:00:00 para comparar apenas as datas
                dataRecebida.setHours(0, 0, 0, 0);
                dataAtual.setHours(0, 0, 0, 0);  // Configura a data atual também para a meia-noite

                // Compara as duas datas sem considerar as horas
                if (dataRecebida >= dataAtual) {
                    dataFormatada = dataRecebida.toLocaleDateString('pt-BR');  // Formata a data para "dd/mm/aaaa"
                }
            }

            // Verifica se a hora recebida é válida
            if (response.irrigacaoAtualHora) {
                var horaRecebida = response.irrigacaoAtualHora;
                var partesHora = horaRecebida.split(":");  // Divide em hora e minuto
                if (partesHora.length === 3) {
                    horaFormatada = partesHora[0] + ":" + partesHora[1];  // Mantém a hora no formato "HH:mm"
                }
            }

            // Verifica se o countdown é válido
            if (response.countdownSegundos !== undefined && response.countdownSegundos !== null) {
                countdownSegundos = response.countdownSegundos;
            }

            // Atualiza os spans com os dados
            $("#proxima-irrigacao-data").text(dataFormatada);
            $("#proxima-irrigacao-hora").text(horaFormatada);
            $("#countdown").text(countdownSegundos);
        },
        error: function() {
            alert("Erro ao carregar as informações.");
        }
    });
}

function iniciarAtualizacaoAutomatica() {
    // Chama a função a cada 6 segundos
    setInterval(carregarProximasIrrigacoes, 6000);
}

$(document).ready(function() {
    iniciarAtualizacaoAutomatica();
});

document.addEventListener('DOMContentLoaded', function () {
    // Inicializa o carousel com autoplay
    var carouselElement = document.getElementById('carouselExampleAutoplaying');
    var carousel = new bootstrap.Carousel(carouselElement, {
        interval: 4500, // Tempo para a troca automática de slides (4,5 segundos)
        ride: 'carousel' // Ativar o autoplay
    });
});

