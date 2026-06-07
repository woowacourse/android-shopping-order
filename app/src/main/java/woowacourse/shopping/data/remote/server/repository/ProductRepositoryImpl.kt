package woowacourse.shopping.data.remote.server.repository

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.dto.product.toDomain
import woowacourse.shopping.data.remote.server.dto.products.toDomain
import woowacourse.shopping.data.remote.server.service.ProductService
import woowacourse.shopping.domain.Product

class ProductRepositoryImpl(
    private val productService: ProductService,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        pageSize: Int,
    ): ApiResult<List<Product>> =
        try {
            val response =
                productService.requestProducts(
                    page = page,
                    size = pageSize,
                )
            ApiResult.Success(response.content.map { it.toDomain() })
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun getProduct(id: Long): ApiResult<Product> =
        try {
            val response = productService.requestProduct(id)
            ApiResult.Success(response.toDomain())
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun getCategoryProducts(
        page: Int,
        pageSize: Int,
        category: String,
    ): ApiResult<List<Product>> =
        try {
            val response = productService.requestCategoryProducts(category = category)
            ApiResult.Success(response.content.map { it.toDomain() })
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
}
