package woowacourse.shopping.data.source.remote.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.model.CartRequest
import woowacourse.shopping.data.model.ItemCount
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.util.enqueueResult

class CartItemsRemoteDataSource(
    private val api: CartApiService,
) : CartItemsDataSource {
    override fun getCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<CartItemResponse>) -> Unit,
    ) {
        api.getCartItems(page = page, size = size).enqueueResult(onResult)
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        val request =
            CartRequest(
                productId = id,
                quantity = quantity,
            )
        api.postCartItems(request = request).enqueue(
            object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void?>,
                    response: Response<Void?>,
                ) {
                    val header = response.headers()
                    val cartId = header["Location"]?.substringAfterLast("/")?.toLongOrNull()
                    if (response.isSuccessful && cartId != null) {
                        onResult(Result.success(cartId))
                    } else {
                        onResult(Result.failure(Exception(POST_ERROR_MESSAGE)))
                    }
                }

                override fun onFailure(
                    call: Call<Void?>,
                    t: Throwable,
                ) {
                    onResult(Result.failure(t))
                }
            },
        )
    }

    override fun deleteCartItem(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        api.deleteCartItems(id = id).enqueueResult(onResult)
    }

    override fun updateCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        api.patchCartItems(id = id, quantity = quantity).enqueueResult(onResult)
    }

    override fun getCarItemsCount(onResult: (Result<ItemCount>) -> Unit) {
        api.getCartItemsCounts().enqueueResult(onResult)
    }

    companion object {
        private const val POST_ERROR_MESSAGE = "[ERROR] 장바구니 ID가 존재하지 않습니다."
    }
}
