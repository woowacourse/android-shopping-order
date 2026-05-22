package woowacourse.shopping.domain

import kotlin.math.min

class Products(
    val products: List<Product> = emptyList(),
) {
    fun isEmpty() = products.isEmpty()

    fun size() = products.size

    fun add(product: Product) = Products(products + product)

    fun remove(id: Long): Products {
        val product = findWithId(id) ?: return this
        return Products(products - product)
    }

    fun getPartedItem(
        page: Int,
        pageSize: Int,
    ): Products {
        val fromIndex =
            if (page * pageSize > products.size) products.size else page * pageSize
        val toIndex = min(fromIndex + pageSize, products.size)
        return Products(products.subList(fromIndex, toIndex))
    }

    fun getSingleItem(index: Int): Product = products[index]

    operator fun plus(products: Products) = Products(this.products + products.products)

    fun findWithId(id: Long) = products.find { it.id == id }
}
