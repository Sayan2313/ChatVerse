package com.example.chatverse.data

import com.example.chatverse.model.Models.Message
import com.example.chatverse.model.Models.ChatItem

object DemoData {
        val chats = listOf(
            ChatItem("c1", "Ayan", "Bro UI ready?", "12:10 PM"),
            ChatItem("c2", "College Group", "Lab tomorrow 9am", "11:46 AM"),
            ChatItem("c3", "Mom", "Reached home?", "Yesterday")
        )

        val messagesByChatId: Map<String, List<Message>> = mapOf(
            "c1" to listOf(
                Message("1", "How’s the chat app going?", "12:01 PM", fromMe = false),
                Message("2", "Compose UI done, Firebase later.", "12:02 PM", fromMe = true),
            ),
            "c2" to listOf(
                Message("1", "Reminder: DBMS lab 9am", "11:44 AM", fromMe = false),
                Message("2", "Got it.", "11:45 AM", fromMe = true),
            ),
            "c3" to listOf(
                Message("1", "Reached home?", "8:22 PM", fromMe = false),
                Message("2", "Yes ✅", "8:23 PM", fromMe = true),
            )
        )
    }
