package com.skymilk.shoppingkt.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skymilk.shoppingkt.R
import com.skymilk.shoppingkt.databinding.ItemAddressBinding
import com.skymilk.shoppingkt.models.Address

class AddressAdapter :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    var onItemClick: ((Address) -> Unit)? = null
    var selectedAddress = -1

    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)
    }


    inner class AddressViewHolder(val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnAddress.setOnClickListener {
                if (selectedAddress >= 0) notifyItemChanged(selectedAddress)

                selectedAddress = adapterPosition
                notifyItemChanged(selectedAddress)
                onItemClick?.invoke(differ.currentList[adapterPosition])
            }
        }

        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                btnAddress.text = address.addressTitle

                btnAddress.background =
                    if (isSelected) ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                    else ColorDrawable(itemView.context.resources.getColor(R.color.white))
            }
        }
    }
}