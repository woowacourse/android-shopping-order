package woowacourse.shopping.presentation.view.order.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.order.viewholder.OrderProductViewHolder
import woowacourse.shopping.presentation.view.productlist.adpater.ViewType

class OrderProductAdapter : ListAdapter<CartModel, OrderProductViewHolder>(productDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(parent)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    override fun getItemViewType(position: Int): Int = ViewType.PRODUCT_LIST.ordinal

    fun setItems(newItems: List<CartModel>) {
        submitList(newItems)
    }

    companion object {
        private val productDiffUtil = object : DiffUtil.ItemCallback<CartModel>() {
            override fun areItemsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
                return oldItem.product.id == newItem.product.id
            }

            override fun areContentsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
