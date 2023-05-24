package woowacourse.shopping.data.cart

import com.example.domain.CartProduct
import com.example.domain.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CartRemoteService {
    private val okHttpClient = OkHttpClient()

    fun requestAllProducts(
        url: String,
        port: String = "8080",
        user: String = BANDAL,
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        val baseUrl = "$url:$port"
        val requestUrl = "$baseUrl/cart-items"
        val request = Request.Builder()
            .addHeader("Authorization", "Basic $user")
            .url(requestUrl).build()

        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (400 <= response.code) return onFailure()
                    val responseBody = response.body?.string()
                    response.close()

                    val result: List<CartProduct> = responseBody?.let {
                        parseJsonToCartProductList(it)
                    } ?: emptyList()

                    onSuccess(result)
                }
            }
        )
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

            val cartItem = CartProduct(
                id = id,
                quantity = quantity,
                productId = product.id,
                productImageUrl = product.imageUrl,
                productName = product.name,
                productPrice = product.price,
                isPicked = true
            )
            cartItems.add(cartItem)
        }
        return cartItems
    }

    private fun parseJsonToProduct(jsonObject: JSONObject): Product {
        val id = jsonObject.getInt("id")
        val name = jsonObject.getString("name")
        val imageUrl = jsonObject.getString("imageUrl")
        val price = jsonObject.getInt("price")

        return Product(id = id, name = name, imageUrl = imageUrl, price = price)
    }

    companion object {
        private const val BANDAL = "bm8xbXNoMTIxN0BnbWFpbC5jb206MTIzNDU="
    }
}
