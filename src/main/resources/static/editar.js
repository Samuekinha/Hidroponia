

var isRequestInProgress = false;

// Função para carregar as próximas irrigacoes
function carregarUsuarios() {
    // Verifica se já há uma requisição em andamento
    if (isRequestInProgress) {
        return; // Se já houver uma requisição, não faz outra
    }

    isRequestInProgress = true; // Marca a requisição como em andamento

    $.ajax({
        url: '/lista-usuario', // URL da requisição
        type: 'GET', // Método GET
        success: function(response) {
            $('#conteudo').html(response); // Atualiza a tabela com o conteúdo retornado
        },
        error: function() {
            alert('Erro ao carregar as próximas users.'); // Se der erro na requisição
        },
        complete: function() {
            isRequestInProgress = false; // Após a requisição ser completada, a flag é resetada
        }
    });
}

// Carrega as próximas irrigacoes quando a página é carregada
carregarUsuarios();


setInterval(function() {
    carregarUsuarios();
}, 60000);





// Função para salvar a irrigação
document.addEventListener("DOMContentLoaded", function () {
    const saveButton = document.getElementById("saveButton");

    if (!saveButton) {
        console.error("Botão saveButton não encontrado!");
        return;
    }

    saveButton.addEventListener("click", function () {
        const idUsuario = document.getElementById("id")?.value || '';
        const nomeUsuario = document.getElementById("username")?.value || '';
        const emailUsuario = document.getElementById("email")?.value || '';


        // Validando os campos antes de enviar a requisição
        if (!idUsuario || !nomeUsuario || !emailUsuario) {
            alert("Todos os campos devem ser preenchidos.");
            return;
        }

        console.log('Id Usuario:', idUsuario);
        console.log('Nome usuario:', nomeUsuario);
        console.log('Email usuario:', emailUsuario);

        // Fazendo a chamada AJAX para atualizar a irrigação
        $.ajax({
            url: "/lista-usuario",
            method: "POST",
            data: {
                id: idUsuario,
                usename: nomeUsuario,
                email: emailUsuario,
                intervalo: intervalo
            },

        });
    });

    document.addEventListener('DOMContentLoaded', function () {
        const optionsModal = document.getElementById('optionsModal');
        if (!optionsModal) {
            console.error('Modal não encontrado!');
            return;
        }

        optionsModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget; // O botão que acionou o modal

            // Pega os dados do botão para preencher os campos do modal
            const id = button.getAttribute('data-usuario-id');
            const username = button.getAttribute('data-username');
            const email = button.getAttribute('data-email');

            // Preenche os campos do modal com os dados do usuário
            document.getElementById('username').value = username || '';
            document.getElementById('email').value = email || '';

            // Atualiza o ID do usuário no campo escondido
            document.getElementById('selectedUsuarioId').value = id;
        });
    });

    // Limpeza do modal após ser fechado
    $("#optionsModal").on('hidden.bs.modal', function () {
        document.getElementById('username').value = '';
        document.getElementById('email').value = '';
        document.getElementById('selectedUsuarioId').value = '';
    });
