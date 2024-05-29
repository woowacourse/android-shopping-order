package woowacourse.shopping.domain.entity

data class CartProduct(
    val product: Product,
    val count: Int,
    val id: Long = -1,
) {
    val totalPrice get(): Int = product.price * count

    fun plusCount(): CartProduct = CartProduct(product, count + 1)

    fun minusCount(): CartProduct = CartProduct(product, count - 1)
}
