package woowacourse.shopping.ui.orderHistories.historyAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderHistoryItemUIModel
import woowacourse.shopping.ui.orderHistories.historyAdapter.viewHolder.HistoryItemViewHolder

class HistoryItemAdapter(
    private val onItemClick: (Int) -> Unit
) :
    ListAdapter<OrderHistoryItemUIModel, HistoryItemViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        return HistoryItemViewHolder.from(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderHistoryItemUIModel>() {
            override fun areItemsTheSame(
                oldItem: OrderHistoryItemUIModel,
                newItem: OrderHistoryItemUIModel
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: OrderHistoryItemUIModel,
                newItem: OrderHistoryItemUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
