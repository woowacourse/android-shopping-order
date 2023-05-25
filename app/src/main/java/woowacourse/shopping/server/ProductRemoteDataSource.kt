package woowacourse.shopping.server

import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.Storage
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL
import java.io.IOException

class ProductRemoteDataSource {
    private val okHttpClient: OkHttpClient = OkHttpClient()

    fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH"
        val request = Request.Builder().url(url).build()
        val handler = Handler(Looper.myLooper()!!)
        val products = mutableListOf<Product>()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonArray = JSONArray(input)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    products.add(createProduct(jsonObject))
                }

                handler.post {
                    onSuccess(products)
                }
            }
        })
    }

    fun getProduct(id: Int, onSuccess: (Product) -> Unit, onFailure: () -> Unit) {
        val baseUrl = Server.getUrl(Storage.server)
        val url = "$baseUrl/$PATH/$id"
        val request = Request.Builder().url(url).build()
        val handler = Handler(Looper.myLooper()!!)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonObject = JSONObject(input)
                val product = createProduct(jsonObject)

                handler.post {
                    onSuccess(product)
                }
            }
        })
    }

    private fun createProduct(response: JSONObject): Product {
        return Product(
            id = response.getInt(KEY_ID),
            picture = URL(response.getString(KEY_IMAGE_URL)),
            title = response.getString(KEY_TITLE),
            price = response.getInt(KEY_PRICE)
        )
    }

    companion object {
        private const val PATH = "products"
        private const val KEY_ID = "id"
        private const val KEY_IMAGE_URL = "imageUrl"
        private const val KEY_TITLE = "name"
        private const val KEY_PRICE = "price"
    }
}
