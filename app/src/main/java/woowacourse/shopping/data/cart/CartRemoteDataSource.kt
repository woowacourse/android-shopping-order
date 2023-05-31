package woowacourse.shopping.data.cart

import retrofit2.Call
import woowacourse.shopping.data.cart.model.CartDataModel

interface CartRemoteDataSource {
    fun addCartItem(productId: Int): Call<Unit>
    fun deleteCartItem(cartId: Int): Call<CartDataModel>
    fun updateCartItemQuantity(cartId: Int, count: Int): Call<Unit>
    fun getAllCartProductsInfo(): Call<List<CartDataModel>>
}
