document.addEventListener("DOMContentLoaded", function() {
    const toggleDropdown = document.querySelector(".toggle-dropdown");
    const dropdownMenu = document.querySelector(".dropdown-menu");

    toggleDropdown.addEventListener("click", function() {
        // Alterna a classe 'show' para mostrar/ocultar o menu
        dropdownMenu.classList.toggle("show");
    });

    // Fecha o dropdown se o usuário clicar fora dele
    document.addEventListener("click", function(event) {
        if (!dropdownMenu.contains(event.target) && !toggleDropdown.contains(event.target)) {
            dropdownMenu.classList.remove("show");
        }
    });
});