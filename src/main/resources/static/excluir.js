function openModalUsuario(element){
    $("#idUsuario").val($(element).data('usuario-id'));
}

document.addEventListener("DOMContentLoaded", function () {
    const deleteButton = document.getElementById("deleteButton");

    if (!deleteButton) {
        console.error("Botão deleteButton não encontrado!");
        return;
    }

    deleteButton.addEventListener("click", function () {
        const selectedUsuarioId = document.getElementById("selectedUsuarioId")?.value || '';

        // Validação para garantir que o ID foi selecionado
        if (!selectedUsuarioId) {
            alert("Nenhum usuário selecionado para exclusão.");
            return;
        }

        // Confirmação antes de excluir
        if (!confirm(`Tem certeza de que deseja excluir o usuário ID ${selectedUsuarioId}?`)) {
            return;
        }

        // Requisição AJAX para excluir o usuário
        $.ajax({
            url: `/usuario/excluir/${selectedUsuarioId}`, // URL configurada no backend
            method: "DELETE", // Método HTTP adequado
            success: function (response) {
                alert(response); // Mensagem de sucesso
                $("#optionsModal").modal('hide'); // Fechar modal (se aplicável)
                $("#row" + selectedUsuarioId).remove(); // Remover linha da tabela (se aplicável)
            },
            error: function () {
                alert("Erro ao excluir o usuário.");
            }
        });
    });
});