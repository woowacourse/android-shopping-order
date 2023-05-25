package woowacourse.shopping

class Products(products: List<Product> = listOf()) {
    private val _items = products.toMutableList()
    val items get() = _items.toList()

    val size get(): Int = _items.size
    fun addProducts(products: List<Product>): Products {
        _items.addAll(products)
        return Products(items)
    }

    fun getItemsInRange(startIndex: Int, size: Int): Products {
        return when {
            startIndex > items.size -> Products(emptyList())
            startIndex + size > items.size -> Products(
                items.subList(
                    startIndex,
                    items.size,
                ),
            )
            else -> Products(items.subList(startIndex, startIndex + size))
        }
    }
}
