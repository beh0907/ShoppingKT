package com.skymilk.shoppingkt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skymilk.shoppingkt.databinding.ItemOrderBinding
import com.skymilk.shoppingkt.models.Order
import com.skymilk.shoppingkt.models.getOrderStatus
import com.skymilk.shoppingkt.models.getOrderStatusColor

class AllOrdersAdapter :
    RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick: ((Order) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    inner class OrdersViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(differ.currentList[adapterPosition])
            }
        }

        fun bind(order: Order) {
            binding.apply {
                txtOrderId.text = "${order.orderId}"
                txtOrderDateTime.text = order.dateTime

                val colorDrawable = getOrderStatusColor(getOrderStatus(order.orderStatus), itemView.resources)
                imgOrderState.setImageDrawable(colorDrawable)

            }
        }
    }
}