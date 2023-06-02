package woowacourse.shopping.ui.order.orderProductAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrdersBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orders.orderItemAdapter.OrderItemAdapter

class OrdersyViewHolder(
    private val binding: ItemOrdersBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val adapter = OrderItemAdapter()

    fun bind(product: OrderUIModel) {
        adapter.submitList(product.orderItems)
        binding.rvProducts.adapter = adapter
        binding.tvOrderHistoryDate.text = product.orderDate
    }

    companion object {
        fun from(parent: ViewGroup): OrdersyViewHolder {
            val binding = ItemOrdersBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return OrdersyViewHolder(binding)
        }
    }
}
