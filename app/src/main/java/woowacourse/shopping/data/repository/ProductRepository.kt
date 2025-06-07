package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.model.response.ProductDetailResponse.Companion.toDomain
import woowacourse.shopping.data.model.response.ProductsResponse.Content.Companion.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
    ): Result<Products> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getProducts(category, page, size)
            }.mapCatching { response ->
                val items = response.content.map { it.toDomain() }
                val pageInfo = Page(page, response.first, response.last)
                Products(items, pageInfo)
            }
        }

    override suspend fun fetchAllProducts(): Result<List<Product>> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        return withContext(Dispatchers.IO) {
            runCatching {
                api.getProducts(page = firstPage, size = maxSize)
            }.mapCatching { response ->
                response.content.map { it.toDomain() }
            }
        }
    }

    override suspend fun fetchProduct(productId: Long): Result<ProductDetail> = runCatching { api.getProduct(productId).toDomain() }
}
