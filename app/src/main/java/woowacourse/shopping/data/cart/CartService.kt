package woowacourse.shopping.data.cart

import com.google.gson.GsonBuilder
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.presentation.serversetting.ServerSettingPresenter

class CartService(private val sharedPreferences: SharedPreferencesDb) : CartRemoteDataSource {
    override fun addProduct(productId: Int) {
        val requestBody = ApiClient().getRequestBody(
            name = "productId", value = productId.toString()
        )
        val thread = Thread() {
            ApiClient().postApiService(
                path = PATH,
                body = requestBody,
                header = getAuthToken()
            )
        }
        thread.start()
        thread.join()
    }

    override fun deleteCartProduct(cartId: Int) {
        val thread = Thread {
            ApiClient().deleteApiService(
                path = "$PATH/$cartId",
                header = getAuthToken()
            )
        }
        thread.start()
        thread.join()
    }

    override fun updateProductCount(cartId: Int, count: Int) {
        val requestBody = ApiClient().getRequestBody(
            name = "quantity", value = count.toString()
        )
        val thread = Thread() {
            ApiClient().patchApiService(
                path = "$PATH/$cartId",
                body = requestBody,
                header = getAuthToken()
            )
        }
        thread.start()
        thread.join()
    }

    override fun getAllCartProductsInfo(): List<CartRemoteDataModel> {
        var cartProducts = emptyList<CartRemoteDataModel>()
        val thread = Thread {
            val response = ApiClient().getApiService(
                path = PATH,
                header = getAuthToken()
            )
            val responseBody = response.body?.string()
            cartProducts = parseToCartProducts(responseBody)
        }
        thread.start()
        thread.join()
        return cartProducts
    }

    private fun parseToCartProducts(responseBody: String?): List<CartRemoteDataModel> {
        val gson = GsonBuilder().create()
        return gson.fromJson(responseBody, Array<CartRemoteDataModel>::class.java).toList()
    }

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingPresenter.AUTHORIZATION_TOKEN, "")

    companion object {
        private const val PATH = "cart-items"
    }
}
