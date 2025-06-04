package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductDto
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.CartProductApiService

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProductDto>> {
        val response = cartProductService.getPagedProducts(page = page, size = size)

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val products = body.content
                val hasNext = !body.last
                Result.success(PagedResult(products, hasNext))
            } else {
                Result.success(PagedResult(emptyList(), false))
            }
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun insert(
        id: Int,
        quantity: Int,
    ): Result<Int> {
        val response = cartProductService.insert(body = CartProductRequestDto(id, quantity))

        return if (response.code() == SUCCESS_POST) {
            val cartProductId =
                response.headers()[HEADER_LOCATION]?.removePrefix(PREFIX_CART_ITEM)?.toInt()
                    ?: throw IllegalArgumentException()
            Result.success(cartProductId)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun delete(id: Int): Result<Unit> {
        val response = cartProductService.delete(id = id)

        return if (response.code() == SUCCESS_DELETE) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun deleteProductsByIds(ids: Set<Int>): Result<Unit> {
        val exceptions = mutableListOf<Throwable>()

        ids.forEach { id ->
            val result = delete(id)
            result
                .onFailure { throwable ->
                    exceptions.add(throwable)
                }
        }
        return if (exceptions.isNotEmpty()) {
            Result.failure(exceptions.first())
        } else {
            Result.success(Unit)
        }
    }

    suspend fun getTotalQuantity(): Result<Int> {
        val response = cartProductService.getTotalQuantity()

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body.quantity)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun updateQuantity(
        id: Int,
        quantity: Int,
    ): Result<Unit> {
        val response = cartProductService.updateQuantity(id = id, body = CartProductQuantityRequestDto(quantity))
        return if (response.code() == SUCCESS_PATCH) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    companion object {
        private const val SUCCESS_PATCH = 200
        private const val SUCCESS_POST = 201
        private const val SUCCESS_DELETE = 204
        private const val HEADER_LOCATION = "location"
        private const val PREFIX_CART_ITEM = "/cart-items/"
    }
}
