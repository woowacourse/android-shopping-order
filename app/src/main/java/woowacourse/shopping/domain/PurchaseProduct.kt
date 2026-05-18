package woowacourse.shopping.domain

data class PurchaseProduct(
    val id: Long,
    val product: Product,
    val count: Int = 1,
) {
    init {
        require(count > 0) { "구매할 상품의 개수는 1개 이상이어야 합니다." }
    }

    fun updateCount(updateAmount: Int): PurchaseProduct {
        val newCount = count + updateAmount
        return copy(count = newCount)
    }

    fun name() = product.name

    fun price() = product.price

    fun imageUri() = product.imageUri

    fun productId() = product.id

    fun totalPrice() = product.price * count

    fun isSameProductID(id: Long) = id == product.id
}
