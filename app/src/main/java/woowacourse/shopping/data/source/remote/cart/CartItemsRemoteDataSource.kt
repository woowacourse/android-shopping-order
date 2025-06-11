package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.CartRequest
import woowacourse.shopping.data.model.ItemCount
import woowacourse.shopping.data.source.remote.api.CartApiService
import kotlin.text.get

class CartItemsRemoteDataSource(
    private val api: CartApiService,
) : CartItemsDataSource {
    override suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<CartItemResponse> =
        runCatching {
            val response = api.getCartItems(page = page, size = size)
            if (response.isSuccessful) {
                response.body() ?: CartItemResponse.EMPTY
            } else {
                throw Exception(GET_CART_ID_ERROR_MESSAGE)
            }
        }

    override suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Long> =
        runCatching {
            val request = CartRequest(productId = id, quantity = quantity)
            val response = api.postCartItems(request = request)

            if (response.isSuccessful) {
                val cartId =
                    response
                        .headers()["Location"]
                        ?.substringAfterLast("/")
                        ?.toLongOrNull()

                cartId ?: throw Exception(POST_ERROR_MESSAGE)
            } else {
                throw Exception(POST_CART_ERROR_MESSAGE)
            }
        }

    override suspend fun deleteCartItem(id: Long): Result<Unit> =
        runCatching {
            val response = api.deleteCartItems(id = id)
            if (!response.isSuccessful) throw Exception(DELETE_ERROR_MESSAGE)
        }

    override suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val response = api.patchCartItems(id = id, quantity = quantity)
            if (!response.isSuccessful) throw Exception(UPDATE_ERROR_MESSAGE)
        }

    override suspend fun getCarItemsCount(): Result<ItemCount> =
        runCatching {
            val response = api.getCartItemsCounts()
            if (response.isSuccessful) {
                response.body() ?: ItemCount.EMPTY
            } else {
                throw Exception(GET_CART_ITEM_COUNT_ERROR_MESSAGE)
            }
        }

    companion object {
        private const val POST_ERROR_MESSAGE = "[ERROR] 장바구니 ID가 존재하지 않습니다."
        private const val POST_CART_ERROR_MESSAGE = "[ERROR] 장바구니에 상품 추가를 실패했습니다."
        private const val GET_CART_ID_ERROR_MESSAGE = "[ERROR] 장바구니 ID를 불러오는 도중 오류가 발생했습니다."
        private const val DELETE_ERROR_MESSAGE = "[ERROR] 상품을 삭제하는 도중 오류가 발생했습니다."
        private const val UPDATE_ERROR_MESSAGE = "[ERROR] 상품 수량을 업데이트하는 도중 오류가 발생했습니다."
        private const val GET_CART_ITEM_COUNT_ERROR_MESSAGE =
            "[ERROR] 장바구니 상품의 수를 받아오는 도중 오류가 발생했습니다."
    }
}
