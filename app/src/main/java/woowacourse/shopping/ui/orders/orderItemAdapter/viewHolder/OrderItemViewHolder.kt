package woowacourse.shopping.ui.order.orderProductAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemItemOrdersBinding
import woowacourse.shopping.model.OrderItemUIModel

class OrderItemViewHolder(
    private val binding: ItemItemOrdersBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: OrderItemUIModel) {
        binding.orderItem = product
    }

    companion object {
        fun from(parent: ViewGroup): OrderItemViewHolder {
            val binding = ItemItemOrdersBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrderItemViewHolder(binding)
        }
    }
}
