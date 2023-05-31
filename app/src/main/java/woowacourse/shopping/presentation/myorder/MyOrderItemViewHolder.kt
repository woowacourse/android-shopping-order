package woowacourse.shopping.presentation.myorder

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemMyOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class MyOrderItemViewHolder(
    private val binding: ItemMyOrderBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(orderModel: OrderModel) {
        binding.orderModel = orderModel
    }
}
