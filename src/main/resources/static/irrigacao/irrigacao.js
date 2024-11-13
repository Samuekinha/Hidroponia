$(document).ready(function() {
    var isRequestInProgress = false; // Flag para verificar se a requisição está em andamento

    // Função para carregar as próximas irrigacoes
    function carregarProximasIrrigacoes() {
        if (isRequestInProgress) {
            return; // Se já houver uma requisição em andamento, não faz outra
        }

        isRequestInProgress = true; // Define a flag como verdadeira (requisição em andamento)

        $.ajax({
            url: '/agendar-irrigacao', // URL da requisição
            type: 'GET', // get
            success: function(response) {
                $('#conteudo-irrigacoes').html(response); // Atualiza a tabela
            },
            error: function() {
                alert('Erro ao carregar as próximas irrigações.');
            },
            complete: function() {
                isRequestInProgress = false; // Após a requisição ser completada, a flag é resetada
            }
        });
    }

    // Carrega as próximas irrigacoes quando a página é carregada
    carregarProximasIrrigacoes();

    // Configura o intervalo para recarregar as próximas irrigacoes a cada 60 segundos
    setInterval(function() {
        carregarProximasIrrigacoes(); // Chama a função para recarregar os dados
    }, 60000); // 60000 ms = 60 segundos
});


document.addEventListener("DOMContentLoaded", function () {
    const toggleDropdown = document.querySelector('.toggle-dropdown');
    const dropdownMenu = document.querySelector('.dropdown-menu');

    // Toggle a classe 'show' para exibir ou ocultar o menu dropdown
    toggleDropdown.addEventListener('click', function (event) {
        event.preventDefault(); // Impede o comportamento padrão do link
        dropdownMenu.classList.toggle('show');
    });

    // Fecha o dropdown ao clicar fora dele
    document.addEventListener('click', function (event) {
        if (!toggleDropdown.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.classList.remove('show');
        }
    });
});
