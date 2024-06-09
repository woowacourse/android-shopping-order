package woowacourse.shopping.domain.model

data class CartData(
    val cartItemId: Int,
    val productId: Int,
    val quantity: Int = DEFAULT_SHOPPING_QUANTITY,
) {
    fun increaseQuantity(step: Int = DEFAULT_STEP): CartData = copy(quantity = quantity + step)

    fun decreaseQuantity(step: Int = DEFAULT_STEP): CartData =
        copy(
            quantity =
                (quantity - step).coerceAtLeast(
                    DEFAULT_SHOPPING_QUANTITY,
                ),
        )

    fun totalPrice(unitPrice: Int): Int = unitPrice * quantity

    companion object {
        private const val DEFAULT_SHOPPING_QUANTITY = 0
        private const val DEFAULT_STEP = 1
    }
}
