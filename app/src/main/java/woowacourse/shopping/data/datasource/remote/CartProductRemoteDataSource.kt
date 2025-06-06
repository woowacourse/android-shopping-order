package woowacourse.shopping.data.datasource.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.CartProductQuantityResponseDto
import woowacourse.shopping.data.dto.response.CartProductResponseDto
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.data.util.requireBody
import woowacourse.shopping.data.util.requireHeader

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<CartProductResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                cartProductService.getPagedProducts(page = page, size = size).requireBody()
            }
        }

    suspend fun insert(
        id: Int,
        quantity: Int,
    ): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                val locationHeader =
                    cartProductService
                        .insert(body = CartProductRequestDto(id, quantity))
                        .requireHeader(HEADER_LOCATION)
                locationHeader.removePrefix(LOCATION_PREFIX).toInt()
            }
        }

    suspend fun delete(id: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                cartProductService.delete(id = id).requireBody()
            }
        }

    suspend fun getTotalQuantity(): Result<CartProductQuantityResponseDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                cartProductService.getTotalQuantity().requireBody()
            }
        }

    suspend fun updateQuantity(
        id: Int,
        quantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                cartProductService
                    .updateQuantity(
                        id = id,
                        body = CartProductQuantityRequestDto(quantity),
                    ).requireBody()
            }
        }

    companion object {
        private const val HEADER_LOCATION = "location"
        private const val LOCATION_PREFIX = "/cart-items/"
    }
}
