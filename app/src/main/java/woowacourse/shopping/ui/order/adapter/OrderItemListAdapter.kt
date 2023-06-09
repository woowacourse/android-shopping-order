package woowacourse.shopping.ui.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.order.uistate.OrderItemUIState

class OrderItemListAdapter(
    private val onClickProduct: (Long) -> Unit
) : ListAdapter<OrderItemUIState, OrderItemViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder.create(parent, onClickProduct)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOrder(orderItems: List<OrderItemUIState>) {
        submitList(orderItems)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderItemUIState>() {
            override fun areItemsTheSame(
                oldItem: OrderItemUIState,
                newItem: OrderItemUIState
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: OrderItemUIState,
                newItem: OrderItemUIState
            ): Boolean = oldItem == newItem
        }
    }
}
