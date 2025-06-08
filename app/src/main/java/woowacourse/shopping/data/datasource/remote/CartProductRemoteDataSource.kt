package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductDto
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.CartProductApiService
import java.net.HttpURLConnection

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProductDto>> {
        val result = cartProductService.getPagedProducts(page = page, size = size)

        return result.mapCatching { dto ->
            if (dto == null) return@mapCatching PagedResult(emptyList(), false)
            val products = dto.content
            val hasNext = !dto.last
            PagedResult(products, hasNext)
        }
    }

    suspend fun insert(
        id: Int,
        quantity: Int,
    ): Result<Int> {
        val response = cartProductService.insert(body = CartProductRequestDto(id, quantity))

        return if (response.code() == HttpURLConnection.HTTP_CREATED) {
            val cartProductId =
                response.headers()[HEADER_LOCATION]?.removePrefix(PREFIX_CART_ITEM)?.toInt()
                    ?: throw IllegalArgumentException()
            Result.success(cartProductId)
        } else {
            Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
        }
    }

    suspend fun delete(id: Int): Result<Unit> {
        return cartProductService.delete(id = id)
    }

    suspend fun getTotalQuantity(): Result<Int> {
        val result = cartProductService.getTotalQuantity()

        return result.mapCatching { dto ->
            dto.quantity
        }
    }

    suspend fun updateQuantity(
        id: Int,
        quantity: Int,
    ): Result<Unit> {
        return cartProductService.updateQuantity(id = id, body = CartProductQuantityRequestDto(quantity))
    }

    companion object {
        private const val HEADER_LOCATION = "location"
        private const val PREFIX_CART_ITEM = "/cart-items/"
    }
}
