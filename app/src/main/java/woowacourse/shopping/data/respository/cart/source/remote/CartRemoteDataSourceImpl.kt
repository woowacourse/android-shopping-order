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
import woowacourse.shopping.data.model.Server
import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.product.Product
import java.io.IOException

class CartRemoteDataSourceImpl(
    private val url: Server.Url,
    private val token: Server.Token,
) : CartRemoteDataSource {
    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    ) {
        val client = OkHttpClient()
        val request =
            Request.Builder()
                .addHeader("Authorization", "Basic ${token.value}")
                .url(url.value + PATH_CART)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request Failed", e.toString())
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
    }

    override fun requestPatchCartItem(
        cartProduct: CartProduct,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        val client = OkHttpClient()
        val path = PATH_CART_PATCH + cartProduct.id
        val mediaType = "application/json".toMediaType()
        val json = JSONObject().put("quantity", cartProduct.count)
        val body = json.toString().toRequestBody(mediaType)

        val request =
            Request.Builder()
                .addHeader("Authorization", "Basic ${token.value}")
                .patch(body)
                .url(url.value + path)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request Failed", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    onSuccess()
                    return
                }
                onFailure()
            }
        })
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (Long) -> Unit,
    ) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val json = JSONObject().put("productId", productId)
        val body = json.toString().toRequestBody(mediaType)

        val request =
            Request.Builder()
                .addHeader("Authorization", "Basic ${token.value}")
                .post(body)
                .url(url.value + PATH_CART)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Request Failed", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 201) {
                    val location = response.headers["Location"] ?: return onFailure()
                    val cartId = location.substringAfterLast("cart-items/").toLong()
                    onSuccess(cartId)
                    return
                }
                onFailure()
            }
        })
    }

    override fun requestDeleteCartItem(cartId: Long) {
        val thread = Thread {
            val client = OkHttpClient()
            val path = PATH_CART_DELETE + cartId

            val request =
                Request.Builder()
                    .addHeader("Authorization", "Basic ${token.value}")
                    .delete()
                    .url(url.value + path)
                    .build()

            client.newCall(request).execute()
        }
        thread.start()
        thread.join()
    }

    private fun parseCartProductList(response: String): List<CartProduct> {
        val cartProducts = mutableListOf<CartProduct>()
        val jsonArray = JSONArray(response)

        for (index in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(index) ?: continue
            cartProducts.add(parseCartProduct(json))
        }

        return cartProducts
    }

    private fun parseCartProduct(json: JSONObject): CartProduct {
        val cartId = json.getLong("id")
        val count = json.getInt("quantity")
        val product = parseProduct(json.getJSONObject("product"))

        return CartProduct(cartId, product, count, true)
    }

    private fun parseProduct(json: JSONObject): Product {
        val id = json.getLong("id")
        val name = json.getString("name")
        val price = json.getInt("price")
        val image = json.getString("imageUrl")

        return Product(id, name, price, image)
    }

    companion object {
        private const val PATH_CART = "/cart-items"
        private const val PATH_CART_PATCH = "/cart-items/"
        private const val PATH_CART_DELETE = "/cart-items/"
    }
}
