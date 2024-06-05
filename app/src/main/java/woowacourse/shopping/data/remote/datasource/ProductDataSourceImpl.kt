package woowacourse.shopping.data.remote.datasource

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.remote.service.ProductService

class ProductDataSourceImpl(private val productService: ProductService) : ProductDataSource {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
    ): Result<List<ProductDto>> {
        return runCatching {
            productService.getProducts(category, page, size, sort).execute().body()?.products
                ?: throw IllegalArgumentException()
        }
    }

    override fun getProductIsLast(
        category: String?,
        page: Int,
        size: Int,
        sort: String
    ): Result<Boolean> {
        return runCatching {
            productService.getProducts(category, page, size, sort).execute().body()?.last
                ?: throw IllegalArgumentException()
        }
    }


    override fun getProductById(productId: Int): Result<ProductDto> {
        return runCatching {
            productService.getProductById(productId).execute().body()
                ?: throw IllegalArgumentException()
        }
    }
}
