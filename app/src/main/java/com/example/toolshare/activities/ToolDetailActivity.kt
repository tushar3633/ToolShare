package com.example.toolshare.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.toolshare.databinding.ActivityToolDetailBinding
import com.example.toolshare.models.BorrowRequest
import com.example.toolshare.models.Tool
import com.example.toolshare.utils.FirebaseHelper
import kotlinx.coroutines.tasks.await

class ToolDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolDetailBinding
    private var tool: Tool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toolId = intent.getStringExtra("toolId") ?: run { finish(); return }
        loadTool(toolId)

        binding.btnRequest.setOnClickListener { sendBorrowRequest() }
    }

    private fun loadTool(toolId: String) {
        setLoading(true)
        lifecycleScope.launch {
            tool = FirebaseHelper.db.collection("tools").document(toolId)
                .get().await().toObject(Tool::class.java)

            tool?.let { displayTool(it) }
            setLoading(false)
        }
    }

    private fun displayTool(tool: Tool) {
        supportActionBar?.title = tool.name
        binding.tvToolName.text = tool.name
        binding.tvCategory.text = tool.category
        binding.tvCondition.text = "Condition: ${tool.condition}"
        binding.tvDescription.text = tool.description
        binding.tvOwner.text = "Listed by: ${tool.ownerName}"
        binding.tvLocation.text = "📍 ${tool.location}"

        val isOwner = tool.ownerId == FirebaseHelper.currentUserId
        binding.btnRequest.visibility = if (isOwner || !tool.isAvailable) View.GONE else View.VISIBLE
        binding.tvStatus.text = if (tool.isAvailable) "✅ Available" else "❌ Currently Borrowed"
    }

    private fun sendBorrowRequest() {
        val message = binding.etMessage.text.toString().trim()
        val t = tool ?: return

        lifecycleScope.launch {
            val currentUser = FirebaseHelper.getUser(FirebaseHelper.currentUserId)
            val request = BorrowRequest(
                toolId = t.id,
                toolName = t.name,
                requesterId = FirebaseHelper.currentUserId,
                requesterName = currentUser?.name ?: "Unknown",
                ownerId = t.ownerId,
                message = message
            )
            val result = FirebaseHelper.sendBorrowRequest(request)
            result.onSuccess {
                Toast.makeText(this@ToolDetailActivity, "Request sent!", Toast.LENGTH_SHORT).show()
                binding.btnRequest.isEnabled = false
                binding.btnRequest.text = "Request Sent"
            }.onFailure {
                Toast.makeText(this@ToolDetailActivity, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.scrollContent.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


}
