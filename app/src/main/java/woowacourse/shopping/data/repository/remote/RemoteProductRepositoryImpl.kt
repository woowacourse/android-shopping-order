package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.ProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class RemoteProductRepositoryImpl(
    private val productDataSource: ProductDataSource = ProductDataSourceImpl(),
) : ProductRepository {
    override fun loadPagingProducts(offset: Int): Result<List<Product>> {
        val page = offset / PRODUCT_LOAD_PAGING_SIZE
        return productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE)
    }

    override fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        return productDataSource.loadCategoryProducts(
            page = DEFAULT_PAGE,
            size = RECOMMEND_PRODUCT_LOAD_PAGING_SIZE,
            category = category,
        )
    }

    override fun getProduct(productId: Long): Result<Product> {
        return productDataSource.loadProduct(productId.toInt())
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
