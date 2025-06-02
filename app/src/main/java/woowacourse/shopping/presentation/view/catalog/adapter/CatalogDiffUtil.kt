package woowacourse.shopping.presentation.view.catalog.adapter

import androidx.recyclerview.widget.DiffUtil
import woowacourse.shopping.presentation.model.CatalogItem

object CatalogDiffUtil : DiffUtil.ItemCallback<CatalogItem>() {
    override fun areItemsTheSame(
        oldItem: CatalogItem,
        newItem: CatalogItem,
    ): Boolean {
        if (oldItem.viewType != newItem.viewType) return false

        return when (oldItem) {
            is CatalogItem.ProductItem -> oldItem.productId == (newItem as CatalogItem.ProductItem).productId
            is CatalogItem.RecentProducts -> true
            is CatalogItem.LoadMoreItem -> true
        }
    }

    override fun areContentsTheSame(
        oldItem: CatalogItem,
        newItem: CatalogItem,
    ): Boolean = oldItem == newItem
}
