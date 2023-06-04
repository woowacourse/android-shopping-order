package woowacourse.shopping.ui.orderhistory

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryListBinding
import woowacourse.shopping.ui.model.OrderHistoryModel

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryListBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(history: OrderHistoryModel) {
        binding.history = history
    }
}