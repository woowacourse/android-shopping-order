package woowacourse.shopping.view.home.product

import androidx.recyclerview.widget.DiffUtil

object ProductDiffUtil : DiffUtil.ItemCallback<HomeViewItem>() {
    override fun areItemsTheSame(
        oldItem: HomeViewItem,
        newItem: HomeViewItem,
    ): Boolean {
        if (oldItem is HomeViewItem.ProductViewItem && newItem is HomeViewItem.ProductViewItem) {
            return oldItem.product.id == newItem.product.id
        }
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(
        oldItem: HomeViewItem,
        newItem: HomeViewItem,
    ): Boolean {
        return oldItem == newItem
    }
}
