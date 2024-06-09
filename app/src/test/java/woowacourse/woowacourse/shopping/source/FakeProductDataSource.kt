package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource
import kotlin.math.min

class FakeProductDataSource(
    private val allProducts: MutableList<ProductData>,
) : ProductDataSource {
    override fun shutDown(): Boolean {
        println("shutDown")
        return true
    }

    override suspend fun findByPaged(page: Int): Result<List<ProductData>> =
        runCatching {
            val endIndex = min(page * 20, allProducts.size)

            allProducts.subList(fromIndex = (page - 1) * 20, toIndex = endIndex)
        }

    override suspend fun findAllUntilPage(page: Int): Result<List<ProductData>> =
        runCatching {
            val endIndex = min(page * 20, allProducts.size)

            allProducts.subList(fromIndex = 0, toIndex = endIndex)
        }

    override suspend fun findById(id: Long): Result<ProductData> =
        runCatching {
            allProducts.find { it.id == id }
                ?: throw NoSuchElementException("there is no product with id: $id")
        }

    override suspend fun findByCategory(category: String): Result<List<ProductData>> =
        runCatching {
            allProducts.filter { it.category == category }
        }

    override suspend fun isFinalPage(page: Int): Result<Boolean> =
        runCatching {
            allProducts.size <= page * 20
        }
}
