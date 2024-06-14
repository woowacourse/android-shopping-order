package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.remote.RemoteProductDataSource
import woowacourse.shopping.domain.PagedProducts
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(private val remoteProductDataSource: RemoteProductDataSource = RemoteProductDataSource()) :
    ProductRepository {
    override suspend fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): Result<List<Product>> {
        return runCatching {
            remoteProductDataSource.loadWithCategory(
                category,
                startPage,
                pageSize,
            ).productItemResponse.map { it.toProduct() }
        }
    }

    override suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<PagedProducts> {
        return runCatching {
            val productResponse = remoteProductDataSource.load(startPage, pageSize)
            PagedProducts(
                productResponse.productItemResponse.map { it.toProduct() },
                productResponse.last,
            )
        }
    }

    override suspend fun loadById(id: Long): Result<Product> {
        return runCatching {
            remoteProductDataSource.loadById(id).toProduct()
        }
    }
}
