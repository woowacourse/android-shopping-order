package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ProductRemoteDataSource
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException
import kotlin.concurrent.thread

class ProductRemoteDataSourceImpl(private val service: ProductService) : ProductRemoteDataSource {
    override fun getProductsByOffset(
        page: Int,
        size: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            var productsDto: ResponseProductsGetDto? = null
            thread {
                productsDto = service.getProductsByOffset(page = page, size = size).execute().body()
            }.join()
            productsDto ?: throw ShoppingException(ShoppingError.ProductNotFound)
        }

    override fun getProductsByCategory(
        category: String,
        page: Int,
    ): Result<ResponseProductsGetDto> =
        runCatching {
            var productsDto: ResponseProductsGetDto? = null
            thread {
                productsDto =
                    service.getProductsByCategory(category = category, page = page).execute().body()
            }.join()
            productsDto ?: throw ShoppingException(ShoppingError.ProductNotFound)
        }

    override fun getProductsById(id: Long): Result<ResponseProductIdGetDto> =
        runCatching {
            var productDto: ResponseProductIdGetDto? = null
            thread {
                productDto = service.getProductsById(id = id).execute().body()
            }.join()
            productDto ?: throw ShoppingException(ShoppingError.ProductNotFoundWithId(id))
        }
}
