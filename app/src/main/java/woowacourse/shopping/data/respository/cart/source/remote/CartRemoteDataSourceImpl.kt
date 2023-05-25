package woowacourse.shopping.data.respository.cart.source.remote

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.model.Server
import java.io.IOException

class CartRemoteDataSourceImpl(
    private val server: Server,
) : CartRemoteDataSource {
    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic ${Server.TOKEN}")
                    .url(server.url + PATH_CART)
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

    override fun requestPatchCartItem(
        cartEntity: CartRemoteEntity,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val path = PATH_CART_PATCH + cartEntity.id
            val mediaType = "application/json".toMediaType()
            val json = JSONObject().put("quantity", cartEntity.quantity)
            val body = json.toString().toRequestBody(mediaType)

            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic ${Server.TOKEN}")
                    .patch(body)
                    .url(server.url + path)
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        onSuccess()
                        return
                    }
                    onFailure()
                }
            })
        }.start()
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        Thread {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val json = JSONObject().put("productId", productId)
            val body = json.toString().toRequestBody(mediaType)

            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic ${Server.TOKEN}")
                    .post(body)
                    .url(server.url + PATH_CART)
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("krrong", e.toString())
                }
                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 201) {
                        onSuccess()
                        return
                    }
                    onFailure()
                }
            })
        }.start()
    }

    override fun requestDeleteCartItem(cartId: Long) {
        val thread = Thread {
            val client = OkHttpClient()
            val path = PATH_CART_DELETE + cartId

            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic ${Server.TOKEN}")
                    .delete()
                    .url(server.url + path)
                    .build()

            client.newCall(request).execute()
        }

        thread.start()
        thread.join()
    }

    private fun parseCartProductList(response: String): List<CartRemoteEntity> {
        val cartProducts = mutableListOf<CartRemoteEntity>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            cartProducts.add(parseCartProduct(json))
        }

        return cartProducts
    }

    private fun parseCartProduct(json: JSONObject): CartRemoteEntity {
        val cartId = json.getLong("id")
        val quantity = json.getInt("quantity")
        val product = json.getJSONObject("product")
        val productEntity = parseProduct(product)

        return CartRemoteEntity(cartId, quantity, productEntity)
    }

    private fun parseProduct(json: JSONObject): ProductEntity {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        return ProductEntity(id, name, price, image)
    }

    companion object {
        private const val PATH_CART = "/cart-items"
        private const val PATH_CART_PATCH = "/cart-items/"
        private const val PATH_CART_DELETE = "/cart-items/"
    }
}
