package com.github.emresarincioglu.smsrouter.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.emresarincioglu.smsrouter.feature.home.databinding.RvSenderItemBinding
import com.github.emresarincioglu.smsrouter.feature.home.model.Sender

internal class SenderAdapter(
    private val senders: List<Sender>,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<SenderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RvSenderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = senders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sender = senders[position]
        holder.bind(sender, viewModel)
    }

    class ViewHolder(private val binding: RvSenderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sender: Sender, viewModel: HomeViewModel) {
            binding.sender = sender
            binding.viewModel = viewModel
        }
    }
}