document.addEventListener('DOMContentLoaded', () => {
    let ws = null;
    let isAdmin = false;
    let timerInterval = null;

    // Login Screen Elements
    const loginScreen = document.getElementById('login-screen');
    const adminRoleBtn = document.getElementById('admin-role-btn');
    const userRoleBtn = document.getElementById('user-role-btn');
    const adminLoginForm = document.getElementById('admin-login-form');
    const adminCredentialsForm = document.getElementById('admin-credentials-form');
    const backToRolesBtn = document.getElementById('back-to-roles');
    const loginError = document.getElementById('login-error');

    // Admin Dashboard Elements
    const adminDashboard = document.getElementById('admin-dashboard');
    const startingPrice = document.getElementById('starting-price');
    const auctionDuration = document.getElementById('auction-duration');
    const startAuctionBtn = document.getElementById('start-auction-btn');
    const auctionTimer = document.getElementById('auction-timer');
    const timerDisplay = document.getElementById('timer-display');
    const adminItemName = document.getElementById('admin-item-name');
    const adminCurrentPrice = document.getElementById('admin-current-price');
    const adminHighestBidder = document.getElementById('admin-highest-bidder');
    const adminBidList = document.getElementById('admin-bid-list');

    // User Interface Elements
    const userInterface = document.getElementById('user-interface');
    const userNumber = document.getElementById('user-number');
    const connectionStatus = document.getElementById('connection-status');
    const itemName = document.getElementById('item-name');
    const currentPrice = document.getElementById('current-price');
    const highestBidder = document.getElementById('highest-bidder');
    const timeRemaining = document.getElementById('time-remaining');
    const bidList = document.getElementById('bid-list');
    const bidForm = document.getElementById('bid-form');
    const bidInput = document.getElementById('bid-input');
    const bidSubmitBtn = document.getElementById('bid-submit-btn');

    // Role Selection
    adminRoleBtn.addEventListener('click', () => {
        document.querySelector('.role-selection').classList.add('hidden');
        adminLoginForm.classList.remove('hidden');
    });

    userRoleBtn.addEventListener('click', () => {
        isAdmin = false;
        connectAsUser();
    });

    backToRolesBtn.addEventListener('click', () => {
        adminLoginForm.classList.add('hidden');
        loginError.classList.add('hidden');
        document.querySelector('.role-selection').classList.remove('hidden');
        document.getElementById('admin-username').value = '';
        document.getElementById('admin-password').value = '';
    });

    // Admin Login
    adminCredentialsForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const username = document.getElementById('admin-username').value;
        const password = document.getElementById('admin-password').value;
        connectAsAdmin(username, password);
    });

    function connectAsAdmin(username, password) {
        ws = new WebSocket('ws://localhost:8080');
        
        ws.onopen = () => {
            ws.send(`ADMIN_LOGIN:${username}:${password}`);
        };

        ws.onmessage = (event) => {
            const message = event.data;
            
            if (message === 'ADMIN_AUTH_SUCCESS') {
                isAdmin = true;
                loginScreen.classList.add('hidden');
                adminDashboard.classList.remove('hidden');
                requestStatus();
                startStatusPolling();
            } else if (message === 'ADMIN_AUTH_FAILED') {
                loginError.classList.remove('hidden');
            } else {
                handleAdminMessage(message);
            }
        };

        ws.onerror = () => {
            alert('Connection error. Make sure the backend is running.');
        };
    }

    function connectAsUser() {
        ws = new WebSocket('ws://localhost:8080');
        
        ws.onopen = () => {
            connectionStatus.textContent = 'Connected';
            connectionStatus.style.color = '#28a745';
            ws.send('REQUEST_USER_NUMBER');
            ws.send('REQUEST_STATUS');
        };

        ws.onmessage = (event) => {
            handleUserMessage(event.data);
        };

        ws.onclose = () => {
            connectionStatus.textContent = 'Disconnected';
            connectionStatus.style.color = '#dc3545';
            bidSubmitBtn.disabled = true;
            clearInterval(timerInterval);
        };

        ws.onerror = () => {
            connectionStatus.textContent = 'Error';
            connectionStatus.style.color = '#ffc107';
        };

        loginScreen.classList.add('hidden');
        userInterface.classList.remove('hidden');
    }

    function handleAdminMessage(message) {
        if (message.startsWith('STATUS:')) {
            const parts = message.split(':');
            adminItemName.textContent = parts[1];
            adminCurrentPrice.textContent = '$' + parseFloat(parts[2]).toFixed(2);
            adminHighestBidder.textContent = parts[3];
        } else if (message.startsWith('BID_UPDATE:')) {
            const parts = message.split(':');
            const bidder = parts[1];
            const price = parseFloat(parts[2]);
            const winner = parts[3];
            
            adminCurrentPrice.textContent = '$' + price.toFixed(2);
            adminHighestBidder.textContent = winner;
            
            addBidToAdminList(bidder, price);
        } else if (message.startsWith('TIMER_UPDATE:')) {
            const seconds = parseInt(message.split(':')[1]);
            updateTimerDisplay(seconds, timerDisplay);
        } else if (message.startsWith('AUCTION_ENDED:')) {
            const parts = message.split(':');
            const winner = parts[1];
            const finalPrice = parts[2];
            alert(`Auction Ended!\nWinner: ${winner}\nFinal Price: $${finalPrice}`);
            clearInterval(timerInterval);
        }
    }

    function handleUserMessage(message) {
        if (message.startsWith('USER_NUMBER:')) {
            const number = message.split(':')[1];
            userNumber.textContent = number;
        } else if (message.startsWith('STATUS:')) {
            const parts = message.split(':');
            itemName.textContent = parts[1];
            currentPrice.textContent = '$' + parseFloat(parts[2]).toFixed(2);
            highestBidder.textContent = parts[3];
            const hasStarted = parts[4] === 'true';
            bidSubmitBtn.disabled = !hasStarted;
        } else if (message.startsWith('BID_UPDATE:')) {
            const parts = message.split(':');
            const bidder = parts[1];
            const price = parseFloat(parts[2]);
            const winner = parts[3];
            
            currentPrice.textContent = '$' + price.toFixed(2);
            highestBidder.textContent = winner;
            
            addBidToList(bidder, price, bidder === userNumber.textContent);
        } else if (message.startsWith('AUCTION_STARTED:')) {
            const duration = parseInt(message.split(':')[1]);
            bidSubmitBtn.disabled = false;
            timeRemaining.textContent = formatTime(duration);
            addSystemMessage('Auction has started!');
        } else if (message.startsWith('TIMER_UPDATE:')) {
            const seconds = parseInt(message.split(':')[1]);
            timeRemaining.textContent = formatTime(seconds);
        } else if (message.startsWith('AUCTION_ENDED:')) {
            const parts = message.split(':');
            const winner = parts[1];
            const finalPrice = parts[2];
            bidSubmitBtn.disabled = true;
            timeRemaining.textContent = 'Ended';
            addSystemMessage(`Auction Ended! Winner: ${winner} - $${finalPrice}`);
        } else if (message.startsWith('ERROR:')) {
            const error = message.substring(6);
            addSystemMessage(error, 'error');
        }
    }

    function addBidToList(bidder, price, isOwnBid) {
        const bidElement = document.createElement('div');
        bidElement.classList.add('bid-item');
        if (isOwnBid) bidElement.classList.add('own-bid');
        
        bidElement.innerHTML = `
            <span class="bid-user">${bidder}</span>
            <span class="bid-amount">$${price.toFixed(2)}</span>
        `;
        bidList.insertBefore(bidElement, bidList.firstChild);
    }

    function addBidToAdminList(bidder, price) {
        if (adminBidList.querySelector('.no-bids')) {
            adminBidList.innerHTML = '';
        }
        
        const bidElement = document.createElement('div');
        bidElement.classList.add('bid-item');
        bidElement.innerHTML = `
            <span class="bid-user">${bidder}</span>
            <span class="bid-amount">$${price.toFixed(2)}</span>
            <span class="bid-time">${new Date().toLocaleTimeString()}</span>
        `;
        adminBidList.insertBefore(bidElement, adminBidList.firstChild);
    }

    function addSystemMessage(message, type = 'info') {
        const msgElement = document.createElement('div');
        msgElement.classList.add('system-message', type);
        msgElement.textContent = message;
        bidList.insertBefore(msgElement, bidList.firstChild);
    }

    function formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    function updateTimerDisplay(seconds, element) {
        element.textContent = formatTime(seconds);
    }

    function requestStatus() {
        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send('REQUEST_STATUS');
        }
    }

    function startStatusPolling() {
        setInterval(() => {
            if (isAdmin && ws && ws.readyState === WebSocket.OPEN) {
                requestStatus();
            }
        }, 2000);
    }

    // Admin Start Auction
    startAuctionBtn.addEventListener('click', () => {
        const duration = parseInt(auctionDuration.value);
        const price = parseFloat(startingPrice.value);
        
        if (duration < 10 || duration > 600) {
            alert('Please enter a duration between 10 and 600 seconds');
            return;
        }
        
        if (price < 1) {
            alert('Please enter a starting price of at least $1.00');
            return;
        }
        
        ws.send(`START_AUCTION:${duration}:${price}`);
        startAuctionBtn.disabled = true;
        auctionDuration.disabled = true;
        startingPrice.disabled = true;
        auctionTimer.classList.remove('hidden');
        timerDisplay.textContent = formatTime(duration);
    });

    // User Bid Submission
    bidForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const bidValue = bidInput.value.trim();
        if (bidValue && ws.readyState === WebSocket.OPEN) {
            ws.send(bidValue);
            bidInput.value = '';
        }
    });
});
