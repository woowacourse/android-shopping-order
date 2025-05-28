package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.data.util.runCatchingInThread
import woowacourse.shopping.domain.model.PageableItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun fetchProduct(
        id: Long,
        onResult: (Result<Product>) -> Unit,
    ) = runCatchingInThread(onResult) {
        productRemoteDataSource.fetchProduct(id).getOrThrow().toDomain()
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onResult: (Result<PageableItem<Product>>) -> Unit,
    ) = runCatchingInThread(onResult) {
        val response = productRemoteDataSource.fetchProducts(null, page, size).getOrThrow()
        val products = response.content.map { it.toDomain() }
        val hasMore = !response.last
        PageableItem(products, hasMore)
    }
}
