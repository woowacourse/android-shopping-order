package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

data class Product(
    val productDetail: ProductDetail,
    val cartId: Long? = null,
    val quantity: Int = 0,
) {
    val totalPrice: Int get() = productDetail.price * quantity

    fun increaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): Product = copy(quantity = quantity + delta)

    fun decreaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): Product =
        if (quantity - delta >= 0) {
            copy(quantity = quantity - delta)
        } else {
            copy(quantity = 0)
        }

    companion object {
        val EMPTY_PRODUCT = Product(EMPTY_PRODUCT_DETAIL, null, 0)
        private const val DEFAULT_QUANTITY_DELTA = 1
    }
}
