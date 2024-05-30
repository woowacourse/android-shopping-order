package woowacourse.shopping.domain

data class Cart(
    val cartId: Long = -1L,
    val product: Product,
    val quantity: Int = DEFAULT_PURCHASE_COUNT,
) {
    fun toShoppingProduct(): ProductListItem.ShoppingProductItem =
        ProductListItem.ShoppingProductItem(
            id = this.product.id,
            cartId = this.cartId,
            name = this.product.name,
            imgUrl = this.product.imgUrl,
            price = this.product.price,
            quantity = this.quantity,
            category = this.product.category,
            isChecked = false,
        )

    companion object {
        const val DEFAULT_PURCHASE_COUNT = 1
    }
}
