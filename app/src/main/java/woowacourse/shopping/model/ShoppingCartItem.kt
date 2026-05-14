package woowacourse.shopping.model

class ShoppingCartItem(
    private val id: Long,
    private val shoppingItem: ShoppingItem,
) {
    fun getId(): Long = id

    val product: Product
        get() = shoppingItem.getProduct()

    fun getQuantity(): Int = shoppingItem.getQuantity()

    fun getProductQuantityPrice(): Int = shoppingItem.getProductQuantityPrice()

    override fun equals(other: Any?): Boolean {
        if (other !is ShoppingCartItem) return false
        return id == other.id &&
            product.id == other.product.id &&
            product.getTitle() == other.product.getTitle() &&
            product.getPrice() == other.product.getPrice() &&
            product.imageUrl == other.product.imageUrl &&
            product.category == other.product.category &&
            getQuantity() == other.getQuantity()
    }

    override fun hashCode(): Int =
        listOf(
            id,
            product.id,
            product.getTitle(),
            product.getPrice(),
            product.imageUrl,
            product.category,
            getQuantity(),
        ).hashCode()
}
