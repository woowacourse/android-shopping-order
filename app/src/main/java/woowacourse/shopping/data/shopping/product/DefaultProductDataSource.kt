package woowacourse.shopping.data.shopping.product

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
        return ioExecutor.submit(Callable {
            productService.fetchProducts(currentPage, size)
                .executeAsResult()
                .mapCatching { it.toDataModel() }
        }).get()
    }

    override fun productById(id: Long): Result<Product> {
        return ioExecutor.submit(Callable {
            productService.fetchDetailProduct(id)
                .executeAsResult()
                .mapCatching { it.toProduct() }
        }).get()
    }
}
