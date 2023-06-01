package woowacourse.shopping.data.server

import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.Storage
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL
import java.io.IOException

class CartRemoteDataSourceImpl {
    private val okHttpClient: OkHttpClient = OkHttpClient()

    fun addCartProduct(id: Int, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH"
        val credentials = Storage.credential
        val json = JSONObject().put("productId", id)
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .addHeader(HEADER_AUTHORIZATION, "Basic $credentials")
            .post(body)
            .url(url)
            .build()
        val handler = Handler(Looper.myLooper()!!)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                handler.post {
                    val location = response.header("Location", "-1") ?: "-1"
                    val id = location.substringAfterLast("/").toInt()
                    onSuccess(id)
                }

                response.close()
            }
        })
    }

    fun getCartProducts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH"
        val credentials = Storage.credential
        val request = Request.Builder()
            .addHeader(HEADER_AUTHORIZATION, "Basic $credentials")
            .url(url)
            .build()
        val handler = Handler(Looper.myLooper()!!)
        val products = mutableListOf<CartProduct>()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonArray = JSONArray(input)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    products.add(createCartProduct(jsonObject))
                }

                handler.post {
                    onSuccess(products)
                }

                response.close()
            }
        })
    }

    private fun createCartProduct(response: JSONObject) = CartProduct(
        id = response.getInt(KEY_ID),
        quantity = response.getInt(KEY_QUANTITY),
        isChecked = true,
        product = createProduct(response)
    )

    private fun createProduct(response: JSONObject): Product {
        val product = response.getJSONObject(KEY_PRODUCT)
        return Product(
            id = product.getInt(KEY_PRODUCT_ID),
            picture = URL(product.getString(KEY_PICTURE)),
            title = product.getString(KEY_TITLE),
            price = product.getInt(KEY_PRICE)
        )
    }

    fun updateCartProductQuantity(id: Int, quantity: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH/$id"
        val credentials = Storage.credential
        val json = JSONObject().put("quantity", quantity)
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .addHeader(HEADER_AUTHORIZATION, "Basic $credentials")
            .patch(body)
            .url(url)
            .build()
        val handler = Handler(Looper.myLooper()!!)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                handler.post {
                    onSuccess()
                }

                response.close()
            }
        })
    }

    fun deleteCartProduct(cartProductId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH/$cartProductId"
        val credentials = Storage.credential

        val request = Request.Builder()
            .addHeader(HEADER_AUTHORIZATION, "Basic $credentials")
            .delete()
            .url(url)
            .build()
        val handler = Handler(Looper.myLooper()!!)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                handler.post { onSuccess() }

                response.close()
            }
        })
    }

    companion object {
        private const val PATH = "cart-items"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val KEY_ID = "id"
        private const val KEY_QUANTITY = "quantity"
        private const val KEY_PRODUCT = "product"
        private const val KEY_PRODUCT_ID = "id"
        private const val KEY_PICTURE = "imageUrl"
        private const val KEY_TITLE = "name"
        private const val KEY_PRICE = "price"
    }
}
