package woowacourse.shopping.domain.model

data class CartProduct(
    val id: Long = 0,
    val product: Product,
    val quantity: Int = MINIMUM_PRODUCT_QUANTITY,
) {
    val totalPrice: Int = product.price * quantity

    companion object {
        private const val MINIMUM_PRODUCT_QUANTITY = 1
    }
}
