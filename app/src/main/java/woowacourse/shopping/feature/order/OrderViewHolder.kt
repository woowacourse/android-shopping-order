package woowacourse.shopping.feature.order

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderBinding
import woowacourse.shopping.model.OrderUiModel

class OrderViewHolder(
    private val binding: ItemOrderBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(order: OrderUiModel) {
        binding.order = order
    }
}
