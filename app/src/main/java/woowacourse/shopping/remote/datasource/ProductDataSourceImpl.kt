package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ProductDataSource
import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.remote.api.ProductService
import woowacourse.shopping.remote.mapper.toData

class ProductDataSourceImpl(private val productService: ProductService) : ProductDataSource {
    override fun findProductById(id: Long): Result<ProductDto> =
        runCatching {
            productService.getProductsById(id = id.toInt()).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }

    override fun getPagingProduct(
        page: Int,
        pageSize: Int,
    ): Result<ProductsDto> =
        runCatching {
            productService.getProducts(page = page, size = pageSize).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }

    companion object {
        private var instance: ProductDataSourceImpl? = null

        fun setInstance(productService: ProductService) {
            instance = ProductDataSourceImpl(productService = productService)
        }

        fun getInstance(): ProductDataSourceImpl = requireNotNull(instance)
    }
}
