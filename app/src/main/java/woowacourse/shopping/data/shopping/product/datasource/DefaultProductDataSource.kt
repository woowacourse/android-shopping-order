package woowacourse.shopping.data.shopping.product.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.data.shopping.product.toData
import woowacourse.shopping.data.shopping.product.toProduct
import woowacourse.shopping.data.util.executeAsResult
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.service.ProductService

class DefaultProductDataSource(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        val result =
            withContext(Dispatchers.IO) {
                productService.fetchProducts(currentPage, size)
                    .executeAsResult()
                    .mapCatching { it.toData() }
            }
        return result
    }

    override suspend fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        val result =
            withContext(Dispatchers.IO) {
                productService.fetchProducts(category, currentPage, size)
                    .executeAsResult()
                    .mapCatching { it.toData() }
            }
        return result
    }

    override suspend fun fetchProductById(id: Long): Result<Product> {
        val result =
            withContext(Dispatchers.IO) {
                productService.fetchDetailProduct(id)
                    .executeAsResult()
                    .mapCatching { it.toProduct() }
            }
        return result
    }
}
