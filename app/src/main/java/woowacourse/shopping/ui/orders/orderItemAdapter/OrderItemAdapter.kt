package woowacourse.shopping.ui.orders.orderItemAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderItemUIModel
import woowacourse.shopping.ui.orders.orderItemAdapter.viewHolder.OrderItemViewHolder

class OrderItemAdapter(
    private val onItemClick: (Int) -> Unit
) :
    ListAdapter<OrderItemUIModel, OrderItemViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder.from(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<OrderItemUIModel>() {
            override fun areItemsTheSame(
                oldItem: OrderItemUIModel,
                newItem: OrderItemUIModel
            ): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: OrderItemUIModel,
                newItem: OrderItemUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
