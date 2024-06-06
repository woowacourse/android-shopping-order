package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.product.ProductDataSource

class FakeProductDataSource(
    private val pagingStrategy: NumberPagingStrategy<ProductData> = NumberPagingStrategy(20),
    private val allProducts: MutableList<ProductData>,
) : ProductDataSource {
    override fun loadByPaged(page: Int): List<ProductData> = pagingStrategy.loadPagedData(page, allProducts)

    override fun loadById(id: Long): ProductData =
        allProducts.find { it.id == id }
            ?: throw NoSuchElementException("there is no product with id: $id")

    override fun isFinalPage(page: Int): Boolean = pagingStrategy.isFinalPage(page, allProducts)

    override fun shutDown(): Boolean {
        println("shutDown")
        return true
    }
}
