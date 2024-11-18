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

// Controla a exibição do modal ao clicar no botão de opções
const openModalBtn = document.getElementById('openModalBtn');
const exampleModal = new bootstrap.Modal(document.getElementById('exampleModal'));

// Abrir o modal apenas quando clicar nas opções
openModalBtn.addEventListener('click', function() {
    exampleModal.show();
});

// Modal de confirmação de ação (Salvar ou Deletar)
const saveBtn = document.getElementById('saveBtn');
const deleteBtn = document.getElementById('deleteBtn');
const confirmModal = new bootstrap.Modal(document.getElementById('confirmModal'));
const confirmMessage = document.getElementById('confirmMessage');
const confirmBtn = document.getElementById('confirmBtn');

let actionType = ''; // Variável para controlar a ação de confirmação (salvar ou deletar)

// Lógica para "Salvar alterações"
saveBtn.addEventListener('click', function() {
    actionType = 'save';
    confirmMessage.innerHTML = 'Tem certeza de que deseja salvar as alterações?';
    confirmModal.show();
});

// Lógica para "Deletar"
deleteBtn.addEventListener('click', function() {
    actionType = 'delete';
    confirmMessage.innerHTML = 'Tem certeza de que deseja deletar essa irrigação?';
    confirmModal.show();
});

// Confirmar a ação
confirmBtn.addEventListener('click', function() {
    if (actionType === 'save') {
        saveChanges();
    } else if (actionType === 'delete') {
        deleteIrrigacao();
    }
    confirmModal.hide(); // Fechar o modal de confirmação
});
