package com.example.toolshare.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.toolshare.databinding.ActivityProfileBinding
import com.example.toolshare.utils.FirebaseHelper
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadProfile()

        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun loadProfile() {
        setLoading(true)
        lifecycleScope.launch {
            val user = FirebaseHelper.getUser(FirebaseHelper.currentUserId)
            user?.let {
                binding.etName.setText(it.name)
                binding.etPhone.setText(it.phone)
                binding.etLocation.setText(it.location)
                binding.tvEmail.text = it.email
                binding.tvToolsListed.text = "Tools Listed: ${it.toolsListed}"
                binding.tvToolsBorrowed.text = "Tools Borrowed: ${it.toolsBorrowed}"
            }
            setLoading(false)
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existing = FirebaseHelper.getUser(FirebaseHelper.currentUserId)
            existing?.let {
                val updated = it.copy(name = name, phone = phone, location = location)
                val success = FirebaseHelper.updateUser(updated)
                if (success) Toast.makeText(this@ProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this@ProfileActivity, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        FirebaseHelper.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
