package com.example.toolshare.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val profileImageUrl: String = "",
    val toolsListed: Int = 0,
    val toolsBorrowed: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
