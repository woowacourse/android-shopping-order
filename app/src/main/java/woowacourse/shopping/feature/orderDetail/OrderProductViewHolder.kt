package woowacourse.shopping.feature.orderDetail

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderProductBinding
import woowacourse.shopping.model.OrderProductUiModel

class OrderProductViewHolder(
    private val binding: ItemOrderProductBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(orderProduct: OrderProductUiModel) {
        binding.orderProduct = orderProduct
    }
}
