package woowacourse.shopping.view.common

import woowacourse.shopping.domain.product.Product

interface ProductQuantityClickListener {
    fun onPlusShoppingCartClick(
        product: Product,
        quantity: Int,
    )

    fun onMinusShoppingCartClick(
        product: Product,
        quantity: Int,
    )
}
