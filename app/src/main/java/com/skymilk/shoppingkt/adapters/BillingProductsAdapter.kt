package com.skymilk.shoppingkt.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.databinding.ItemBillingProductBinding
import com.skymilk.shoppingkt.helper.getOfferPrice
import com.skymilk.shoppingkt.helper.toCommaString
import com.skymilk.shoppingkt.models.CartProduct

class BillingProductsAdapter :
    RecyclerView.Adapter<BillingProductsAdapter.BillingProductsHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick: ((CartProduct) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsHolder {
        return BillingProductsHolder(
            ItemBillingProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingProductsHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }


    inner class BillingProductsHolder(val binding: ItemBillingProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition])
            }
        }

        fun bind(billingProduct: CartProduct) {
            binding.apply {
                txtName.text = billingProduct.product.name
                txtQuantity.text = billingProduct.quantity.toString()

                val price =
                    billingProduct.product.offerPercentage.getOfferPrice(billingProduct.product.price)
                txtPrice.text = "${price.toCommaString()} 원"

                imgColor.setImageDrawable(
                    ColorDrawable(
                        billingProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                txtSize.text = billingProduct.selectedSize ?: "".also {
                    imgSize.setImageDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )
                }

                Glide.with(itemView).load(billingProduct.product.images[0]).into(imgProduct)
            }
        }
    }
}