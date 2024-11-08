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

document.addEventListener("DOMContentLoaded", function () {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' };

    document.querySelectorAll(".data-registro").forEach(cell => {
        const dataRegistro = cell.textContent;  // Pega o texto do campo
        if (dataRegistro) {
            const data = new Date(dataRegistro);  // Converte para um objeto Date
            cell.textContent = data.toLocaleDateString('pt-BR', options);  // Formata a data
        }
    });
});