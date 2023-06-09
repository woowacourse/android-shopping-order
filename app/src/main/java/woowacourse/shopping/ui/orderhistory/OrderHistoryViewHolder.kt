package woowacourse.shopping.ui.orderhistory

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryListBinding
import woowacourse.shopping.ui.model.OrderHistoryModel

class OrderHistoryViewHolder(
    private val binding: ItemOrderHistoryListBinding,
    onShowDetailListener: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.showOrderHistoryDetail.setOnClickListener { onShowDetailListener(binding.history!!.id) }
    }

    fun bind(history: OrderHistoryModel) {
        binding.history = history
    }
}