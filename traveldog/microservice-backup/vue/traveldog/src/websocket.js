// src/websocket.js

let socket;

export function initWebSocket(url) {
  socket = new WebSocket(url);

  socket.onopen = function() {
    console.log('WebSocket connection opened');
  };

  socket.onmessage = function(event) {
    console.log('WebSocket message received:', event.data);
    // Handle incoming messages here
  };

  socket.onclose = function() {
    console.log('WebSocket connection closed');
  };

  socket.onerror = function(error) {
    console.error('WebSocket error:', error);
  };
}

export function sendMessage(message) {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(message);
  } else {
    console.error('WebSocket is not open. Ready state:', socket.readyState);
  }
}

export function closeWebSocket() {
  if (socket) {
    socket.close();
  }
}