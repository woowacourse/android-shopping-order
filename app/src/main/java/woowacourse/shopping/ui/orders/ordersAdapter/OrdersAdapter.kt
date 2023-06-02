package woowacourse.shopping.ui.orders.ordersAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.order.orderProductAdapter.viewHolder.OrdersyViewHolder

class OrdersAdapter : ListAdapter<OrderUIModel, OrdersyViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersyViewHolder {
        return OrdersyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersyViewHolder, position: Int) {
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
