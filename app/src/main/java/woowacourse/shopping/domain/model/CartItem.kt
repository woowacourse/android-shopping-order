package woowacourse.shopping.domain.model

data class CartItem(
    val id: Long,
    val product: Product,
    val quantity: Int,
) {
    val isNew: Boolean get() = id == NEW_ITEM_ID

    init {
        require(quantity > 0) { "수량은 1개 이상이어야 합니다." }
        require(quantity < 100) { "수량은 100개 미만이어야 합니다." }
    }

    fun getTotalPrice(): Money = product.price * quantity

    fun decrease(): CartItem {
        if (quantity == 1) return this
        return copy(quantity = quantity - 1)
    }

    fun increase(quantity: Int = 1): CartItem {
        if (this.quantity + quantity >= 100) return this
        return copy(quantity = this.quantity + quantity)
    }

    companion object {
        const val NEW_ITEM_ID = -1L
    }
}
