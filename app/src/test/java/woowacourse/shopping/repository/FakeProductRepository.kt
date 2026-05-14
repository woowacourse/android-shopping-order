package woowacourse.shopping.repository

import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Products

class FakeProductRepository(
    products: List<Product>,
) : ProductRepository {
    private val products = Products(products)
    private val productMap: Map<Long, Product> = products.associateBy { it.id }

    override val size: Int
        get() = products.count()

    override suspend fun getProducts(
        fromIndex: Int,
        limit: Int,
    ): Products {
        val safeFrom = fromIndex.coerceIn(0, products.count())
        val safeLimit = limit.coerceAtLeast(0)
        val safeTo = minOf(safeFrom + safeLimit, products.count())

        return Products(products.toList().subList(safeFrom, safeTo))
    }

    override suspend fun getProductsByCategory(
        category: String,
        limit: Int,
    ): Products =
        Products(
            products
                .toList()
                .filter { it.category == category }
                .take(limit.coerceAtLeast(0)),
        )

    override suspend fun hasNext(current: Int): Boolean = current < products.toList().lastIndex

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        ids
            .mapNotNull { id ->
                productMap[id]?.let { id to it }
            }.toMap()
}
