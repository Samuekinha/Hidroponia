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

