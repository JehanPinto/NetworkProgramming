document.addEventListener('DOMContentLoaded', () => {
    const status = document.getElementById('status');
    const chatBox = document.getElementById('chat-box');
    const bidForm = document.getElementById('bid-form');
    const bidInput = document.getElementById('bid-input');

    const ws = new WebSocket('ws://localhost:8080');

    function addMessage(message, type = 'server-message') {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', type);
        messageElement.textContent = message;
        // Since the flex-direction is column-reverse, we use prepend to add to the top,
        // which appears as the bottom of the visible chat.
        chatBox.prepend(messageElement);
    }

    ws.onopen = () => {
        status.textContent = 'Connected';
        status.style.color = '#28a745';
        addMessage('Connected to the auction house.');
    };

    ws.onmessage = (event) => {
        addMessage(event.data);
    };

    ws.onclose = () => {
        status.textContent = 'Disconnected';
        status.style.color = '#dc3545';
        addMessage('Connection lost.');
        bidInput.disabled = true;
        bidForm.querySelector('button').disabled = true;
    };

    ws.onerror = (error) => {
        console.error('WebSocket Error:', error);
        status.textContent = 'Error';
        status.style.color = '#ffc107';
        addMessage('An error occurred with the connection.');
    };

    bidForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const bidValue = bidInput.value.trim();
        if (bidValue && ws.readyState === WebSocket.OPEN) {
            ws.send(bidValue);
            // Optionally show the user's own bid immediately
            // addMessage(`You bid: ${bidValue}`, 'bid-message');
            bidInput.value = '';
        }
    });
});
