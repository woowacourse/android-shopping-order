package woowacourse.shopping.ui.orders.ordersAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orders.ordersAdapter.viewHolder.OrdersViewHolder

class OrdersAdapter(
    private val listener: OrdersListener
) : ListAdapter<OrderUIModel, OrdersViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderUIModel>() {
            override fun areItemsTheSame(
                oldItem: OrderUIModel,
                newItem: OrderUIModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OrderUIModel,
                newItem: OrderUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
