package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.product.Product
import woowacourse.shopping.data.model.product.ProductResponse
import woowacourse.shopping.data.remote.ProductService
import kotlin.math.max

class DefaultRemoteProductDataSource(private val productService: ProductService) :
    RemoteProductDataSource {
    override suspend fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): ProductResponse {
        return productService.getProducts(category, page, size, sort)
    }

    override suspend fun getProductById(id: Int): Product {
        return productService.getProductById(id)
    }

    override suspend fun getRecommendedProducts(
        category: String?,
        maxSize: Int,
        sort: String,
    ): ProductResponse {
        var page = 0
        var productResponse: ProductResponse? = null
        do {
            val currentResponse = productService.getProducts(category, page++, maxSize, sort)
            if (currentResponse.empty) break
            productResponse = productResponse?.copy(
                products = productResponse.products + currentResponse.products
            ) ?: currentResponse
        } while ((productResponse?.products?.size ?: 0) < maxSize)

        return productResponse?.copy(
            products = productResponse.products.shuffled().take(maxSize)
        ) ?: throw RuntimeException("Product Response Does Not Exist")
    }
}
