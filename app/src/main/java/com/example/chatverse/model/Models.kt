package com.example.chatverse.model

class Models {
    data class ChatItem(
        val id: String,
        val title: String,
        val lastMessage: String,
        val lastTime: String
    )

    data class Message(
        val id: String,
        val text: String,
        val time: String,
        val fromMe: Boolean
    )
}