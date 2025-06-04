package woowacourse.shopping.domain.model

data class CartProduct(
    val id: Long,
    val product: Product,
    val quantity: Int,
    val isChecked: Boolean = false,
) {
    val totalPrice: Int get() = product.price * quantity

    companion object {
        val EMPTY_CART_PRODUCT =
            CartProduct(
                id = 0,
                product = Product(id = 0, name = "", price = 0, imageUrl = "", category = ""),
                quantity = 0,
            )
    }
}
