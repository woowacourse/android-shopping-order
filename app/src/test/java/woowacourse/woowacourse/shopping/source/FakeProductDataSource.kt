package woowacourse.shopping.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.woowacourse.shopping.testfixture.runCatchingWithDispatcher
import kotlin.math.min

@OptIn(ExperimentalCoroutinesApi::class)
class FakeProductDataSource(
    private val dispatcher: CoroutineDispatcher = UnconfinedTestDispatcher(),
    private val allProducts: MutableList<ProductData>,
) : ProductDataSource {
    override fun shutDown(): Boolean {
        println("shutDown")
        return true
    }

    override suspend fun findByPaged(page: Int): Result<List<ProductData>> = runCatchingWithDispatcher(dispatcher) {
        val endIndex = min(page * 20, allProducts.size)

        allProducts.subList(fromIndex = (page - 1) * 20, toIndex = endIndex)
    }

    override suspend fun findAllUntilPage(page: Int): Result<List<ProductData>> =
        runCatchingWithDispatcher(dispatcher) {
            val endIndex = min(page * 20, allProducts.size)

            allProducts.subList(fromIndex = 0, toIndex = endIndex)
        }

    override suspend fun findById(id: Long): Result<ProductData> = runCatchingWithDispatcher(dispatcher) {
        allProducts.find { it.id == id }
            ?: throw NoSuchElementException("there is no product with id: $id")
    }

    override suspend fun findByCategory(category: String): Result<List<ProductData>> =
        runCatchingWithDispatcher(dispatcher) {
            allProducts.filter { it.category == category }
        }

    override suspend fun isFinalPage(page: Int): Result<Boolean> = runCatchingWithDispatcher(dispatcher) {
        allProducts.size <= page * 20
    }
}
