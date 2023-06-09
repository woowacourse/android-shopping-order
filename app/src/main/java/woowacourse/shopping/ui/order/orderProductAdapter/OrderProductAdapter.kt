package woowacourse.shopping.ui.order.orderProductAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.ui.order.orderProductAdapter.viewHolder.OrderProductViewHolder

class OrderProductAdapter : ListAdapter<CartProductUIModel, OrderProductViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CartProductUIModel>() {
            override fun areItemsTheSame(
                oldItem: CartProductUIModel,
                newItem: CartProductUIModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CartProductUIModel,
                newItem: CartProductUIModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
