package woowacourse.shopping.data.respository.cart.source.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.BASE_URL_JENNA
import woowacourse.shopping.data.model.CartEntity2
import woowacourse.shopping.data.model.ProductEntity
import java.io.IOException

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartEntity2>) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val host = BASE_URL_JENNA
            val path = CART
            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic $TOCKEN_JENNA")
                    .url(host + path)
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return onFailure()
                        onSuccess(parseCartProductList(body))
                        return
                    }
                    onFailure()
                }
            })
        }.start()
    }

    private fun parseCartProductList(response: String): List<CartEntity2> {
        val cartProducts = mutableListOf<CartEntity2>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            cartProducts.add(parseCartProduct(json))
        }

        return cartProducts
    }

    private fun parseCartProduct(json: JSONObject): CartEntity2 {
        val cartId = json.getLong("id")
        val quantity = json.getInt("quantity")
        val product = json.getJSONObject("product")
        val productEntity = parseProduct(product)

        return CartEntity2(cartId, quantity, productEntity)
    }

    private fun parseProduct(json: JSONObject): ProductEntity {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        return ProductEntity(id, name, price, image)
    }

    companion object {
        private const val TOCKEN_POI = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
        private const val TOCKEN_JENNA = "YUBhLmNvbToxMjM0"
        private const val CART = "/cart-items"
    }
}
