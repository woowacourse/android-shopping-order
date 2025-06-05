package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

data class Product(
    val productDetail: ProductDetail,
    val cartId: Long? = null,
    val quantity: Int = 0,
    val isSelected: Boolean = false,
) {
    val totalPrice: Int get() = productDetail.price * quantity

    fun increaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): Product = copy(quantity = quantity + delta)

    fun decreaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): Product =
        if (quantity - delta >= 0) {
            copy(quantity = quantity - delta)
        } else {
            copy(quantity = 0)
        }

    fun updateSelection(isSelected: Boolean = this.isSelected.not()): Product = copy(isSelected = isSelected)

    companion object {
        const val MINIMUM_QUANTITY = 0
        val EMPTY_PRODUCT = Product(EMPTY_PRODUCT_DETAIL, null, MINIMUM_QUANTITY)
        private const val DEFAULT_QUANTITY_DELTA = 1
    }
}
