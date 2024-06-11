package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.RemoteProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.ProductRepository

class RemoteProductRepositoryImpl(
    private val productDataSource: ProductDataSource = RemoteProductDataSourceImpl(),
) : ProductRepository {
    override suspend fun loadPagingProducts(offset: Int): Result<List<Product>> {
        val page = offset / PRODUCT_LOAD_PAGING_SIZE
        return productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE)
    }

    override suspend fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        return productDataSource.loadCategoryProducts(
            page = DEFAULT_PAGE,
            size = RECOMMEND_PRODUCT_LOAD_PAGING_SIZE,
            category = category,
        )
    }

    override suspend fun getProduct(productId: Long): Result<Product> {
        return productDataSource.loadProduct(productId.toInt())
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
