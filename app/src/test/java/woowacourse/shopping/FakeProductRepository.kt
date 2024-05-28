package woowacourse.shopping

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.math.min

class FakeProductRepository(savedProducts: List<Product> = emptyList()) : ProductRepository {
    private val products: MutableMap<Int, Product> = savedProducts.associateBy { it.id }.toMutableMap()

    override fun find(id: Int): Product {
        return products[id] ?: throw IllegalArgumentException()
    }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): List<Product> {
        val fromIndex = page * pageSize
        val toIndex = min(fromIndex + pageSize, products.size)
        return products.map { it.value }.subList(fromIndex, toIndex)
    }

    override fun totalProductCount(): Int = products.size
}
