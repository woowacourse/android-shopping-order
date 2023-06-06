package woowacourse.shopping.view.productlist

import androidx.recyclerview.widget.DiffUtil

class ProductListDiffCallback(
    private val oldList: List<ProductListViewItem>,
    private val newList: List<ProductListViewItem>,
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
        if (oldItem is ProductListViewItem.ProductItem && newItem is ProductListViewItem.ProductItem) {
            return oldItem.product == newItem.product
        }
        if (oldItem is ProductListViewItem.RecentViewedItem && newItem is ProductListViewItem.RecentViewedItem) {
            return oldItem.products == newItem.products
        }
        return oldItem is ProductListViewItem.ShowMoreItem && newItem is ProductListViewItem.ShowMoreItem
    }
}
