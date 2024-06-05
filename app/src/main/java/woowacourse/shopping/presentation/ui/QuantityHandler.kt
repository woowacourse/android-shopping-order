package woowacourse.shopping.presentation.ui

import woowacourse.shopping.domain.ProductListItem

interface QuantityHandler {
    fun onDecreaseQuantity(product: ProductListItem.ShoppingProductItem?)

    fun onIncreaseQuantity(product: ProductListItem.ShoppingProductItem?)
}
