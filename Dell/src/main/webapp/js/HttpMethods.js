        const modal = document.getElementById("productModal");
        const btnAdd = document.getElementById("openModalBtn");
        const closeBtn = document.getElementsByClassName("close-btn")[0];
        const form = document.getElementById("productForm");
        const formTitle = document.querySelector('.modal-content h2');
        let currentOperation = 'POST';
        let currentProductId = null;
        modal.style.display = "none";

        function openModal(operation, productId) {
            currentOperation = operation;
            currentProductId = productId;
            form.reset();

            if (operation === 'POST') {
                formTitle.textContent = 'Adicionar Novo Produto';
            } else if (operation === 'PUT') {
                formTitle.textContent = 'Alterar Produto ID: '+productId ;
                const button = document.querySelector('.btn-warning[data-product-id="'+productId+'"]');

                if (button) {
                    const row = button.closest('tr');

                    const produtoNome = row.children[1].textContent.trim();
                    const produtoCategoria = row.children[2].textContent.trim();
                    const precoTexto = row.children[3].textContent.replace('R$ ', '').replace(',', '.').trim();

                    document.getElementById('produto').value = produtoNome;
                    document.getElementById('categoria').value = produtoCategoria;
                    document.getElementById('preco').value = parseFloat(precoTexto);
                }
            }
            modal.style.display = "flex";
        }
        btnAdd.addEventListener('click', () => openModal('POST', null));

        closeBtn.addEventListener('click', () => modal.style.display = "none");

        window.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });

        document.querySelectorAll('.btn-warning.btn-sm').forEach(button => {
            button.addEventListener('click', function() {
                const productId = this.getAttribute('data-product-id');
                openModal('PUT', productId);
            });
        });

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            let produtoData = {
                produto: document.getElementById('produto').value,
                categoria: document.getElementById('categoria').value,
                preco: parseFloat(document.getElementById('preco').value)
            };

            let method = currentOperation;
            let url = baseUrl + '/produto';

            if (method === 'PUT') {
                produtoData = {
                    id: currentProductId,
                    produto: document.getElementById('produto').value,
                    categoria: document.getElementById('categoria').value,
                    preco: parseFloat(document.getElementById('preco').value)
                };
            }

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(produtoData)
            })
            .then(response => {
                const acao = (method === 'POST') ? 'cadastrado' : 'alterado';

                if (response.ok) {
                    return response.text().then(responseText => {
                        alert(`Produto ${acao} com sucesso! Mensagem: ${responseText}`);
                        modal.style.display = "none";
                        window.location.reload();
                    });
                } else {
                    return response.text().then(errorText => {
                        alert(`Erro ao ${acao} produto: ${response.status}. Detalhes: ${errorText}`);
                    });
                }
            })
            .catch(error => {
                console.error(`Erro de rede/comunicação (${method}):`, error);
                alert('Ocorreu um erro ao conectar ao servidor.');
            });
        });

        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', function() {
                const productId = this.getAttribute('data-product-id');
                if (confirm(`Tem certeza que deseja excluir o produto com ID ${productId}?`)) {

                    const deleteUrl = baseUrl + '/produto?id=' + productId;

                    fetch(deleteUrl, {
                        method: 'DELETE'
                    })
                    .then(response => {
                        if (response.ok) {
                            return response.text().then(responseText => {
                                alert(`Produto com ID ${productId} excluído com sucesso! Mensagem: ${responseText}`);
                                window.location.reload();
                            });
                        } else {
                            return response.text().then(errorText => {
                                alert(`Erro ao excluir produto ID ${productId}: ${response.status}. Detalhes: ${errorText}`);
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Erro de rede/comunicação (DELETE):', error);
                        alert('Ocorreu um erro ao tentar excluir o produto.');
                    });
                }
            });
        });