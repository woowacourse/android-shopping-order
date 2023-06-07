package woowacourse.shopping.feature.order.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.OrderMinInfoItemUiModel

class OrderListAdapter(
    private val orderItemClickListener: OrderItemClickListener
) : ListAdapter<OrderMinInfoItemUiModel, OrderItemViewHolder>(OrderItemDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder.create(parent, orderItemClickListener)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOrderItems(list: List<OrderMinInfoItemUiModel>) {
        submitList(list)
    }

    companion object {
        private val OrderItemDiffUtil = object : DiffUtil.ItemCallback<OrderMinInfoItemUiModel>() {
            override fun areItemsTheSame(
                oldItem: OrderMinInfoItemUiModel,
                newItem: OrderMinInfoItemUiModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OrderMinInfoItemUiModel,
                newItem: OrderMinInfoItemUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
