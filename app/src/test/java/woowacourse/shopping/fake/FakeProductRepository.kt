package woowacourse.shopping.fake

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import java.io.IOException

class FakeProductRepository(
    private val sourceProducts: List<Product>,
    var shouldFail: Boolean = false,
) : ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val recentProductIds = MutableStateFlow<List<Long>>(emptyList())

    override val products = _products.asStateFlow()

    override suspend fun loadProducts(
        offset: Int,
        limit: Int,
    ): Int {
        if (shouldFail) throw IOException()
        if (offset >= sourceProducts.size) return 0
        val toIndex = minOf(offset + limit, sourceProducts.size)
        val newProducts = sourceProducts.subList(offset, toIndex)
        _products.update { products -> (products + newProducts).distinctBy { it.id } }
        return newProducts.size
    }

    override suspend fun getProductById(id: Long): Product? = sourceProducts.find { it.id == id }

    override fun getRecentProductsStream(limit: Int) =
        recentProductIds.map { ids ->
            ids
                .take(limit)
                .mapNotNull { id -> sourceProducts.find { it.id == id } }
        }

    override suspend fun upsertRecentProduct(id: Long) {
        recentProductIds.update { ids ->
            listOf(id) + ids.filterNot { it == id }
        }
    }
}
