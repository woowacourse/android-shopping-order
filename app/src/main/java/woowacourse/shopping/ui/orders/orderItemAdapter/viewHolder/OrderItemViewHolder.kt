package woowacourse.shopping.ui.orders.orderItemAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemItemOrdersBinding
import woowacourse.shopping.model.OrderItemUIModel

class OrderItemViewHolder(
    private val binding: ItemItemOrdersBinding,
    onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onProductClick = onItemClick
    }

    fun bind(product: OrderItemUIModel) {
        binding.orderItem = product
    }

    companion object {
        fun from(parent: ViewGroup, onItemClick: (Int) -> Unit): OrderItemViewHolder {
            val binding = ItemItemOrdersBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderItemViewHolder(binding, onItemClick)
        }
    }
}
