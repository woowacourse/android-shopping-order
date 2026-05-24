package woowacourse.shopping.fake.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import java.io.IOException

class FakeProductRepository(
    private val products: List<Product>,
    var shouldFail: Boolean = false,
) : ProductRepository {
    override suspend fun getProducts(
        offset: Int,
        limit: Int,
        category: String?,
    ): ImmutableList<Product> {
        if (shouldFail) throw IOException()
        val filtered =
            if (category == null) {
                products
            } else {
                products.filter { it.category == category }
            }
        if (offset >= filtered.size) return emptyList<Product>().toImmutableList()
        val toIndex = minOf(offset + limit, filtered.size)
        return filtered.subList(offset, toIndex).toImmutableList()
    }

    override suspend fun getProductById(id: Long): Product = products.first { it.id == id }
}
