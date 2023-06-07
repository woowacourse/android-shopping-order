package woowacourse.shopping.presentation.myorder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemMyOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class MyOrderItemViewHolder(
    private val binding: ItemMyOrderBinding,
    showOrderDetail: (OrderModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var orderModel: OrderModel

    init {
        itemView.setOnClickListener { showOrderDetail(orderModel) }
    }

    fun bind(item: OrderModel) {
        orderModel = item
        binding.orderModel = orderModel
    }
}
