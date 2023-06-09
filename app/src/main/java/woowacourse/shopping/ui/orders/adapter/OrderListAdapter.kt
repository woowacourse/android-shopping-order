package woowacourse.shopping.ui.orders.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.orders.uistate.OrdersItemUIState

class OrderListAdapter(
    private val onClickOrder: (Long) -> Unit
) : ListAdapter<OrdersItemUIState, OrderListViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder.create(parent, onClickOrder)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOrders(orders: List<OrdersItemUIState>) {
        submitList(orders)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrdersItemUIState>() {
            override fun areItemsTheSame(
                oldItem: OrdersItemUIState,
                newItem: OrdersItemUIState
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: OrdersItemUIState,
                newItem: OrdersItemUIState
            ): Boolean = oldItem == newItem
        }
    }
}
