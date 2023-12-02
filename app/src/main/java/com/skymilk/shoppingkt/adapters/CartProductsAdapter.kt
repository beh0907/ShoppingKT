package com.skymilk.shoppingkt.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skymilk.shoppingkt.databinding.ItemCartProductBinding
import com.skymilk.shoppingkt.helper.getOfferPrice
import com.skymilk.shoppingkt.helper.toCommaString
import com.skymilk.shoppingkt.models.CartProduct
import java.text.DecimalFormat

class CartProductsAdapter :
    RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    var onItemClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            ItemCartProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
    }


    inner class CartProductsViewHolder(val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition])
            }

            binding.imgPlus.setOnClickListener { onPlusClick?.invoke(differ.currentList[adapterPosition]) }
            binding.imgMinus.setOnClickListener { onMinusClick?.invoke(differ.currentList[adapterPosition]) }
        }

        fun bind(cartProduct: CartProduct) {
            binding.apply {
                txtName.text = cartProduct.product.name
                txtQuantity.text = "${cartProduct.quantity}"

                val price =
                    cartProduct.product.offerPercentage.getOfferPrice(cartProduct.product.price)
                txtPrice.text = "${price.toCommaString()} 원"

                imgColor.setImageDrawable(
                    ColorDrawable(
                        cartProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                txtSize.text = cartProduct.selectedSize ?: "".also {
                    imgSize.setImageDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )
                }

                Glide.with(itemView).load(cartProduct.product.images[0]).into(imgProduct)
            }
        }
    }
}