package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.repository.ProductId

data class OrderProduct(
    val productId: ProductId = NO_ID,
    val name: String,
    val price: Price,
    val quantity: ProductCount,
    val imageUrl: String,
) {
    val totalPrice: Price = price * quantity.value

    companion object {
        private const val NO_ID = -1

        fun of(cartProduct: CartProduct): OrderProduct = OrderProduct(
            productId = cartProduct.productId,
            name = cartProduct.product.name,
            price = cartProduct.product.price,
            quantity = cartProduct.selectedCount,
            imageUrl = cartProduct.product.imageUrl
        )
    }
}
