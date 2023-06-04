package woowacourse.shopping.ui.order.history.recyclerview.adapter.history

import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.UiOrderResponse

class HistoryViewHolder(
    private val binding: ItemOrderHistoryBinding,
    private val onDetailListener: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(orderedProduct: UiOrderResponse) {
        binding.orderedProduct = orderedProduct
        binding.onHistoryDetailClickListener = onDetailListener
    }
}
