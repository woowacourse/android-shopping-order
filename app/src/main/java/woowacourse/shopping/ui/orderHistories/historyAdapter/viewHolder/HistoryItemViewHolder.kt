package woowacourse.shopping.ui.orderHistories.historyAdapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemOrderHistoryBinding
import woowacourse.shopping.model.OrderHistoryItemUIModel

class HistoryItemViewHolder(
    private val binding: ItemOrderHistoryBinding,
    onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onProductClick = onItemClick
    }

    fun bind(product: OrderHistoryItemUIModel) {
        binding.orderItem = product
    }

    companion object {
        fun from(parent: ViewGroup, onItemClick: (Int) -> Unit): HistoryItemViewHolder {
            val binding = ItemOrderHistoryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return HistoryItemViewHolder(binding, onItemClick)
        }
    }
}
