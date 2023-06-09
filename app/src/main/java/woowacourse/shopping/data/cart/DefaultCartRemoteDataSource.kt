package woowacourse.shopping.data.cart

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.cart.request.PatchCartProductQuantityRequest
import woowacourse.shopping.data.cart.request.PostCartProductRequest
import woowacourse.shopping.data.cart.response.GetCartProductResponse
import woowacourse.shopping.data.dto.mapper.CartProductMapper.toDomain
import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.domain.CartProduct

class DefaultCartRemoteDataSource(private val service: CartService): CartRemoteDataSource {
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: (String) -> Unit) {
        service.requestCartProducts().enqueue(object : Callback<List<GetCartProductResponse>> {
            override fun onResponse(
                call: Call<List<GetCartProductResponse>>,
                response: Response<List<GetCartProductResponse>>
            ) {
                if(response.isSuccessful) {
                    postToMainHandler { onSuccess(response.body()?.map { it.toDomain() } ?: emptyList()) }
                }else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_GET_PRODUCTS_FAILED }) }
                }
            }

            override fun onFailure(call: Call<List<GetCartProductResponse>>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_GET_PRODUCTS_FAILED) }
            }
        })
    }

    override fun addCartProduct(id: Int, quantity: Int, onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        val requestBody = PostCartProductRequest(id, quantity)
        service.createCartProduct(requestBody).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val header = response.headers()
                val cartId = header["Location"]?.substringAfterLast("/")?.toIntOrNull()
                if(response.isSuccessful && cartId != null) {
                    postToMainHandler { onSuccess(cartId) }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_ADD_PRODUCT_FAILED }) }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_ADD_PRODUCT_FAILED) }
            }
        })
    }

    override fun updateCartProductQuantity(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val requestBody = PatchCartProductQuantityRequest(quantity)
        service.updateCartProductQuantity(id, requestBody).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful) {
                    postToMainHandler { onSuccess() }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_UPDATE_QUANTITY_FAILED }) }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_UPDATE_QUANTITY_FAILED) }
            }
        })
    }

    override fun deleteCartProduct(
        cartProductId: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        service.deleteCartProduct(cartProductId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if(response.isSuccessful) {
                    postToMainHandler { onSuccess() }
                }
                else {
                    postToMainHandler { onFailure(response.message().ifBlank { MESSAGE_DELETE_FAILED }) }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                postToMainHandler { onFailure(MESSAGE_DELETE_FAILED) }
            }
        })
    }

    private fun postToMainHandler(block: () -> Unit) {
        mainHandler.post {
            block()
        }
    }

    companion object {
        private const val MESSAGE_GET_PRODUCTS_FAILED = "상품을 불러오는데 실패했습니다."
        private const val MESSAGE_ADD_PRODUCT_FAILED = "장바구니에 상품을 추가하는데 실패했습니다."
        private const val MESSAGE_UPDATE_QUANTITY_FAILED = "장바구니 상품 수량을 업데이트하는데 실패했습니다"
        private const val MESSAGE_DELETE_FAILED = "장바구니 상품을 삭제하는데 실패했습니다"
    }
}