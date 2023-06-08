package woowacourse.shopping.ui.orderhistory

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.model.UiOrder

class OrderHistoryAdapter(private val onItemClick: (UiOrder) -> Unit) :
    ListAdapter<UiOrder, OrderHistoryViewHolder>(recentProductDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder =
        OrderHistoryViewHolder(parent, onItemClick)

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val recentProductDiffUtil = object : DiffUtil.ItemCallback<UiOrder>() {
            override fun areItemsTheSame(oldItem: UiOrder, newItem: UiOrder):
                Boolean = oldItem.orderId == newItem.orderId

            override fun areContentsTheSame(oldItem: UiOrder, newItem: UiOrder):
                Boolean = oldItem == newItem
        }
    }
}
