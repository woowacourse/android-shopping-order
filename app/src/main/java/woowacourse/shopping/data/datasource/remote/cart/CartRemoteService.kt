package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.Price
import com.example.domain.model.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.datasource.remote.ServerInfo
import java.io.IOException
import java.util.concurrent.CountDownLatch

class CartRemoteService(private val credential: String) {

    private val okHttpClient = OkHttpClient()

    private val baseUrl = ServerInfo.currentBaseUrl
    private val url = "$baseUrl/cart-items"

    fun loadAll(): List<CartProduct> {
        val request = Request.Builder().url(url)
            .header("Authorization", "Basic $credential").build()

        val latch = CountDownLatch(1)
        val cartProducts = mutableListOf<CartProduct>()
        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    latch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) latch.countDown()

                    val responseBody = response.body?.string()
                    response.close()

                    val result = responseBody?.let {
                        parseJsonToCartProducts(responseBody)
                    } ?: emptyList()

                    cartProducts.addAll(result)
                    latch.countDown()
                }
            }
        )
        latch.await()
        return cartProducts
    }

    private fun parseJsonToCartProducts(json: String): List<CartProduct> {
        val products = mutableListOf<CartProduct>()
        val jsonProducts = JSONArray(json)

        for (i in 0 until jsonProducts.length()) {
            val jsonCartProduct = jsonProducts.getJSONObject(i)

            val cartId = jsonCartProduct.getInt("id")
            val count = jsonCartProduct.getInt("quantity")
            val jsonProduct = jsonCartProduct.getJSONObject("product")
            val productId = jsonProduct.getInt("id").toLong()
            val productName = jsonProduct.getString("name")
            val productPrice = jsonProduct.getInt("price")
            val productImageUrl = jsonProduct.getString("imageUrl")

            val product = Product(productId, productName, productImageUrl, Price(productPrice))
            val cartProduct = CartProduct(cartId.toLong(), product, count, true)
            products.add(cartProduct)
        }

        return products
    }

    fun addCartProduct(productId: Int): Int {

        val data = JSONObject().put("productId", "$productId").toString()
        val formBody: RequestBody = data.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder().url(url).post(formBody)
            .header("Authorization", "Basic $credential").build()

        var cartItemId = -1

        val latch = CountDownLatch(1)
        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    latch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) latch.countDown()

                    val location = response.headers["Location"]
                    location?.filter { it.isDigit() }?.let { cartItemId = it.toInt() }

                    latch.countDown()
                }
            }
        )
        latch.await()
        return cartItemId
    }

    fun updateCartProductCount(cartProductId: Int, count: Int) {

        val data = JSONObject().put("quantity", count).toString()
        val formBody: RequestBody = data.toRequestBody("application/json".toMediaTypeOrNull())

        val requestUrl = "$url/$cartProductId"
        val request = Request.Builder().url(requestUrl).patch(formBody)
            .header("Authorization", "Basic $credential").build()

        val latch = CountDownLatch(1)
        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    latch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) latch.countDown()

                    latch.countDown()
                }
            }
        )
        latch.await()
    }

    fun deleteCartProduct(cartProductId: Int) {

        val requestUrl = "$url/$cartProductId"
        val request = Request.Builder().url(requestUrl).delete()
            .header("Authorization", "Basic $credential").build()

        val latch = CountDownLatch(1)
        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    latch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) latch.countDown()

                    latch.countDown()
                }
            }
        )
        latch.await()
    }
}
