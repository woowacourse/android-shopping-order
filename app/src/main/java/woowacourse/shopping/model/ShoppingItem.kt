package woowacourse.shopping.model

class ShoppingItem(
    private val product: Product,
    private var quantity: Int = 0,
) {
    init {
        require(quantity >= 0) { "상품의 수량은 음수일 수 없습니다." }
    }
    fun getProduct(): Product = product

    fun getProductId(): Long = product.id

    fun getQuantity(): Int = quantity

    fun plusQuantity(amount: Int = 1) {
        quantity += amount
    }

    fun minusQuantity() {
        if (quantity == 0) {
            throw IllegalArgumentException("상품의 수량은 0보다 작을 수 없습니다.")
        }
        quantity -= 1
    }

    fun getProductQuantityPrice(): Int = getProductQuantityPrice(quantity)

    fun getProductQuantityPrice(quantity: Int): Int {
        return product.getPrice() * quantity
    }
}
