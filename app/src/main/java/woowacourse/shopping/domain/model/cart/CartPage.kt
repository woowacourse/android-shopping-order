package woowacourse.shopping.domain.model.cart

import woowacourse.shopping.domain.model.order.PurchaseProducts

data class CartPage(
    val items: PurchaseProducts,
    val isLast: Boolean,
)
