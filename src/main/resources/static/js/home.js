const Toast = Swal.mixin({
  toast: true,
  position: "top-end",
  showConfirmButton: false,
  timer: 3000,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.onmouseenter = Swal.stopTimer;
    toast.onmouseleave = Swal.resumeTimer;
  }
});

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
                var partesData = response.irrigacaoAtualData.split("-");
                var dataRecebida = new Date(partesData[0], partesData[1] - 1, partesData[2]); // Ajuste do mês

                // Configura as horas, minutos, segundos para 00:00:00
                dataRecebida.setHours(0, 0, 0, 0);
                dataAtual.setHours(0, 0, 0, 0);  // Configura a data atual também para a meia-noite

                if (dataRecebida >= dataAtual) {
                    dataFormatada = dataRecebida.toLocaleDateString('pt-BR');  // Formata para "dd/mm/aaaa"
                }
            }

            // Verifica a hora
            if (response.irrigacaoAtualHora) {
                var horaRecebida = response.irrigacaoAtualHora;
                var partesHora = horaRecebida.split(":");
                if (partesHora.length === 3) {
                    horaFormatada = partesHora[0] + ":" + partesHora[1];  // "HH:mm"
                }
            }

            // Verifica o countdown
            if (response.countdownSegundos !== undefined && response.countdownSegundos !== null) {
                countdownSegundos = response.countdownSegundos;
            }

            // Atualiza os spans com os dados
            $("#proxima-irrigacao-data").text(dataFormatada);
            $("#proxima-irrigacao-hora").text(horaFormatada);
            $("#countdown").text(countdownSegundos);
        },
        error: function() {
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

function iniciarAtualizacaoAutomatica() {
    // Atualiza a cada 10 segundos (ajustado para intervalos mais realistas)
    setInterval(carregarProximasIrrigacoes, 10000);
}

$(document).ready(function() {
    iniciarAtualizacaoAutomatica();
});

document.addEventListener('DOMContentLoaded', function () {
    // Inicializa o carousel com autoplay
    var carouselElement = document.getElementById('carouselExampleAutoplaying');
    if (carouselElement) {
        var carousel = new bootstrap.Carousel(carouselElement, {
            interval: 4500, // Tempo de troca automática de slides
            ride: 'carousel'
        });
    }
});
