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

document.getElementById("saveButton").addEventListener("click", function () {
    const dataIrrigacao = document.getElementById("datairrigacao").value;
    const horaIrrigacao = document.getElementById("horairrigacao").value;
    const intervalo = document.getElementById("intervalo").value;
    const selectedIrrigacaoId = document.getElementById("selectedIrrigacaoId").value;

    console.log('Data Irrigação:', dataIrrigacao); // Verifique o formato da data
    console.log('Hora Irrigação:', horaIrrigacao); // Verifique o formato da hora

    $.ajax({
        url: "/atualizarirrigacao",
        method: "POST",
        data: {
            id: selectedIrrigacaoId,
            datairrigacao: dataIrrigacao, // Certifique-se de que o nome é "datairrigacao"
            horairrigacao: horaIrrigacao, // Certifique-se de que o nome é "horairrigacao"
            intervalo: intervalo // Certifique-se de que o nome é "intervalo"
        },
        success: function (response) {
            alert(response); // Exibe a mensagem retornada pelo backend
            $("#optionsModal").modal('hide'); // Fecha o modal
            carregarProximasIrrigacoes(); // Atualiza os dados
        },
        error: function (xhr) {
            console.error(xhr.responseText); // Log do erro para debugging
            alert("Erro ao salvar alterações: " + xhr.responseText);
        }
    });
});

// Configuração para carregar as informações ao abrir o modal
document.addEventListener('DOMContentLoaded', function () {
    optionsModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;

        // Use data-irrigacao-id
        const id = button.getAttribute('data-irrigacao-id');
        const dataIrrigacao = button.getAttribute('data-datairrigacao');
        const horaIrrigacao = button.getAttribute('data-horairrigacao');
        const intervalo = button.getAttribute('data-intervalo');

        document.getElementById('datairrigacao').value = dataIrrigacao || '';
        document.getElementById('horairrigacao').value = horaIrrigacao || '';
        document.getElementById('intervalo').value = intervalo || '';

        document.getElementById('selectedIrrigacaoId').value = id; // Atualiza o ID escondido
    });
});

// Limpeza do modal após ser fechado
$("#optionsModal").on('hidden.bs.modal', function () {
    document.getElementById('datairrigacao').value = '';
    document.getElementById('horairrigacao').value = '';
    document.getElementById('intervalo').value = '';
    document.getElementById('selectedIrrigacaoId').value = '';
});

/*
$('#optionsModal').on('shown.bs.modal', function () {
    $(this).find('.modal-dialog').css({
        'margin-top': ($(window).height() - $('.modal-dialog').outerHeight()) / 2
    });
});
*/

