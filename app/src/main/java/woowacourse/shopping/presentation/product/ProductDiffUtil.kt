package woowacourse.shopping.presentation.product

import androidx.recyclerview.widget.DiffUtil

object ProductDiffUtil : DiffUtil.ItemCallback<ProductItemType>() {
    override fun areItemsTheSame(
        oldItem: ProductItemType,
        newItem: ProductItemType,
    ): Boolean =
        when {
            oldItem is ProductItemType.Product && newItem is ProductItemType.Product ->
                oldItem.cartItemUiModel.product.id == newItem.cartItemUiModel.product.id

            oldItem is ProductItemType.LoadMore && newItem is ProductItemType.LoadMore -> true
            else -> false
        }

    override fun areContentsTheSame(
        oldItem: ProductItemType,
        newItem: ProductItemType,
    ): Boolean = oldItem == newItem
}
