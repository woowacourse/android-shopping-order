package woowacourse.shopping.cart

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.cart.CartItem.ProductItem

class CartItemDiffCallback(
    private val oldList: List<CartItem>,
    private val newList: List<CartItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        if (oldItem is ProductItem && newItem is ProductItem) {
            return oldItem.productItem.id == newItem.productItem.id
        }
        return false
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
}
