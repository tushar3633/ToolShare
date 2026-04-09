package com.example.toolshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toolshare.databinding.ItemRequestBinding
import com.example.toolshare.models.BorrowRequest

class RequestAdapter(
    private var requests: List<BorrowRequest>,
    private val onApprove: (BorrowRequest) -> Unit,
    private val onReject: (BorrowRequest) -> Unit,
    private val onDelete: (BorrowRequest) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(private val binding: ItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: BorrowRequest) {
            binding.tvToolName.text = request.toolName
            binding.tvRequesterName.text = "From: ${request.requesterName}"
            binding.tvMessage.text = if (request.message.isNotEmpty()) "\"${request.message}\"" else "No message"
            binding.tvStatus.text = request.status.uppercase()

            when (request.status) {
                "pending" -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_orange_dark))
                    binding.btnApprove.visibility = android.view.View.VISIBLE
                    binding.btnReject.visibility = android.view.View.VISIBLE
                    binding.btnDelete.visibility = android.view.View.GONE
                }
                "approved" -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                    binding.btnApprove.visibility = android.view.View.GONE
                    binding.btnReject.visibility = android.view.View.GONE
                    binding.btnDelete.visibility = android.view.View.VISIBLE
                }
                "rejected" -> {
                    binding.tvStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                    binding.btnApprove.visibility = android.view.View.GONE
                    binding.btnReject.visibility = android.view.View.GONE
                    binding.btnDelete.visibility = android.view.View.VISIBLE
                }
            }

            binding.btnApprove.setOnClickListener { onApprove(request) }
            binding.btnReject.setOnClickListener { onReject(request) }
            binding.btnDelete.setOnClickListener { onDelete(request) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount() = requests.size

    fun updateData(newRequests: List<BorrowRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }
}
