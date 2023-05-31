package woowacourse.shopping.data.cart

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.utils.RemoteHost
import java.io.IOException

class CartItemRemoteService(private val host: RemoteHost) : CartItemDataSource {
    private val client = OkHttpClient()
    override fun save(cartItem: CartItem, user: User, onFinish: (CartItem) -> Unit) {
        val path = "/cart-items"
        val jsonObject = JSONObject().apply {
            put("productId", cartItem.product.id)
        }
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder().url(host.url + path).post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${user.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val id =
                    response.header("Location")?.removePrefix("/cart-items/")?.toLong() ?: return
                val savedCartItem = CartItem(
                    id, cartItem.quantity, cartItem.product
                )
                onFinish(savedCartItem)
            }
        })
    }

    override fun findAll(user: User, onFinish: (List<CartItem>) -> Unit) {
        val path = "/cart-items"
        val request =
            Request.Builder().url(host.url + path).addHeader("Authorization", "Basic ${user.token}")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code != 200) return
                val body = response.body?.string()
                val jsonArray = JSONArray(body)
                val cartItems = (0 until jsonArray.length()).map {
                    val jsonObject = jsonArray.getJSONObject(it)
                    parseToCartItem(jsonObject)
                }
                onFinish(cartItems)
            }
        })
    }

    override fun findAll(limit: Int, offset: Int, user: User, onFinish: (List<CartItem>) -> Unit) {
        val path = "/cart-items"
        val request =
            Request.Builder().url(host.url + path).addHeader("Authorization", "Basic ${user.token}")
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code != 200) return
                val body = response.body?.string()
                val jsonArray = JSONArray(body)
                val cartItems = (0 until jsonArray.length()).map {
                    val jsonObject = jsonArray.getJSONObject(it)
                    parseToCartItem(jsonObject)
                }
                onFinish(cartItems.slice(offset until cartItems.size).take(limit))
            }
        })
    }

    override fun updateCountById(id: Long, count: Int, user: User, onFinish: () -> Unit) {
        val path = "/cart-items/$id"
        val jsonObject = JSONObject().apply {
            put("quantity", count)
        }
        val body = jsonObject.toString().toRequestBody()
        val request = Request.Builder().url(host.url + path).patch(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Basic ${user.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) onFinish()
            }
        })
    }

    override fun deleteById(id: Long, user: User, onFinish: () -> Unit) {
        val path = "/cart-items/$id"
        val request = Request.Builder().url(host.url + path).delete()
            .addHeader("Authorization", "Basic ${user.token}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 204) onFinish()
            }
        })
    }

    private fun parseToCartItem(jsonObject: JSONObject): CartItem {
        val id = jsonObject.getLong("id")
        val quantity = jsonObject.getInt("quantity")
        val jsonObject1 = jsonObject.getJSONObject("product")
        val product = parseToProduct(jsonObject1)
        return CartItem(id, quantity, product)
    }

    private fun parseToProduct(jsonObject: JSONObject): Product {
        val id = jsonObject.getLong("id")
        val name = jsonObject.getString("name")
        val price = jsonObject.getInt("price")
        val imageUrl = jsonObject.getString("imageUrl")
        return Product(id, name, price, imageUrl)
    }
}
