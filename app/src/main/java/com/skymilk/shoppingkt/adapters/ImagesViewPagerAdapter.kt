package com.skymilk.shoppingkt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.databinding.ItemProductImageBinding

class ImagesViewPagerAdapter() :
    RecyclerView.Adapter<ImagesViewPagerAdapter.ImagesViewPagerViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagesViewPagerViewHolder {

        return ImagesViewPagerViewHolder(
            ItemProductImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ImagesViewPagerViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    inner class ImagesViewPagerViewHolder(val binding: ItemProductImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(itemView).load(imageUrl).into(binding.imgProduct)
        }
    }
}