// Variável para controle da requisição
var isRequestInProgress = false;

// Função para carregar a lista de usuários
function carregarUsuarios() {
    if (isRequestInProgress) {
        return; // Se já houver uma requisição, não faz outra
    }

    isRequestInProgress = true;

    $.ajax({
        url: '/usuario/lista-usuario',
        type: 'GET',
        success: function(response) {
            $('#conteudo-usuario').html(response);
        },
        error: function() {
            alert('Erro ao carregar os usuários.');
        },
        complete: function() {
            isRequestInProgress = false;
        }
    });
}

// Carrega os usuários quando a página é carregada
carregarUsuarios();

// Configura o intervalo para recarregar os dados a cada 60 segundos
setInterval(function() {
    carregarUsuarios();
}, 60000);

// Função para atualizar dados de um usuário na tabela
function atualizarDadosTabela() {
    const nomeUsuario = document.getElementById("editUsername")?.value || '';
    const emailUsuario = document.getElementById("editEmail")?.value || '';

    const selectedUsuarioId = document.getElementById("selectedUsuarioId")?.value || '';

    $("#username" + selectedUsuarioId).text(nomeUsuario);
    $("#email" + selectedUsuarioId).text(emailUsuario);
}

// Função para salvar as alterações do usuário
document.addEventListener("DOMContentLoaded", function () {
    const saveButton = document.getElementById("saveButton");
    const deleteButton = document.getElementById("deleteButton");

    if (!saveButton) {
        console.error("Botão saveButton não encontrado!");
        return;
    }
    if (!deleteButton) {
        console.error("Botão deleteButton não encontrado!");
        return;
    }

    saveButton.addEventListener("click", function () {
        const nomeUsuario = document.getElementById("editUsername")?.value || '';
        const emailUsuario = document.getElementById("editEmail")?.value || '';
        const selectedUsuarioId = document.getElementById("selectedUsuarioId")?.value || '';

        // Validando os campos antes de enviar a requisição
        if (!nomeUsuario || !emailUsuario || !selectedUsuarioId) {
            alert("Todos os campos devem ser preenchidos.");
            return;
        }

        $.ajax({
            url: "/usuario/atualizar",
            method: "POST",
            data: {
                id: selectedUsuarioId,
                username: nomeUsuario,
                email: emailUsuario
            },
            success: function () {
                $("#optionsModal").modal('hide');
                $("#username" + selectedUsuarioId).text(nomeUsuario);
                $("#email" + selectedUsuarioId).text(emailUsuario);
            },
            error: function () {
                alert("Erro ao atualizar o usuário.");
            }
        });
    });

    // Excluir usuário
    deleteButton.addEventListener("click", function () {

