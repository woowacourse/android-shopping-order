package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Page.Companion.EMPTY_PAGE

data class CartProducts(
    val products: List<CartProduct>,
    val page: Page,
) {
    fun updateCartProductQuantity(
        productId: Long,
        quantity: Int,
    ): CartProducts {
        val updatedProducts =
            products.map { product ->
                if (product.productDetail.id == productId) {
                    product.copy(quantity = quantity)
                } else {
                    product
                }
            }
        return copy(products = updatedProducts)
    }

    companion object {
        val EMPTY_CART_PRODUCTS = CartProducts(emptyList(), EMPTY_PAGE)
    }
}
