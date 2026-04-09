package com.example.toolshare.utils

import com.example.toolshare.models.BorrowRequest
import com.example.toolshare.models.Tool
import com.example.toolshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseHelper {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUserId get() = auth.currentUser?.uid ?: ""

    // ─── Auth ────────────────────────────────────────────────────────────────

    suspend fun registerUser(email: String, password: String, name: String, phone: String, location: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user!!.uid
            val user = User(uid = uid, name = name, email = email, phone = phone, location = location)
            db.collection("users").document(uid).set(user).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()

    // ─── Users ───────────────────────────────────────────────────────────────

    suspend fun getUser(uid: String): User? {
        return try {
            db.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            db.collection("users").document(user.uid).set(user).await()
            true
        } catch (e: Exception) { false }
    }

    // ─── Tools ───────────────────────────────────────────────────────────────

    suspend fun addTool(tool: Tool): Result<String> {
        return try {
            val ref = db.collection("tools").document()
            val newTool = tool.copy(id = ref.id)
            ref.set(newTool).await()
            val user = getUser(tool.ownerId)
            user?.let {
                updateUser(it.copy(toolsListed = it.toolsListed + 1))
            }
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllTools(): List<Tool> {
        return try {
            db.collection("tools")
                .get().await()
                .toObjects(Tool::class.java)
                .filter { it.isAvailable }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun searchTools(query: String): List<Tool> {
        return try {
            db.collection("tools").get().await()
                .toObjects(Tool::class.java)
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getMyTools(ownerId: String): List<Tool> {
        return try {
            db.collection("tools")
                .whereEqualTo("ownerId", ownerId)
                .get().await()
                .toObjects(Tool::class.java)
        } catch (e: Exception) { emptyList() }
    }

    suspend fun deleteTool(toolId: String, ownerId: String): Boolean {
        return try {
            db.collection("tools").document(toolId).delete().await()
            val user = getUser(ownerId)
            user?.let {
                val newCount = if (it.toolsListed > 0) it.toolsListed - 1 else 0
                updateUser(it.copy(toolsListed = newCount))
            }
            true
        } catch (e: Exception) { false }
    }

    // ─── Borrow Requests ─────────────────────────────────────────────────────

    suspend fun sendBorrowRequest(request: BorrowRequest): Result<String> {
        return try {
            val ref = db.collection("requests").document()
            val newReq = request.copy(id = ref.id)
            ref.set(newReq).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRequestsForOwner(ownerId: String): List<BorrowRequest> {
        return try {
            db.collection("requests")
                .whereEqualTo("ownerId", ownerId)
                .get().await()
                .toObjects(BorrowRequest::class.java)
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getRequestsByUser(userId: String): List<BorrowRequest> {
        return try {
            db.collection("requests")
                .whereEqualTo("requesterId", userId)
                .get().await()
                .toObjects(BorrowRequest::class.java)
        } catch (e: Exception) { emptyList() }
    }

    suspend fun updateRequestStatus(requestId: String, status: String): Boolean {
        return try {
            db.collection("requests").document(requestId)
                .update("status", status).await()

            if (status == "approved") {
                val snapshot = db.collection("requests")
                    .document(requestId).get().await()

                val toolId = snapshot.getString("toolId") ?: ""
                val requesterId = snapshot.getString("requesterId") ?: ""

                android.util.Log.d("TOOLSHARE", "Approving - toolId: $toolId, requesterId: $requesterId")

                if (toolId.isNotEmpty()) {
                    db.collection("tools").document(toolId)
                        .update(
                            mapOf(
                                "isAvailable" to false,
                                "borrowedBy" to requesterId
                            )
                        ).await()
                    android.util.Log.d("TOOLSHARE", "Tool updated successfully")
                } else {
                    android.util.Log.d("TOOLSHARE", "toolId was empty!")
                }
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("TOOLSHARE", "Error: ${e.message}")
            false
        }
    }

    suspend fun deleteRequest(requestId: String): Boolean {
        return try {
            db.collection("requests").document(requestId).delete().await()
            true
        } catch (e: Exception) { false }
    }
}
