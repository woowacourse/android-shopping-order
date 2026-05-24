package woowacourse.shopping.domain.model.order

data class Order(
    val purchaseProducts: PurchaseProducts = PurchaseProducts(),
) {
    val orderAmount: Int
        get() = purchaseProducts.purchaseProducts.sumOf { it.totalPrice }

    companion object {
        fun fromSelectedCartItems(
            cartItems: PurchaseProducts,
            selectedCartItemIds: List<Long>,
        ): Order =
            Order(
                PurchaseProducts(
                    cartItems.purchaseProducts.filter { it.id in selectedCartItemIds },
                ),
            )
    }
}
