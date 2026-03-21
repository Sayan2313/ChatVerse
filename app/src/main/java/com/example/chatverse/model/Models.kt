package com.example.chatverse.model

object Models {
    data class User(
        val uid: String = "",
        val name: String? = "",
        val email: String? = "",
        val photoUrl: String? = ""
    )

    data class ChatItem(
        val id: String = "",
        val title: String = "",
        val lastMessage: String = "",
        val lastTime: String = "",
        val otherUserId: String = ""
    )

    data class Message(
        val id: String = "",
        val text: String = "",
        val time: String = "",
        val fromMe: Boolean = false,
        val senderId: String = "",
        val timestamp: Long = 0
    )
}
