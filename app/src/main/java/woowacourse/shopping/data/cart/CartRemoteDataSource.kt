package woowacourse.shopping.data.cart

import retrofit2.Call
import woowacourse.shopping.data.cart.model.CartDataModel
import woowacourse.shopping.data.common.model.BaseResponse

interface CartRemoteDataSource {
    fun addCartItem(productId: Int): Call<BaseResponse<Unit>>
    fun deleteCartItem(cartId: Int): Call<BaseResponse<CartDataModel>>
    fun updateCartItemQuantity(cartId: Int, count: Int): Call<BaseResponse<Unit>>
    fun getAllCartProductsInfo(): Call<BaseResponse<List<CartDataModel>>>
}
