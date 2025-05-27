package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL

data class CatalogProduct(
    val productDetail: ProductDetail,
    val quantity: Int,
) {
    val totalPrice: Int get() = productDetail.price * quantity

    fun increaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): CatalogProduct = copy(quantity = quantity + delta)

    fun decreaseQuantity(delta: Int = DEFAULT_QUANTITY_DELTA): CatalogProduct =
        if (quantity - delta >= 0) {
            copy(quantity = quantity - delta)
        } else {
            copy(quantity = 0)
        }

    companion object {
        val EMPTY_CATALOG_PRODUCT = CatalogProduct(EMPTY_PRODUCT_DETAIL, 0)
        private const val DEFAULT_QUANTITY_DELTA = 1
    }
}
