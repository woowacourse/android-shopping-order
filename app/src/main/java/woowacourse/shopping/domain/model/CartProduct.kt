package woowacourse.shopping.domain.model

data class CartProduct(
    val id: Long,
    val productDetail: ProductDetail,
    val quantity: Int,
) {
    val totalPrice: Int get() = productDetail.price * quantity
}
