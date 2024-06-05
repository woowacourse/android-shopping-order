package woowacourse.shopping.view.products.model

import woowacourse.shopping.domain.model.product.Product

sealed class ShoppingItem {
    data class ProductItem(val product: Product) : ShoppingItem()

    data object SkeletonItem : ShoppingItem()
}
