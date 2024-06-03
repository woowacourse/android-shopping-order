package woowacourse.shopping.presentation.ui.model

sealed interface ProductListItem {
    data class RecentProductItems(val items: List<ProductModel>) : ProductListItem

    data class ShoppingProductItem(val product: ProductModel) : ProductListItem
}
