package woowacourse.shopping.view.cart

import androidx.recyclerview.widget.DiffUtil

class CartDiffCallback(
    private val oldList: List<CartViewItem>,
    private val newList: List<CartViewItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is CartViewItem.CartProductItem && newItem is CartViewItem.CartProductItem) {
            return oldItem.product == newItem.product
        }
        if (oldItem is CartViewItem.PaginationItem && newItem is CartViewItem.PaginationItem) {
            return oldItem.status == newItem.status
        }
        return false
    }
}
