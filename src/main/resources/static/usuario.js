// Variável para controle da requisição
var isRequestInProgress = false;

// Função para carregar a lista de usuários
function carregarUsuarios() {
    if (isRequestInProgress) return; // Evita múltiplas requisições simultâneas
    isRequestInProgress = true;

    $.ajax({
        url: '/usuario/lista-usuario',
        type: 'GET',
        success: function (response) {
            $('#conteudo-usuario').html(response); // Atualiza a tabela
        },
        error: function () {
            alert('Erro ao carregar os usuários.');
        },
        complete: function () {
            isRequestInProgress = false;
        },
    });
}

// Função para abrir o modal e preencher os campos
function openModalUsuario(element) {
    const usuarioId = $(element).data('usuario-id');
    const username = $(element).data('username');
    const email = $(element).data('email');

    // Preenche os campos do modal
    $("#selectedUsuarioId").val(usuarioId);
    $("#editUsername").val(username);
    $("#editEmail").val(email);

    // Exibe o modal
    $("#optionsModal").modal('show');
}

// Função para excluir o usuário
function excluirUsuario() {
    const selectedUsuarioId = $("#selectedUsuarioId").val();

    if (!selectedUsuarioId) {
        alert("Nenhum usuário selecionado para exclusão.");
        return;
    }

    if (!confirm(`Tem certeza de que deseja excluir o usuário ID ${selectedUsuarioId}?`)) return;

    $.ajax({
        url: `/usuario/excluir/${selectedUsuarioId}`,
        method: "DELETE",
        success: function (response) {
            alert(response); // Exibe mensagem de sucesso
            $("#optionsModal").modal('hide'); // Fecha o modal
            $(`#row${selectedUsuarioId}`).remove(); // Remove a linha da tabela
        },
        error: function () {
            alert("Erro ao excluir o usuário.");
        },
    });
}

// Evento ao carregar a página
$(document).ready(function () {
    carregarUsuarios();

    // Configura o intervalo para recarregar a lista a cada 60 segundos
    setInterval(carregarUsuarios, 60000);

    // Configurar evento para o botão de exclusão
    $("#deleteButton").on("click", excluirUsuario);
});
