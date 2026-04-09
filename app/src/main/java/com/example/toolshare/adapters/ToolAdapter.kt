package com.example.toolshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toolshare.databinding.ItemToolBinding
import com.example.toolshare.models.Tool

class ToolAdapter(
    private var tools: List<Tool>,
    private val onItemClick: (Tool) -> Unit
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    inner class ToolViewHolder(private val binding: ItemToolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tool: Tool) {
            binding.tvToolName.text = tool.name
            binding.tvCategory.text = tool.category
            binding.tvCondition.text = tool.condition
            binding.tvOwner.text = tool.ownerName
            binding.tvLocation.text = tool.location
            binding.tvStatus.text = if (tool.isAvailable) "✅ Available" else "❌ Unavailable"
            binding.tvStatus.setTextColor(
                if (tool.isAvailable)
                    binding.root.context.getColor(android.R.color.holo_green_dark)
                else
                    binding.root.context.getColor(android.R.color.holo_red_dark)
            )
            binding.root.setOnClickListener { onItemClick(tool) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val binding = ItemToolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(tools[position])
    }

    override fun getItemCount() = tools.size

    fun updateData(newTools: List<Tool>) {
        tools = newTools
        notifyDataSetChanged()
    }
}
