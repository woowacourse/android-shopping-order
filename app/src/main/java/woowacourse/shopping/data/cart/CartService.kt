package woowacourse.shopping.data.cart

import android.util.Log
import com.google.gson.GsonBuilder
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.common.SharedPreferencesDb
import woowacourse.shopping.presentation.serversetting.ServerSettingActivity

class CartService(private val sharedPreferences: SharedPreferencesDb) : CartRemoteDataSource {

//    override fun addProduct(productId: Int) {
//        val requestBody = ApiClient.getRequestBody(
//            name = "productId", value = productId.toString()
//        )
//        Log.d("test", "addProduct: $requestBody")
//        val thread = Thread {
//            val response = ApiClient.postApiService(
//                path = PATH,
//                body = requestBody,
//                header = getAuthToken()
//            )
//            Log.d("codeAdd", response.code.toString())
//        }
//        thread.start()
//        thread.join()
//    }

    override fun addProduct(productId: Int) {
        val requestBody = ApiClient().getRequestBody(
            name = "productId", value = productId.toString()
        )
        Log.d("test", "addProduct: $requestBody")
        val thread = Thread() {
            val response = ApiClient().postApiService(
                path = PATH,
                body = requestBody,
                header = getAuthToken()
            )
            Log.d("codeAdd", response.code.toString())
        }
        thread.start()
        thread.join()
    }

    override fun deleteCartProduct(productId: Int) {
        val thread = Thread {
            ApiClient().deleteApiService(
                path = "$PATH/$productId",
                header = getAuthToken()
            )
        }
        thread.start()
        thread.join()
    }

    override fun updateProductCount(cartProductInfo: CartLocalDataModel) {
        val requestBody = ApiClient().getRequestBody(
            name = "quantity", value = cartProductInfo.count.toString()
        )
        val thread = Thread() {
            ApiClient().patchApiService(
                path = "$PATH/${cartProductInfo.productId}",
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
            Log.d("code", response.code.toString())
            val responseBody = response.body?.string()
            Log.d("test", "getAllProducts: $responseBody")
            cartProducts = parseToCartProducts(responseBody)
        }
        thread.start()
        thread.join()
        return cartProducts
    }

    private fun parseToCartProducts(responseBody: String?): List<CartRemoteDataModel> {
        val gson = GsonBuilder().create()
        Log.d("test", "parseToCartProducts: $responseBody")
        return gson.fromJson(responseBody, Array<CartRemoteDataModel>::class.java).toList()
    }

    private fun getAuthToken() =
        sharedPreferences.getString(ServerSettingActivity.AUTHORIZATION_TOKEN, "")

    companion object {
        private const val PATH = "cart-items"
    }
}
