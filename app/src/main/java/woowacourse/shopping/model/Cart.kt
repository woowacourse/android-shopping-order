package woowacourse.shopping.model

data class Cart(
    val items: List<CartItem>,
) {
    init {
        require(items.map { it.productId }.distinct().size == items.size) {
            "장바구니에는 같은 상품이 중복 저장될 수 없습니다."
        }
    }

    fun add(productId: Long): Cart {
        val newItems = items.toMutableList()
        val index = newItems.indexOfFirst { it.productId == productId }

        if (index == -1) {
            newItems.add(CartItem(productId, 1))
        } else {
            newItems[index] = newItems[index].increase()
        }

        return Cart(newItems)
    }

    fun delete(productId: Long): Cart {
        val newItems = items.toMutableList()
        val index = newItems.indexOfFirst { it.productId == productId }

        require(index != -1) { "해당 상품은 장바구니에 존재하지 않습니다." }

        val updatedItem = newItems[index].decreaseOrNull()

        if (updatedItem == null) {
            newItems.removeAt(index)
        } else {
            newItems[index] = updatedItem
        }

        return Cart(newItems)
    }

    fun setQuantity(
        productId: Long,
        quantity: Int,
    ): Cart {
        require(quantity >= 0) { "장바구니 수량은 0개 이상이어야 합니다." }

        val newItems = items.toMutableList()
        val index = newItems.indexOfFirst { it.productId == productId }

        if (quantity == 0) {
            if (index != -1) {
                newItems.removeAt(index)
            }
            return Cart(newItems)
        }

        val updatedItem = CartItem(productId = productId, quantity = quantity)

        if (index == -1) {
            newItems.add(updatedItem)
        } else {
            newItems[index] = updatedItem
        }

        return Cart(newItems)
    }

    fun count(): Int = items.size
}
