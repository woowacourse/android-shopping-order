package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override suspend fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
    ): Result<List<CartItem>> {
        val pagedItems = ProductsFixture.dummyProducts.drop(0).take(12)
        val result = pagedItems.toCartItems()
        return Result.success(result)
    }

    override suspend fun fetchProductById(productId: Long): Result<Product> {
        val product = ProductsFixture.dummyProducts.find { it.productId == productId }
        return Result.success(product!!)
    }

    private fun List<Product>.toCartItems(): List<CartItem> =
        this.map { product ->
            CartItem(1111, product, 1)
        }
}
