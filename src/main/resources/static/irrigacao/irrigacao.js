// Variável para controle da requisição
var isRequestInProgress = false;

// Função para carregar as próximas irrigacoes
function carregarProximasIrrigacoes() {
    // Verifica se já há uma requisição em andamento
    if (isRequestInProgress) {
        return; // Se já houver uma requisição, não faz outra
    }

    isRequestInProgress = true; // Marca a requisição como em andamento

    $.ajax({
        url: '/lista-irrigacao', // URL da requisição
        type: 'GET', // Método GET
        success: function(response) {
            $('#conteudo-irrigacoes').html(response); // Atualiza a tabela com o conteúdo retornado
        },
        error: function() {
            alert('Erro ao carregar as próximas irrigações.'); // Se der erro na requisição
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

// Função para atualizar os dados de uma irrigação na tabela
function atualizarDadosTabela() {
    const dataIrrigacao = document.getElementById("datairrigacao")?.value || '';
    const horaIrrigacao = document.getElementById("horairrigacao")?.value || '';
    const intervalo = document.getElementById("intervalo")?.value || '';
    const selectedIrrigacaoId = document.getElementById("selectedIrrigacaoId")?.value || '';

    $("#data" + id).text(data);  // Atualiza o texto do elemento que exibe a data
    $("#hora" + id).text(hora);  // Atualiza o texto do elemento que exibe a hora
    $("#dura" + id).text(intervalo);  // Atualiza o texto do elemento que exibe o intervalo
}

// Função para salvar a irrigação
document.addEventListener("DOMContentLoaded", function () {
    const saveButton = document.getElementById("saveButton");
    const deleteButton = document.getElementById("deleteButton");

    if (!saveButton) {
        console.error("Botão saveButton não encontrado!");
        return;
    }
    if (!deleteButton){
        console.error("Botão deleteButton nao achado")
        return;
    }

    saveButton.addEventListener("click", function () {
        const dataIrrigacao = document.getElementById("datairrigacao")?.value || '';
        const horaIrrigacao = document.getElementById("horairrigacao")?.value || '';
        const intervalo = document.getElementById("intervalo")?.value || '';
        const selectedIrrigacaoId = document.getElementById("selectedIrrigacaoId")?.value || '';

        // Validando os campos antes de enviar a requisição
        if (!dataIrrigacao || !horaIrrigacao || !intervalo || !selectedIrrigacaoId) {
            alert("Todos os campos devem ser preenchidos.");
            return;
        }

        console.log('Data Irrigação:', dataIrrigacao);
        console.log('Hora Irrigação:', horaIrrigacao);
        console.log('Intervalo:', intervalo);
        console.log('ID selecionado:', selectedIrrigacaoId);

        // Fazendo a chamada AJAX para atualizar a irrigação
        $.ajax({
            url: "/atualizarirrigacao",
            method: "POST",
            data: {
                id: selectedIrrigacaoId,
                datairrigacao: dataIrrigacao,
                horairrigacao: horaIrrigacao,
                intervalo: intervalo
            },
            success: function () {
                $("#optionsModal").modal('hide'); // Fecha o modal
                $("#data"+selectedIrrigacaoId).text(dataIrrigacao);
                $("#hora"+selectedIrrigacaoId).text(horaIrrigacao);
                $("#dura"+selectedIrrigacaoId).text(intervalo);
            },
            error: function () {
                alert("Erro ao atualizar irrigação.");
            }
        });
    });

    deleteButton.addEventListener("click", function () {
        const selectedIrrigacaoId = document.getElementById("selectedIrrigacaoId")?.value || '';

        // Fazendo a chamada AJAX para atualizar a irrigação
        $.ajax({
            url: "/deletairrigacao",
            method: "POST",
            data: {
                id: selectedIrrigacaoId,
            },
            success: function () {
                $("#optionsModal").modal('hide');

                const rowToRemove = document.getElementById("row" + selectedIrrigacaoId);
                if (rowToRemove) {
                    rowToRemove.remove(); // Remove a linha do DOM
                } else {
                    console.error("Erro: Linha da tabela não encontrada para o ID " + selectedIrrigacaoId);
                }
            },
            error: function () {
                alert("Erro ao deletar irrigação.");
            }
        });
    });

});

// Configuração para carregar as informações ao abrir o modal
document.addEventListener('DOMContentLoaded', function () {
    const optionsModal = document.getElementById('optionsModal');
    if (!optionsModal) {
        console.error('Modal não encontrado!');
        return;
    }

    optionsModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget; // O botão que acionou o modal

        // Pega os dados do botão para preencher os campos do modal
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