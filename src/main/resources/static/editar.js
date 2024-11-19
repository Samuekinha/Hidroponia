

var isRequestInProgress = false;

// Função para carregar as próximas irrigacoes
function carregarProximasIrrigacoes() {
    // Verifica se já há uma requisição em andamento
    if (isRequestInProgress) {
        return; // Se já houver uma requisição, não faz outra
    }

    isRequestInProgress = true; // Marca a requisição como em andamento

    $.ajax({
        url: '/lista-usuario', // URL da requisição
        type: 'GET', // Método GET
        success: function(response) {
            $('#conteudo').html(response); // Atualiza a tabela com o conteúdo retornado
        },
        error: function() {
            alert('Erro ao carregar as próximas users.'); // Se der erro na requisição
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





// Função para salvar a irrigação
document.addEventListener("DOMContentLoaded", function () {
    const saveButton = document.getElementById("saveButton");

    if (!saveButton) {
        console.error("Botão saveButton não encontrado!");
        return;
    }

    saveButton.addEventListener("click", function () {
        const idUsuario = document.getElementById("id")?.value || '';
        const nomeUsuario = document.getElementById("username")?.value || '';
        const emailUsuario = document.getElementById("email")?.value || '';


        // Validando os campos antes de enviar a requisição
        if (!idUsuario || !nomeUsuario || !emailUsuario) {
            alert("Todos os campos devem ser preenchidos.");
            return;
        }

        console.log('Id Usuario:', idUsuario);
        console.log('Nome usuario:', nomeUsuario);
        console.log('email Usuario:', emailUsuario);
        console.log('ID selecionado:', selectedIrrigacaoId);

        // Fazendo a chamada AJAX para atualizar a irrigação
        $.ajax({
            url: "/lista-usuario",
            method: "POST",
            data: {
                id: selectedIrrigacaoId,
                usename: nomeUsuario,
                email: emailUsuario,
                intervalo: intervalo
            },
            success: function () {
                $("#optionsModal").modal('hide'); // Fecha o modal
                carregarProximasIrrigacoes(); // Atualiza a lista de irrigacoes
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
                    datairrigacao: dataIrrigacao,
                    horairrigacao: horaIrrigacao,
                    intervalo: intervalo
                },
                success: function () {
                    $("#optionsModal").modal('hide'); // Fecha o modal
                    carregarProximasIrrigacoes(); // Atualiza a lista de irrigacoes
                    $("#data"+selectedIrrigacaoId).text(dataIrrigacao);
                    $("#hora"+selectedIrrigacaoId).text(horaIrrigacao);
                    $("#dura"+selectedIrrigacaoId).text(intervalo);
                },
                error: function () {
                    alert("Erro ao atualizar irrigação.");
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

        document.getElementById('username').value = username || '';
        document.getElementById('email').value = email || '';


        document.getElementById('selectedUsuarioId').value = id; // Atualiza o ID escondido
    });
});

// Limpeza do modal após ser fechado
$("#optionsModal").on('hidden.bs.modal', function () {
    document.getElementById('username').value = '';
    document.getElementById('email').value = '';
    document.getElementById('selectedUsuarioId').value = '';
});