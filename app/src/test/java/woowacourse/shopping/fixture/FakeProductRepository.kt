package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<PageableItem<Product>> {
        val pagedItems = ProductsFixture.dummyProducts.drop(0).take(12)
        return Result.success(PageableItem(pagedItems, true))
    }

    override suspend fun fetchProductById(productId: Long): Result<Product> {
        val product = ProductsFixture.dummyProducts.find { it.productId == productId }
        return Result.success(product!!)
    }
}
