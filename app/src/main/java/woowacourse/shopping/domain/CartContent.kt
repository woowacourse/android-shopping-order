package woowacourse.shopping.domain

class CartContent(
    val product: Product,
    val quantity: Int,
    val id: Long = 0L,
) {
    init {
        require(isGreaterThanZero(quantity)) { "수량은 1보다 작을 수 없습니다. 수량 : $quantity" }
    }

    val productId: Long get() = product.id

    fun hasProductId(id: Long): Boolean = productId == id

    fun addQuantity(target: CartContent): CartContent = CartContent(product, quantity + target.quantity, id = target.id)

    fun decreaseQuantity(target: CartContent): CartContent {
        require(target.quantity <= quantity) { "존재하는 수량보다 많이 뺄 수 없습니다." }
        return CartContent(product, quantity - target.quantity, id = target.id)
    }

    fun changeQuantity(newQuantity: Int): CartContent = CartContent(product, newQuantity)

    companion object {
        fun isGreaterThanZero(quantity: Int) = quantity >= 0
    }
}
