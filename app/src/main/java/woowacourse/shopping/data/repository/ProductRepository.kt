package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override suspend fun fetchCatalogProduct(productId: Long): Result<Product?> =
        runCatching {
            val response = api.getProductDetail(productId)
            val detail = response.body()?.toDomain() ?: EMPTY_PRODUCT_DETAIL
            if (response.isSuccessful) {
                Product(detail)
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
    ): Result<Products> =
        runCatching {
            val response = api.getProducts(category, page, size)

            if (response.isSuccessful) {
                val body = response.body()
                val items = body?.content?.map { it.toDomain() } ?: emptyList()
                val pageInfo = Page(page, body?.first ?: false, body?.last ?: false)
                Products(items, pageInfo)
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun fetchAllProducts(): Result<List<Product>> =
        runCatching {
            val fistPage = 0
            val maxsize = Int.MAX_VALUE
            val response = api.getProducts(page = fistPage, size = maxsize)
            if (response.isSuccessful) {
                val products = response.body()?.content?.map { it.toDomain() } ?: emptyList()
                products
            } else {
                throw IllegalArgumentException()
            }
        }
}
