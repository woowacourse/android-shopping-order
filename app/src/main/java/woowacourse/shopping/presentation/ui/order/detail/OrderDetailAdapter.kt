package woowacourse.shopping.presentation.ui.order.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderedProductBinding
import woowacourse.shopping.domain.model.Order

class OrderDetailAdapter :
    ListAdapter<Order.OrderedProduct, OrderDetailAdapter.OrderDetailViewHolder>(
        OrderDetailComparator,
    ) {

    class OrderDetailViewHolder(val binding: ItemOrderedProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Order.OrderedProduct) {
            Glide.with(binding.imageOrderItem.context).load(data.product.imageUrl)
                .into(binding.imageOrderItem)
            binding.textOrderItemName.text = data.product.name
            binding.textOrderItemQuantity.text = data.quantity.toString()
            binding.textOrderItemPrice.text =
                binding.root.context.getString(R.string.detailPriceFormat, data.product.price)
            binding.textOrderItemTotalPrice.text = binding.root.context.getString(
                R.string.detailPriceFormat,
                data.product.price * data.quantity,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderedProductBinding.inflate(layoutInflater, parent, false)
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object OrderDetailComparator : DiffUtil.ItemCallback<Order.OrderedProduct>() {

        override fun areItemsTheSame(
            oldItem: Order.OrderedProduct,
            newItem: Order.OrderedProduct,
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: Order.OrderedProduct,
            newItem: Order.OrderedProduct,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
