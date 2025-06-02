package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val productDataSource: ProductsDataSource,
) : ProductRepository {
    override fun loadSinglePage(
        category: String?,
        page: Int?,
        pageSize: Int?,
        callback: (Result<ProductSinglePage>) -> Unit,
    ) {
        productDataSource.singlePage(category, page, pageSize) { callback(it) }
    }

    override fun loadProduct(
        productId: Long,
        callback: (Result<Product>) -> Unit,
    ) {
        productDataSource.getProduct(productId) { callback(it) }
    }
}
