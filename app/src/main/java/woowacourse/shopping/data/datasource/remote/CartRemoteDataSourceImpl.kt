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
        runCatching {
            val response = cartItemService.requestCartItemCount()
            if (!response.isSuccessful) {
                throw Exception("장바구니 전체 수량 조회 실패: ${response.code()} ${response.message()}")
            }

            val quantity =
                response.body()?.quantity ?: throw IllegalStateException("응답 바디가 null입니다.")
            quantity
        }.recoverCatching {
            throw Exception("인터넷 연결을 확인해주세요.")
        }

    override suspend fun fetchPagedCartItems(
        page: Int,
        size: Int?,
    ): Result<List<CartItem>> =
        runCatching {
            val response = cartItemService.requestCartItems(page = page, size = size)
            if (!response.isSuccessful) {
                throw Exception("장바구니 조회 실패: ${response.code()} ${response.message()}")
            }

            val cartItems =
                response.body()?.cartContent?.map { it.toDomain() }
                    ?: throw IllegalStateException("응답 바디가 null입니다.")
            cartItems
        }.recoverCatching {
            throw Exception("인터넷 연결을 확인해주세요.")
        }

    override suspend fun insertCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Long> =
        runCatching {
            val request = CartItemRequest(productId, quantity)
            val response = cartItemService.addCartItem(request)
            if (!response.isSuccessful) {
                throw Exception("장바구니 추가 실패: ${response.code()} ${response.message()}")
            }

            val cartId =
                response.toIdOrNull() ?: throw IllegalStateException("응답 헤더에 cartId가 없습니다.")
            cartId
        }.recoverCatching {
            throw Exception("인터넷 연결을 확인해주세요.")
        }

    override suspend fun updateQuantity(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val request = UpdateCartRequest(quantity)
            val response = cartItemService.updateCartItem(cartId, request)
            if (!response.isSuccessful) {
                throw Exception("장바구니 수량 업데이트 실패: ${response.code()} ${response.message()}")
            }
        }.recoverCatching {
            throw Exception("인터넷 연결을 확인해주세요.")
        }

    override suspend fun deleteCartItemById(cartId: Long): Result<Unit> =
        runCatching {
            val response = cartItemService.deleteCartItem(cartId)
            if (!response.isSuccessful) {
                throw Exception("아이템 삭제 실패: ${response.code()} ${response.message()}")
            }
        }.recoverCatching {
            throw Exception("인터넷 연결을 확인해주세요.")
        }

    private fun <T> Response<T>.toIdOrNull(): Long? = headers()["LOCATION"]?.substringAfterLast("/")?.toLongOrNull()
}
