package com.example.toolshare.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.toolshare.adapters.RequestAdapter
import com.example.toolshare.adapters.ToolAdapter
import com.example.toolshare.databinding.ActivityMainBinding
import com.example.toolshare.models.BorrowRequest
import com.example.toolshare.models.Tool
import com.example.toolshare.utils.FirebaseHelper
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolAdapter: ToolAdapter
    private lateinit var requestAdapter: RequestAdapter
    private var allTools: List<Tool> = emptyList()
    private var currentTab = "browse"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNav()
        setupSearchView()
        setupFab()

        loadBrowseTab()
    }

    override fun onResume() {
        super.onResume()
        when (currentTab) {
            "browse" -> loadBrowseTab()
            "my_tools" -> loadMyToolsTab()
            "requests" -> loadRequestsTab()
        }
    }

    private fun setupRecyclerView() {
        toolAdapter = ToolAdapter(emptyList()) { tool ->
            if (currentTab == "my_tools") {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Unlist Tool")
                    .setMessage("Do you want to unlist \"${tool.name}\"?")
                    .setPositiveButton("Unlist") { _, _ ->
                        lifecycleScope.launch {
                            val success = FirebaseHelper.deleteTool(tool.id, tool.ownerId)
                            if (success) {
                                Toast.makeText(this@MainActivity, "Tool unlisted!", Toast.LENGTH_SHORT).show()
                                loadMyToolsTab()
                            } else {
                                Toast.makeText(this@MainActivity, "Failed to unlist", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                val intent = Intent(this, ToolDetailActivity::class.java)
                intent.putExtra("toolId", tool.id)
                startActivity(intent)
            }
        }

        requestAdapter = RequestAdapter(
            emptyList(),
            onApprove = { request -> handleRequest(request, "approved") },
            onReject = { request -> handleRequest(request, "rejected") },
            onDelete = { request ->
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Request")
                    .setMessage("Remove this request from your list?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            val success = FirebaseHelper.deleteRequest(request.id)
                            if (success) {
                                Toast.makeText(this@MainActivity, "Request deleted!", Toast.LENGTH_SHORT).show()
                                loadRequestsTab()
                            } else {
                                Toast.makeText(this@MainActivity, "Failed to delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvTools.layoutManager = LinearLayoutManager(this)
        binding.rvTools.adapter = toolAdapter
    }

    private fun handleRequest(request: BorrowRequest, status: String) {
        lifecycleScope.launch {
            val success = FirebaseHelper.updateRequestStatus(request.id, status)
            if (success) {
                Toast.makeText(this@MainActivity, "Request ${status}!", Toast.LENGTH_SHORT).show()
                loadRequestsTab()
            } else {
                Toast.makeText(this@MainActivity, "Failed to update request", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.toolshare.R.id.nav_browse -> { currentTab = "browse"; loadBrowseTab(); true }
                com.example.toolshare.R.id.nav_my_tools -> { currentTab = "my_tools"; loadMyToolsTab(); true }
                com.example.toolshare.R.id.nav_requests -> { currentTab = "requests"; loadRequestsTab(); true }
                com.example.toolshare.R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java)); true
                }
                else -> false
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchTools(it) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) loadBrowseTab()
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddTool.setOnClickListener {
            startActivity(Intent(this, AddToolActivity::class.java))
        }
    }

    private fun loadBrowseTab() {
        binding.searchView.visibility = View.VISIBLE
        binding.fabAddTool.show()
        binding.rvTools.adapter = toolAdapter
        setLoading(true)
        lifecycleScope.launch {
            allTools = FirebaseHelper.getAllTools()
            toolAdapter.updateData(allTools)
            setLoading(false)
            binding.tvEmpty.visibility = if (allTools.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun loadMyToolsTab() {
        binding.searchView.visibility = View.GONE
        binding.fabAddTool.show()
        binding.rvTools.adapter = toolAdapter
        setLoading(true)
        lifecycleScope.launch {
            val tools = FirebaseHelper.getMyTools(FirebaseHelper.currentUserId)
            toolAdapter.updateData(tools)
            setLoading(false)
            binding.tvEmpty.visibility = if (tools.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun loadRequestsTab() {
        binding.searchView.visibility = View.GONE
        binding.fabAddTool.hide()
        binding.rvTools.adapter = requestAdapter
        setLoading(true)
        lifecycleScope.launch {
            val requests = FirebaseHelper.getRequestsForOwner(FirebaseHelper.currentUserId)
            requestAdapter.updateData(requests)
            setLoading(false)
            binding.tvEmpty.text = "No requests yet"
            binding.tvEmpty.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun searchTools(query: String) {
        setLoading(true)
        lifecycleScope.launch {
            val results = FirebaseHelper.searchTools(query)
            toolAdapter.updateData(results)
            setLoading(false)
            binding.tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
