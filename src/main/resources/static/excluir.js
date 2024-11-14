 function confirmarExclusao(event) {
            var confirmar = confirm("Tem certeza que deseja excluir este usuário?");
            if (!confirmar) {
                event.preventDefault(); // Impede o envio do formulário se o usuário cancelar
            }
        }