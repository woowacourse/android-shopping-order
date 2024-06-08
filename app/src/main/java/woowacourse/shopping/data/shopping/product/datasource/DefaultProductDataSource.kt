package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.data.shopping.product.toData
import woowacourse.shopping.data.shopping.product.toProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.service.ProductService

class DefaultProductDataSource(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        return runCatching {
            productService.fetchProducts(currentPage, size)
                .toData()
        }
    }

    override suspend fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        return runCatching {
            productService.fetchProducts(category, currentPage, size)
                .toData()
        }
    }

    override suspend fun productById(id: Long): Result<Product> {
        return runCatching {
            productService.fetchDetailProduct(id).toProduct()
        }
    }
}
