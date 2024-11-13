$(document).ready(function() {
    var isRequestInProgress = false; // Flag para verificar se a requisição está em andamento

    // Função para carregar as próximas irrigacoes
    function carregarProximasIrrigacoes() {
        if (isRequestInProgress) {
            return; // Se já houver uma requisição em andamento, não faz outra
        }

        isRequestInProgress = true; // Define a flag como verdadeira (requisição em andamento)

        $.ajax({
            url: '/agendar-irrigacao', // URL da requisição
            type: 'GET', // get
            success: function(response) {
                $('#conteudo-irrigacoes').html(response); // Atualiza a tabela
            },
            error: function() {
                alert('Erro ao carregar as próximas irrigações.');
            },
            complete: function() {
                isRequestInProgress = false; // Após a requisição ser completada, a flag é resetada
            }
        });
    }

    // Carrega as próximas irrigacoes quando a página é carregada
    carregarProximasIrrigacoes();

    // Configura o intervalo para recarregar as próximas irrigacoes a cada 60 segundos
    setInterval(function() {
        carregarProximasIrrigacoes(); // Chama a função para recarregar os dados
    }, 60000); // 60000 ms = 60 segundos
});


document.addEventListener("DOMContentLoaded", function () {
    const toggleDropdown = document.querySelector('.toggle-dropdown');
    const dropdownMenu = document.querySelector('.dropdown-menu');

    // Toggle a classe 'show' para exibir ou ocultar o menu dropdown
    toggleDropdown.addEventListener('click', function (event) {
        event.preventDefault(); // Impede o comportamento padrão do link
        dropdownMenu.classList.toggle('show');
    });

    // Fecha o dropdown ao clicar fora dele
    document.addEventListener('click', function (event) {
        if (!toggleDropdown.contains(event.target) && !dropdownMenu.contains(event.target)) {
            dropdownMenu.classList.remove('show');
        }
    });
});

let selectedIrrigacaoId;
function openOptionsModal(id) {
    selectedIrrigacaoId = id;

    // Corrige o uso do template string para o URL
    fetch(`/irrigacoes/${id}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById("dataIrrigacao").value = data.dataIrrigacao;
            document.getElementById("horaIrrigacao").value = data.horaIrrigacao;
            document.getElementById("intervalo").value = data.intervalo;
        })
        .catch(error => console.error("Erro ao carregar os dados:", error));

    // Abre a modal
    new bootstrap.Modal(document.getElementById("optionsModal")).show();
}

// Para salvar alterações
function saveChanges() {
    const data = {
        dataIrrigacao: document.getElementById("dataIrrigacao").value,
        horaIrrigacao: document.getElementById("horaIrrigacao").value,
        intervalo: document.getElementById("intervalo").value
    };

    fetch(`/irrigacoes/update/${selectedIrrigacaoId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            alert("Alterações salvas com sucesso!");
            location.reload();
        } else {
            alert("Erro ao salvar alterações.");
        }
    })
    .catch(error => console.error("Erro:", error));
}

// Para deletar uma irrigação
function deleteIrrigacao() {
    if (confirm("Tem certeza que deseja deletar este registro?")) {
        fetch(`/irrigacoes/delete/${selectedIrrigacaoId}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                alert("Registro deletado com sucesso!");
                location.reload();
            } else {
                alert("Erro ao deletar o registro.");
            }
        })
        .catch(error => console.error("Erro:", error));
    }
}