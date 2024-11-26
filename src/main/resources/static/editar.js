document.addEventListener('DOMContentLoaded', function () {
    // Função para abrir o modal e preencher os dados do usuário
    $('#editModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Botão que acionou o modal
        var usuarioId = button.data('usuario-id');
        var username = button.data('username');
        var email = button.data('email');
         var senha = button.data('senha');

        // Preenche os campos do modal
        $('#usuarioId').val(usuarioId);
        $('#editUsername').val(username);
        $('#editEmail').val(email);
        $('#editSenha').val(senha);
    });
});

const saveButton = document.getElementById("saveButton");

saveButton.addEventListener("click", function (event) {
    const idUsuario = document.getElementById("usuarioId").value || '';
    const nomeUsuario = document.getElementById("editUsername").value || '';
    const emailUsuario = document.getElementById("editEmail").value || '';
    const senha = document.getElementById("editSenha").value || '';  // Captura a senha
     // Captura a confirmação de senha

    // Validação de campos obrigatórios
    if (!nomeUsuario || !emailUsuario || !senha ) {
        alert("Todos os campos devem ser preenchidos.");
        event.preventDefault(); // Previne o envio do formulário caso haja erro
        return;
    }

    // Valida se as senhas coincidem


    // Fazendo a chamada AJAX para atualizar o usuário
    $.ajax({
        url: "/atualizarusuario", // URL de destino para a atualização
        method: "POST", // Método POST
        data: {
            id: idUsuario,
            username: nomeUsuario,
            email: emailUsuario,
            senha: senha,  // Envia a senha
             // Envia a confirmação da senha
        },
        success: function (response) {
            // Sucesso na atualização, pode exibir uma mensagem ou atualizar a página
            alert("Usuário atualizado com sucesso!");
            location.reload(); // Atualiza a página para refletir as mudanças
        },
        error: function () {
            // Erro na requisição
            alert("Erro ao atualizar o usuário.");
        }
    });
});
