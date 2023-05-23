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
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL
import java.io.IOException
import kotlin.concurrent.thread

class ProductRemoteDataSource {
    private val okHttpClient: OkHttpClient

    init {
        thread {
            startMockWebServer()
        }
        okHttpClient = OkHttpClient()
    }

    fun getProducts(path: Int, onSuccess: (List<Product>) -> Unit) {
        val url = "http://localhost:8080/products/$path"
        val request = Request.Builder().url(url).build()
        val handler = Handler(Looper.myLooper()!!)
        val products = mutableListOf<Product>()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonArray = JSONArray(input)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    products.add(createProduct(jsonObject))
                }

                Thread.sleep(2000)
                handler.post {
                    onSuccess(products)
                }
            }
        })
    }

    private fun createProduct(response: JSONObject): Product {
        return Product(
            picture = URL(response.getString("imageUrl")),
            title = response.getString("name"),
            price = response.getInt("price")
        )
    }
}
