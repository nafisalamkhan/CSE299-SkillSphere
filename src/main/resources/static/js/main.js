'use strict';

const loginPage = document.querySelector('#login');
const chatPage = document.querySelector('#chat-page');
const loginForm = document.querySelector('#loginForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

const

let stompClient = null;
let username = null;
let password = null;
let selectedUser = null;

function connect(event){
    username = document.querySelector('#username');
    password = document.querySelector('#password');
    if(username && password){

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected(){

}

