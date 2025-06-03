package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    override fun fetchPagingProducts(
        page: Int?,
        pageSize: Int?,
        category: String?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        val pagedItems = ProductsFixture.dummyProducts.drop(0).take(12)
        val result = pagedItems.toCartItems()
        onResult(Result.success(result))
    }

    override fun fetchProductById(
        productId: Long,
        onResult: (Result<Product>) -> Unit,
    ) {
        val product = ProductsFixture.dummyProducts.find { it.productId == productId }
        if (product != null) {
            onResult(Result.success(product))
        }
    }

    private fun List<Product>.toCartItems(): List<CartItem> =
        this.map { product ->
            CartItem(1111, product, 1)
        }
}
