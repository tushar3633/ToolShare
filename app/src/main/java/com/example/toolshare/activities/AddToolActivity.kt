package com.example.toolshare.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.toolshare.R
import com.example.toolshare.databinding.ActivityAddToolBinding
import com.example.toolshare.models.Tool
import com.example.toolshare.utils.FirebaseHelper
import kotlinx.coroutines.launch

class AddToolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToolBinding

    private val categories = listOf("Hand Tools", "Power Tools", "Garden Tools",
        "Measuring Tools", "Electrical Tools", "Plumbing Tools", "Other")
    private val conditions = listOf("New", "Good", "Fair")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddToolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "List a Tool"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSpinners()

        binding.btnSubmit.setOnClickListener { submitTool() }
    }

    private fun setupSpinners() {
        binding.spinnerCategory.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, categories)

        binding.spinnerCondition.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, conditions)
    }

    private fun submitTool() {
        val name = binding.etToolName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val condition = binding.spinnerCondition.selectedItem.toString()

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            val currentUser = FirebaseHelper.getUser(FirebaseHelper.currentUserId)
            val tool = Tool(
                name = name,
                description = description,
                category = category,
                condition = condition,
                location = location,
                ownerId = FirebaseHelper.currentUserId,
                ownerName = currentUser?.name ?: "Unknown",
                isAvailable = true
            )

            val result = FirebaseHelper.addTool(tool)
            setLoading(false)
            result.onSuccess {
                Toast.makeText(this@AddToolActivity, "Tool listed successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this@AddToolActivity, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnSubmit.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
