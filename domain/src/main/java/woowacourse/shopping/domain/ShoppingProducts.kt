package woowacourse.shopping.domain

data class ShoppingProducts(val value: List<ShoppingProduct>) {
    val totalQuantity: Int = value.sumOf { it.quantity }

    fun replaceShoppingProduct(shoppingProduct: ShoppingProduct): ShoppingProducts {
        return ShoppingProducts(
            value.map {
                if (it.product.id == shoppingProduct.product.id) shoppingProduct
                else it
            }
        )
    }
}
