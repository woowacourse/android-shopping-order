package woowacourse.shopping.presentation.view.orderlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.view.orderlist.viewholder.OrderListViewHolder

class OrderListAdapter(
    private val onClick: () -> Unit,
) : ListAdapter<OrderDetailModel, OrderListViewHolder>(orderDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(parent, onClick)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setItems(newItems: List<OrderDetailModel>) {
        submitList(newItems)
    }

    companion object {
        private val orderDiffUtil = object : DiffUtil.ItemCallback<OrderDetailModel>() {
            override fun areItemsTheSame(oldItem: OrderDetailModel, newItem: OrderDetailModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: OrderDetailModel, newItem: OrderDetailModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
