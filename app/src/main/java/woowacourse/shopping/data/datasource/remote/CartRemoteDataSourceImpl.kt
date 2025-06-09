package woowacourse.shopping.data.datasource.remote

import retrofit2.Response
import woowacourse.shopping.data.dto.cart.CartItemRequest
import woowacourse.shopping.data.dto.cart.UpdateCartRequest
import woowacourse.shopping.data.dto.cart.toDomain
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.domain.model.CartItem

class CartRemoteDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartRemoteDataSource {
    override suspend fun fetchTotalCount(): Result<Int> =
        handleApiCall(
            errorMessage = "장바구니 전체 수량 조회 실패",
            apiCall = { cartItemService.requestCartItemCount() },
            transform = { response ->
                response.body()?.quantity
                    ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun fetchPagedCartItems(
        page: Int,
        size: Int?,
    ): Result<List<CartItem>> =
        handleApiCall(
            errorMessage = "장바구니 조회 실패",
            apiCall = { cartItemService.requestCartItems(page = page, size = size) },
            transform = { response ->
                response.body()?.cartContent?.map { content -> content.toDomain() }
                    ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun insertCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Long> =
        handleApiCall(
            errorMessage = "장바구니 추가 실패",
            apiCall = { cartItemService.addCartItem(CartItemRequest(productId, quantity)) },
            transform = { response ->
                response.toIdOrNull()
                    ?: throw IllegalStateException("응답 헤더에 cartId가 없습니다.")
            },
        )

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        handleApiCall(
            errorMessage = "장바구니 수량 업데이트 실패",
            apiCall = {
                val request = UpdateCartRequest(quantity)
                cartItemService.updateCartItem(cartId, request)
            },
            transform = { response ->
                response.body() ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )

    override suspend fun deleteCartItemById(cartId: Long): Result<Unit> =
        handleApiCall(
            errorMessage = "아이템 삭제 실패",
            apiCall = { cartItemService.deleteCartItem(cartId) },
            transform = { response ->
                if (response.isSuccessful) {
                    Unit
                } else {
                    throw IllegalStateException("삭제 실패: ${response.code()}")
                }
            },
        )

    private fun <T> Response<T>.toIdOrNull(): Long? = headers()["LOCATION"]?.substringAfterLast("/")?.toLongOrNull()
}
