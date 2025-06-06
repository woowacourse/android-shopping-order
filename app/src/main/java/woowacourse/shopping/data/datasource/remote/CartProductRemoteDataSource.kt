package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.request.CartProductQuantityRequestDto
import woowacourse.shopping.data.dto.request.CartProductRequestDto
import woowacourse.shopping.data.dto.response.toCartProduct
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.data.service.CartProductApiService
import woowacourse.shopping.data.util.requireBody
import woowacourse.shopping.data.util.requireHeader
import woowacourse.shopping.domain.model.CartProduct

class CartProductRemoteDataSource(
    private val cartProductService: CartProductApiService,
) {
    suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> =
        runCatching {
            val responseBody =
                cartProductService.getPagedProducts(page = page, size = size).requireBody()
            val products = responseBody.content.map { it.toCartProduct() }
            val hasNext = responseBody.last.not()
            PagedResult(products, hasNext)
        }

    suspend fun insert(
        id: Int,
        quantity: Int,
    ): Result<Int> =
        runCatching {
            val locationHeader =
                cartProductService
                    .insert(body = CartProductRequestDto(id, quantity))
                    .requireHeader(HEADER_LOCATION)
            locationHeader.removePrefix(LOCATION_PREFIX).toInt()
        }

    suspend fun delete(id: Int): Result<Unit> =
        runCatching {
            cartProductService.delete(id = id).requireBody()
        }

    suspend fun getTotalQuantity(): Result<Int> =
        runCatching {
            cartProductService.getTotalQuantity().requireBody().quantity
        }

    suspend fun updateQuantity(
        id: Int,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartProductService
                .updateQuantity(
                    id = id,
                    body = CartProductQuantityRequestDto(quantity),
                ).requireBody()
        }

    companion object {
        private const val HEADER_LOCATION = "location"
        private const val LOCATION_PREFIX = "/cart-items/"
    }
}
