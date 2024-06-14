package woowacourse.shopping.domain

data class Product(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val price: Long,
    val category: String,
) {
    fun toInitialShoppingItem() =
        ProductListItem.ShoppingProductItem(
            id = id,
            name = name,
            imgUrl = imgUrl,
            price = price,
            quantity = 0,
            category = category,
            isChecked = false,
        )
}
