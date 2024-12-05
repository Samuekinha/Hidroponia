
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

const Toast = Swal.mixin({
  toast: true,
  position: "top-end",
  showConfirmButton: false,
  timer: 3000,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.onmouseenter = Swal.stopTimer;
    toast.onmouseleave = Swal.resumeTimer;
  }
});

Swal.fire({
    icon: 'success',
    title: 'Bem vindo!',
    text: 'Faça login antes de acessar nossos serviços.',
    showConfirmButton: false,
    timer: 6000,
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.onmouseenter = Swal.stopTimer;
        toast.onmouseleave = Swal.resumeTimer;
    }
});