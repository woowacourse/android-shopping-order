package woowacourse.shopping.source

import woowacourse.shopping.NumberPagingStrategy
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource
import kotlin.math.min

class FakeProductDataSource(
    private val pagingStrategy: NumberPagingStrategy<ProductData> = NumberPagingStrategy(20),
    private val allProducts: MutableList<ProductData>,
) : ProductDataSource {
    override fun findByPaged(page: Int): List<ProductData> = pagingStrategy.loadPagedData(page, allProducts)

    override fun findAllUntilPage(page: Int): List<ProductData> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): ProductData =
        allProducts.find { it.id == id }
            ?: throw NoSuchElementException("there is no product with id: $id")

    override fun findByCategory(category: String): List<ProductData> = allProducts.filter { it.category == category }

    override fun isFinalPage(page: Int): Boolean = pagingStrategy.isFinalPage(page, allProducts)

    override fun shutDown(): Boolean {
        println("shutDown")
        return true
    }

    override fun findByPaged2(page: Int): Result<List<ProductData>> =
        runCatching {
            val endIndex = min(page * 20, allProducts.size)

            allProducts.subList(fromIndex = (page - 1) * 20, toIndex = endIndex)
        }

    override fun findAllUntilPage2(page: Int): Result<List<ProductData>> =
        runCatching {
            val endIndex = min(page * 20, allProducts.size)

            allProducts.subList(fromIndex = 0, toIndex = endIndex)
        }

    override fun findById2(id: Long): Result<ProductData> =
        runCatching {
            allProducts.find { it.id == id }
                ?: throw NoSuchElementException("there is no product with id: $id")
        }

    override fun findByCategory2(category: String): Result<List<ProductData>> =
        runCatching {
            allProducts.filter { it.category == category }
        }

    override fun isFinalPage2(page: Int): Result<Boolean> =
        runCatching {
            allProducts.size <= page * 20
        }
}
