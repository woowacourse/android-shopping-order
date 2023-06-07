package woowacourse.shopping.feature.order.confirm

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import woowacourse.shopping.model.CartProductUiModel

class OrderConfirmAdapter :
    ListAdapter<CartProductUiModel, OrderConfirmViewHolder>(OrderConfirmDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderConfirmViewHolder {
        return OrderConfirmViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: OrderConfirmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOrderProducts(list: List<CartProductUiModel>) {
        submitList(list)
    }

    companion object {
        private val OrderConfirmDiffUtil = object : DiffUtil.ItemCallback<CartProductUiModel>() {
            override fun areItemsTheSame(
                oldItem: CartProductUiModel,
                newItem: CartProductUiModel
            ): Boolean {
                return oldItem.cartId == newItem.cartId
            }

            override fun areContentsTheSame(
                oldItem: CartProductUiModel,
                newItem: CartProductUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
