package woowacourse.shopping.ui.orderdetail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.ui.model.UiOrderItem

class OrderDetailAdapter : ListAdapter<UiOrderItem, OrderDetailViewHolder>(recentProductDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder =
        OrderDetailViewHolder(parent)

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val recentProductDiffUtil = object : DiffUtil.ItemCallback<UiOrderItem>() {
            override fun areItemsTheSame(oldItem: UiOrderItem, newItem: UiOrderItem):
                Boolean = oldItem.product.id == newItem.product.id

            override fun areContentsTheSame(oldItem: UiOrderItem, newItem: UiOrderItem):
                Boolean = oldItem == newItem
        }
    }
}
