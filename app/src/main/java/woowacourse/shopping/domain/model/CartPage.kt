package woowacourse.shopping.domain.model

data class CartPage(
    val items: PurchaseProducts,
    val isLast: Boolean,
)
