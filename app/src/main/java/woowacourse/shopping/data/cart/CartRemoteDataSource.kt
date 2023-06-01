package woowacourse.shopping.data.cart

import retrofit2.Call
import woowacourse.shopping.data.common.BaseResponse

interface CartRemoteDataSource {
    fun addCartItem(productId: Int): Call<Unit>
    fun deleteCartItem(cartId: Int): Call<Unit>
    fun updateCartItemQuantity(cartId: Int, count: Int): Call<Unit>
    fun getAllCartProductsInfo(): Call<BaseResponse<List<CartDataModel>>>
}
