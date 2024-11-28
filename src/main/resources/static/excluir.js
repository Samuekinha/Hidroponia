function openModalUsuario(element){
    $("#idUsuario").val($(element).data('usuario-id'));
}

//document.addEventListener("DOMContentLoaded", function () {
//    document.querySelectorAll("#deleteButton").forEach(button => {
//        button.addEventListener("click", function () {
//            const userId = this.getAttribute("data-usuario-id"); // Pega o ID do botão
//            if (!userId) {
//                alert("ID do usuário não encontrado!");
//                return;
//            }
//
//            const confirmar = confirm("Tem certeza que deseja excluir este usuário?");
//            if (!confirmar) {
//                return;
//       }
//
//            fetch(`/usuario/excluir/${userId}`, {
//                method: "DELETE",
//                headers: {
//                    "Content-Type": "application/json"
//                }
//            })
//                .then(response => {
//                    if (response.ok) {
//                        alert("Usuário excluído com sucesso!");
//                        location.reload(); // Atualiza a página
//                    } else {
//                        alert("Erro ao excluir usuário.");
//                    }
//                })
//                .catch(error => console.error("Erro:", error));
//        });
//    });
//});