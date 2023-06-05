package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.repository.CartProductId

data class OrderProduct(
    val cartProductId: CartProductId = NO_ID,
    val name: String,
    val price: Price,
    val quantity: ProductCount,
    val imageUrl: String,
) {
    val totalPrice: Price = price * quantity.value

    companion object {
        private const val NO_ID = -1

        fun of(cartProduct: CartProduct): OrderProduct = OrderProduct(
            cartProductId = cartProduct.id,
            name = cartProduct.product.name,
            price = cartProduct.product.price,
            quantity = cartProduct.selectedCount,
            imageUrl = cartProduct.product.imageUrl
        )
    }
}
