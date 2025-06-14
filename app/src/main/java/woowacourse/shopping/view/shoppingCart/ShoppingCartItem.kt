package woowacourse.shopping.view.shoppingCart

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.QuantityTarget

data class ShoppingCartItem(
    val shoppingCartProduct: ShoppingCartProduct,
    val isChecked: Boolean = false,
) : QuantityTarget
