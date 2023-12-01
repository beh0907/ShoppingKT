package com.skymilk.shoppingkt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skymilk.shoppingkt.databinding.ItemSizeBinding

class SizesAdapter :
    RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            ItemSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        holder.bind(differ.currentList[position], position)
    }


    inner class SizesViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition)


                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)

                onItemClick?.invoke(differ.currentList[selectedPosition])
            }
        }

        fun bind(size: String, position: Int) {
            if (position == selectedPosition) {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                }
            }

            binding.txtSize.text = size
        }
    }
}