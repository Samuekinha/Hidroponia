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


// Para salvar alterações
function saveChanges() {
    const data = {
        dataIrrigacao: document.getElementById("dataIrrigacao").value,
        horaIrrigacao: document.getElementById("horaIrrigacao").value,
        intervalo: document.getElementById("intervalo").value
    };

    fetch(`/irrigacoes/update/${selectedIrrigacaoId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            alert("Alterações salvas com sucesso!");
            location.reload();
        } else {
            alert("Erro ao salvar alterações.");
        }
    })
    .catch(error => console.error("Erro:", error));
}

/*
$('#optionsModal').on('shown.bs.modal', function () {
    $(this).find('.modal-dialog').css({
        'margin-top': ($(window).height() - $('.modal-dialog').outerHeight()) / 2
    });
});
*/

document.addEventListener('DOMContentLoaded', function () {
    const optionsModal = document.getElementById('optionsModal');

    optionsModal.addEventListener('show.bs.modal', function (event) {
        // Botão que acionou o modal
        const button = event.relatedTarget;

        // Pegando os dados do botão (atributos data-*)
        const id = button.getAttribute('data-optionsb');
        const dataIrrigacao = button.getAttribute('data-datairrigacao');
        const horaIrrigacao = button.getAttribute('data-horairrigacao');
        const intervalo = button.getAttribute('data-intervalo');

        // Atualizando os campos do modal com os dados da irrigação
        document.getElementById('datairrigacao').value = dataIrrigacao || '';
        document.getElementById('horairrigacao').value = horaIrrigacao || '';
        document.getElementById('intervalo').value = intervalo || '';

        // (Opcional) Salvar o ID em algum campo oculto ou variável global
        console.log('ID da irrigação:', id);
    });
});
