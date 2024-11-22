document.addEventListener('DOMContentLoaded', function () {
    // Função para abrir o modal e preencher os dados do usuário
    function editModal(element) {
        // Exibe o modal
        $('#editModal').modal('show');
    };

    // Limpeza do modal após ser fechado
    $('#editModal').on('hidden.bs.modal', function () {
        document.getElementById('editUsername').value = '';
        document.getElementById('editEmail').value = '';
        document.getElementById('usuarioId').value = '';
    });

    // Função para validar e salvar a edição do usuário
    const saveButton = document.getElementById("saveButton");

    if (!saveButton) {
        console.error("Botão saveButton não encontrado!");
        return;
    }

    saveButton.addEventListener("click", function (event) {
        const idUsuario = document.getElementById("usuarioId").value || '';
        const nomeUsuario = document.getElementById("editUsername").value || '';
        const emailUsuario = document.getElementById("editEmail").value || '';
        const senha = document.getElementById("editSenha").value || '';
        const confSenha = document.getElementById("editConfSenha").value || '';

        // Validação de campos obrigatórios
        if (!nomeUsuario || !emailUsuario || !senha || !confSenha) {
            alert("Todos os campos devem ser preenchidos.");
            event.preventDefault(); // Previne o envio do formulário caso haja erro
            return;
        }

        // Valida se as senhas coincidem
        if (senha !== confSenha) {
            alert("As senhas não coincidem!");
            event.preventDefault(); // Previne o envio do formulário caso haja erro
            return;
        }

        // Fazendo a chamada AJAX para atualizar o usuário
        $.ajax({
            url: "/atualizarusuario", // URL de destino para a atualização
            method: "POST", // Método POST
            data: {
                id: idUsuario,
                username: nomeUsuario,
                email: emailUsuario,
                senha: senha,
                conf_senha: confSenha
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
});