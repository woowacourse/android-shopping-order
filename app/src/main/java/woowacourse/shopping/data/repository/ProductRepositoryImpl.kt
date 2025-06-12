package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.product.ProductRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val dataSource: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun fetchCatalogProduct(productId: Long): Result<Product?> =
        runCatching {
            val response = dataSource.getProductDetail(productId)
            val detail = response.toDomain()

            Product(detail)
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
    ): Result<Products> =
        runCatching {
            val response = dataSource.getProducts(category, page, size)
            val items = response.content.map { it.toDomain() }
            val isFirst = response.first
            val isLast = response.last
            val pageInfo = Page(page, isFirst, isLast)

            Products(items, pageInfo)
        }

    override suspend fun fetchAllProducts(): Result<List<Product>> =
        runCatching {
            val fistPage = 0
            val maxsize = Int.MAX_VALUE
            val response = dataSource.getProducts(page = fistPage, size = maxsize)
            val products = response.content.map { it.toDomain() }

            products
        }
}
