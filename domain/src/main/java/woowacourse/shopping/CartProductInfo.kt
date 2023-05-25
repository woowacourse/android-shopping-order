package woowacourse.shopping

data class CartProductInfo(
    val id: Int,
    val product: Product,
    val count: Int,
    val isOrdered: Boolean = false
) {
    val totalPrice: Int get() = product.price.value * count
    fun setCount(newCount: Int): CartProductInfo {
        return CartProductInfo(id, product, newCount, isOrdered)
    }

    fun setOrderState(newIsOrdered: Boolean): CartProductInfo {
        return CartProductInfo(id, product, count, newIsOrdered)
    }
}
