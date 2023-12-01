package com.skymilk.shoppingkt.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.databinding.ItemBestProductBinding
import com.skymilk.shoppingkt.models.Product
import java.text.DecimalFormat

class BestProductsAdapter :
    RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick : ((Product) -> Unit)? = null
    val decimal = DecimalFormat("#,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ItemBestProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }


    inner class BestProductsViewHolder(val binding: ItemBestProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition])
            }
        }

        fun bind(product: Product) {
            binding.apply {
                txtName.text = product.name
                txtOldPrice.text = "${decimal.format(product.price)} 원"


                //할인 정보가 있을 경우 반영하여 추가한다
                product.offerPercentage?.let {
                    val offerPrice = product.price * (1f - (it / 100f))
                    txtNewPrice.text = "${decimal.format(product.price)} 원"

                    //기존 가격에 취소선을 추가하고 새 가격을 표시한다
                    txtOldPrice.paintFlags = txtOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtNewPrice.visibility = View.VISIBLE
                }

                Glide.with(itemView).load(product.images[0]).into(imgProduct)
            }
        }
    }
}