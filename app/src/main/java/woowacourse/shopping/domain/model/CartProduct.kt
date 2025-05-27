package woowacourse.shopping.domain.model

data class CartProduct(
    val productDetail: ProductDetail,
    val quantity: Int,
) {
    val totalPrice: Int get() = productDetail.price * quantity
}
