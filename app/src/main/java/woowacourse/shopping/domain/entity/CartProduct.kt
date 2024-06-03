package woowacourse.shopping.domain.entity

data class CartProduct(
    val product: Product,
    val count: Int,
    val id: Long,
) {
    val totalPrice get(): Int = product.price * count
}
