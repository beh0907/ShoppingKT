package com.skymilk.shoppingkt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skymilk.shoppingkt.databinding.ItemColorBinding

class ColorsAdapter :
    RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    private var selectedPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        holder.bind(differ.currentList[position], position)
    }


    inner class ColorsViewHolder(val binding: ItemColorBinding) :
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

        fun bind(color: Int, position: Int) {
            val imgDrawable = ColorDrawable(color)

            if (position == selectedPosition) {
                binding.apply {
                    imgShadow.visibility = View.VISIBLE
                    imgPicked.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imgShadow.visibility = View.INVISIBLE
                    imgPicked.visibility = View.INVISIBLE
                }
            }

            binding.imgColor.setImageDrawable(imgDrawable)
        }
    }
}