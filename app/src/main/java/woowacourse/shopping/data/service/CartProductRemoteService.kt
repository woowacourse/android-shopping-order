package woowacourse.shopping.data.service

import com.example.domain.model.CartProduct
import com.example.domain.model.Price
import com.example.domain.model.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.user.ServerInfo
import java.io.IOException
import java.net.URI

class CartProductRemoteService {
    private val baseUrl: String
        get() = ServerInfo.url

    private val token: String
        get() = ServerInfo.token

    fun requestCarts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        val request = Request.Builder().url("${baseUrl}cart-items")
            .addHeader("Authorization", "Basic $token").build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code >= 400) return onFailure()
                val responseBody = response.body?.string()
                response.close()

                val result = responseBody?.let {
                    parseJsonToCartProductList(it)
                } ?: emptyList()

                onSuccess(result)
            }
        })
    }

    fun requestAddCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        val bodyString = """ { 
                "productId": $productId 
            } 
        """.trimIndent()
        val body = bodyString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("${baseUrl}cart-items")
            .addHeader("Authorization", "Basic $token")
            .post(body).build()

        val thread = Thread {
            kotlin.runCatching { OkHttpClient().newCall(request).execute() }
                .onSuccess {
                    if (it.isSuccessful) {
                        val responseHeader = it.headers["Location"] ?: return@onSuccess onFailure()
                        val cartId = URI(responseHeader).path.substringAfterLast("/").toLong()
                        onSuccess(cartId)
                    } else {
                        onFailure()
                    }
                }
                .onFailure { onFailure() }
        }
        thread.start()
        thread.join()
    }

    fun requestChangeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        val bodyString = """ 
            { 
            "quantity": $count 
            } 
        """.trimIndent()
        val body = bodyString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("${baseUrl}cart-items/$cartId")
            .addHeader("Authorization", "Basic $token").patch(body).build()

        val thread = Thread {
            kotlin.runCatching { OkHttpClient().newCall(request).execute() }
                .onSuccess {
                    if (it.isSuccessful) return@onSuccess onSuccess(cartId)
                    onFailure()
                }
                .onFailure { onFailure() }
        }
        thread.start()
        thread.join()
    }

    fun requestDeleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        val request = Request.Builder()
            .url("${baseUrl}cart-items/$cartId")
            .addHeader("Authorization", "Basic $token").delete().build()

        val thread = Thread {
            kotlin.runCatching { OkHttpClient().newCall(request).execute() }
                .onSuccess {
                    if (it.isSuccessful) return@onSuccess onSuccess(cartId)
                    onFailure()
                }
                .onFailure { onFailure() }
        }
        thread.start()
        thread.join()
    }

    private fun parseJsonToCartProductList(responseString: String): List<CartProduct> {
        val jsonArray = JSONArray(responseString)
        val cartItems = mutableListOf<CartProduct>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getInt("id")
            val quantity = jsonObject.getInt("quantity")
            val productObject = jsonObject.getJSONObject("product")
            val product = parseJsonToProduct(productObject)

            val cartItem = CartProduct(id.toLong(), product, quantity, true)
            cartItems.add(cartItem)
        }
        return cartItems
    }

    private fun parseJsonToProduct(jsonProduct: JSONObject): Product {
        val id = jsonProduct.getInt("id")
        val name = jsonProduct.getString("name")
        val price = jsonProduct.getInt("price")
        val imageUrl = jsonProduct.getString("imageUrl")

        return Product(id.toLong(), name, imageUrl, Price(price))
    }
}
