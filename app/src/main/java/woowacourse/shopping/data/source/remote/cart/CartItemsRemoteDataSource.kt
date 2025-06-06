package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.CartRequest
import woowacourse.shopping.data.model.ItemCount
import woowacourse.shopping.data.source.remote.api.CartApiService

class CartItemsRemoteDataSource(
    private val api: CartApiService,
) : CartItemsDataSource {
    override suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<CartItemResponse> {
        val response = api.getCartItems(page = page, size = size)
        return if (response.isSuccessful) {
            Result.success(response.body() ?: CartItemResponse.EMPTY)
        } else {
            Result.failure(Exception(GET_CART_ID_ERROR_MESSAGE))
        }
    }

    override suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Long> {
        val request =
            CartRequest(
                productId = id,
                quantity = quantity,
            )

        val response = api.postCartItems(request = request)

        return if (response.isSuccessful) {
            val header = response.headers()
            val cartId = header["Location"]?.substringAfterLast("/")?.toLongOrNull()

            if (cartId != null) {
                Result.success(cartId)
            } else {
                Result.failure(Exception(POST_ERROR_MESSAGE))
            }
        } else {
            Result.failure(Exception("[ERROR] ${response.code()} - ${response.message()}"))
        }
    }

    override suspend fun deleteCartItem(id: Long): Result<Unit> {
        val response = api.deleteCartItems(id = id)

        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(DELETE_ERROR_MESSAGE))
        }
    }

    override suspend fun updateCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val response = api.patchCartItems(id = id, quantity = quantity)
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception(UPDATE_ERROR_MESSAGE))
        }
    }

    override suspend fun getCarItemsCount(): Result<ItemCount> {
        val response = api.getCartItemsCounts()

        return if (response.isSuccessful) {
            Result.success(response.body() ?: ItemCount.EMPTY)
        } else {
            Result.failure(Exception(GET_CART_ITEM_COUNT_ERROR_MESSAGE))
        }
    }

    companion object {
        private const val POST_ERROR_MESSAGE = "[ERROR] 장바구니 ID가 존재하지 않습니다."
        private const val GET_CART_ID_ERROR_MESSAGE = "[ERROR] 장바구니 ID를 불러오는 도중 오류가 발생했습니다."
        private const val DELETE_ERROR_MESSAGE = "[ERROR] 상품을 삭제하는 도중 오류가 발생했습니다."
        private const val UPDATE_ERROR_MESSAGE = "[ERROR] 상품 수량을 업데이트하는 도중 오류가 발생했습니다."
        private const val GET_CART_ITEM_COUNT_ERROR_MESSAGE = "[ERROR] 장바구니 상품의 수를 받아오는 도중 오류가 발생했습니다."
    }
}
