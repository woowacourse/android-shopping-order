package woowacourse.shopping.presentation.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.presentation.model.OrderProductModel

class OrderItemViewHolder(
    private val binding: ItemOrderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(orderProductModel: OrderProductModel) {
        binding.orderProduct = orderProductModel
    }
}
