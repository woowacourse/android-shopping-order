package woowacourse.shopping.view.home.product

import androidx.recyclerview.widget.DiffUtil

object ProductDiffUtil : DiffUtil.ItemCallback<HomeViewItem>() {
    override fun areItemsTheSame(
        oldItem: HomeViewItem,
        newItem: HomeViewItem,
    ): Boolean {
        if (oldItem is HomeViewItem.ProductViewItem && newItem is HomeViewItem.ProductViewItem) {
            return oldItem.orderableProduct.productItemDomain.id == newItem.orderableProduct.productItemDomain.id
        }
        return oldItem.viewType == newItem.viewType
    }

    override fun areContentsTheSame(
        oldItem: HomeViewItem,
        newItem: HomeViewItem,
    ): Boolean {
        return oldItem == newItem
    }
}
