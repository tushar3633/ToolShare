package com.example.toolshare.models

data class Tool(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val condition: String = "",        // "New", "Good", "Fair"
    val ownerId: String = "",
    val ownerName: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val borrowedBy: String = "",       // userId of current borrower
    val location: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
