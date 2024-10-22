const bottonLine = document.getElementById('bottonLine');

if (bottonLine) {
    bottonLine.addEventListener('mouseover', () => {
        console.log("Mouse over!");
        bottonLine.classList.add('hover');
    }); //garante que quando passe o mause no "bottonLine" faça uma animação

    bottonLine.addEventListener('mouseout', () => {
        console.log("Mouse out!");
        bottonLine.classList.remove('hover');
    }); //e quando tire o mause a animação saia
} else {
    console.log("Elemento não encontrado.");//em caso de erro
}

document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Impede o envio normal do formulário

    login(); // Chama a função login
});

function login() {
    let username = $("#username").val();
    let senha = $("#senha").val();

    $.ajax({
        url: "/login",
        method: "POST",
        contentType: "application/json",  // Envia como JSON
        data: JSON.stringify({
            username: username,
            senha: senha
        }),
        success: function(response) {
            alert("deu bom!");
        },
        error: function(xhr, status, error) {
            console.error("Erro no login: ", xhr.responseText);  // Log de erro mais detalhado
            alert("deu ruim");
        }
    });
}
