package woowacourse.shopping.domain

class CartContent(
    val product: Product,
    val quantity: Int,
    val id: String = "",
) {
    init {
        require(quantity > 0) { "수량은 1보다 작을 수 없습니다. 수량 : $quantity" }
    }

    val productId: String get() = product.id
    fun hasProductId(id: String): Boolean = productId == id

    fun addQuantity(increment: Int): CartContent {
        return CartContent(product, quantity + increment)
    }

    fun decreaseQuantity(decrement: Int): CartContent {
        require(decrement <= quantity) { "존재하는 수량보다 많이 뺄 수 없습니다." }
        return CartContent(product, quantity - decrement)
    }

    fun changeQuantity(newQuantity: Int): CartContent {
        return CartContent(product, newQuantity)
    }
}
