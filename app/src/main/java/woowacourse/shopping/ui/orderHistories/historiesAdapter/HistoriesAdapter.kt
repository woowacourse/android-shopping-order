package woowacourse.shopping.ui.orderHistories.historiesAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.ui.orderHistories.historiesAdapter.viewHolder.HistoriesViewHolder

class HistoriesAdapter(
    private val listener: HistoriesListener
) : ListAdapter<OrderHistoryUIModel, HistoriesViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriesViewHolder {
        return HistoriesViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: HistoriesViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderHistoryUIModel>() {
            override fun areItemsTheSame(
                oldItem: OrderHistoryUIModel,
                newItem: OrderHistoryUIModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OrderHistoryUIModel,
                newItem: OrderHistoryUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
