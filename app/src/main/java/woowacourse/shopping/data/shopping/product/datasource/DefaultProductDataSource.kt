package woowacourse.shopping.data.shopping.product.datasource

import woowacourse.shopping.data.shopping.product.ProductPageData
import woowacourse.shopping.data.shopping.product.toData
import woowacourse.shopping.data.shopping.product.toProduct
import woowacourse.shopping.data.util.executeAsResult
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.remote.service.ProductService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

class DefaultProductDataSource(
    private val ioExecutor: ExecutorService,
    private val productService: ProductService,
) : ProductDataSource {
    override fun products(
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        return ioExecutor.submit(
            Callable {
                productService.fetchProducts(currentPage, size)
                    .executeAsResult()
                    .mapCatching { it.toData() }
            },
        ).get()
    }

    override fun products(
        category: String,
        currentPage: Int,
        size: Int,
    ): Result<ProductPageData> {
        return ioExecutor.submit(
            Callable {
                productService.fetchProducts(category, currentPage, size)
                    .executeAsResult()
                    .mapCatching { it.toData() }
            },
        ).get()
    }

    override fun fetchProductById(id: Long): Result<Product> {
        return ioExecutor.submit(
            Callable {
                productService.fetchDetailProduct(id)
                    .executeAsResult()
                    .mapCatching { it.toProduct() }
            },
        ).get()
    }
}
