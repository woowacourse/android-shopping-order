package woowacourse.shopping.view.products

import woowacourse.shopping.domain.model.Product

sealed class ShoppingItem {
    data class ProductItem(val product: Product) : ShoppingItem()

    data object SkeletonItem : ShoppingItem()
}
