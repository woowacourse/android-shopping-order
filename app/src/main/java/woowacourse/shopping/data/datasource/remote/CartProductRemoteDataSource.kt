package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.toCartProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.domain.model.CartProduct

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> =
        try {
            val response = cartProductService.getPagedProducts(page = page, size = size)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val products = body.content.map { it.toCartProduct() }
                    val hasNext = body.last.not()
                    Result.success(PagedResult(products, hasNext))
                } ?: Result.success(PagedResult(emptyList(), false))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun insert(
        id: Int,
        quantity: Int,
    ): Result<Int> =
        try {
            val response = cartProductService.insert(body = CartProductRequestDto(id, quantity))
            if (response.isSuccessful) {
                val cartProductId =
                    response.headers()["location"]?.removePrefix("/cart-items/")?.toInt()
                        ?: throw IllegalArgumentException()
                Result.success(cartProductId)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun delete(id: Int): Result<Unit> =
        try {
            val response = cartProductService.delete(id = id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getTotalQuantity(): Result<Int> =
        try {
            val response = cartProductService.getTotalQuantity()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Result.success(body.quantity)
                } ?: Result.success(0)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateQuantity(
        id: Int,
        quantity: Int,
    ): Result<Unit> =
        try {
            val response =
                cartProductService.updateQuantity(
                    id = id,
                    body = CartProductQuantityRequestDto(quantity),
                )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
