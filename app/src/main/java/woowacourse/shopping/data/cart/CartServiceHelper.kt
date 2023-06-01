package woowacourse.shopping.data.cart

import retrofit2.Call
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.cart.model.AddCartRequestBody
import woowacourse.shopping.data.cart.model.CartDataModel
import woowacourse.shopping.data.cart.model.UpdateQuantityRequestBody
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

class CartServiceHelper(private val sharedPreferences: SharedPreferencesDb) : CartRemoteDataSource {
    private val retrofitService = ApiClient.client
        .create(CartService::class.java)

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    override fun addCartItem(productId: Int): Call<BaseResponse<Unit>> {
        return retrofitService.addCartItem(
            credentials = getAuthToken(),
            addCartRequestBody = AddCartRequestBody(productId = productId),
        )
    }

    override fun deleteCartItem(cartItemId: Int): Call<BaseResponse<CartDataModel>> {
        return retrofitService.deleteCartItem(
            credentials = getAuthToken(),
            cartItemId = cartItemId,
        )
    }

    override fun updateCartItemQuantity(cartItemId: Int, quantity: Int): Call<BaseResponse<Unit>> {
        return retrofitService.updateCartItemCount(
            credentials = getAuthToken(),
            cartItemId = cartItemId,
            quantityBody = UpdateQuantityRequestBody(quantity = quantity),
        )
    }

    override fun getAllCartProductsInfo(): Call<BaseResponse<List<CartDataModel>>> {
        return retrofitService.getAllCartItems(
            credentials = getAuthToken(),
        )
    }
}
