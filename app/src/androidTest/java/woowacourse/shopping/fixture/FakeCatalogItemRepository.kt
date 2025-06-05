package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse
import woowacourse.shopping.data.source.remote.products.ProductsDataSource

class FakeCatalogItemRepository(
    private val baseUrl: String,
) : ProductsDataSource {
    override fun getProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<ProductsResponse>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun getProductById(
        id: Long,
        onResult: (Result<ProductResponse>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun getProductsByCategory(
        category: String,
        onResult: (Result<ProductsResponse>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }
}
