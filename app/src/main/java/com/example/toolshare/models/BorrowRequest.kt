package com.example.toolshare.models

data class BorrowRequest(
    val id: String = "",
    val toolId: String = "",
    val toolName: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val ownerId: String = "",
    val message: String = "",
    val status: String = "pending",    // "pending", "approved", "rejected", "returned"
    val requestedAt: Long = System.currentTimeMillis()
)
