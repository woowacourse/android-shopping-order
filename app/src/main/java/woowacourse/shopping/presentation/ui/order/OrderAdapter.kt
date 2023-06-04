package woowacourse.shopping.presentation.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.domain.model.Order

class OrderAdapter(
    private val onClick: (position: Int) -> Unit,
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderComparator) {

    class OrderViewHolder(
        val binding: ItemOrderBinding,
        private val onClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(absoluteAdapterPosition) }
        }

        fun bind(data: Order) {
            if (data.products.isEmpty()) return
            Glide.with(binding.imageOrderList).load(data.products.first().product.imageUrl)
                .into(binding.imageOrderList)
            binding.textOrderListName.text = getOrderName(data)
            binding.textOrderListDateTime.text = data.orderedDateTime
            binding.textOrderListTotalPrice.text = data.totalPrice.toString()
        }

        private fun getOrderName(data: Order): String {
            return if (data.products.size > 1) {
                binding.root.context.getString(
                    R.string.severalProducts,
                    data.products.first().product.name,
                    data.products.size - 1,
                )
            } else {
                data.products.first().product.name
            }
        }
    }

    object OrderComparator : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
        return OrderViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
