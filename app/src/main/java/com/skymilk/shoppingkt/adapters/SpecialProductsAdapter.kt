package com.skymilk.shoppingkt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.databinding.ItemSpecialBinding
import com.skymilk.shoppingkt.models.Product
import java.text.DecimalFormat

class SpecialProductsAdapter :
    RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    val decimal = DecimalFormat("#,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            ItemSpecialBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }


    inner class SpecialProductsViewHolder(val binding: ItemSpecialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnAddCart.setOnClickListener {

            }
        }

        fun bind(product: Product) {
            binding.apply {
                txtName.text = "$ ${product.name}"
                txtPrice.text = "${decimal.format(product.price)} 원"

                Glide.with(itemView).load(product.images[0]).into(imgProduct)
            }
        }
    }
}