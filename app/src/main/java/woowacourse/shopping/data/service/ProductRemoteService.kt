package woowacourse.shopping.data.service

import com.example.domain.model.Price
import com.example.domain.model.Product
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class ProductRemoteService {

    private val okHttpClient = OkHttpClient()

    fun request(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        val baseUrl = ServerInfo.currentBaseUrl
        val url = "$baseUrl/products"
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code >= 400) return onFailure()
                    val responseBody = response.body?.string()
                    response.close()

                    val result = responseBody?.let {
                        parseJsonToProducts(responseBody)
                    } ?: emptyList()

                    onSuccess(result)
                }
            }
        )
    }

    private fun parseJsonToProducts(json: String): List<Product> {
        val products = mutableListOf<Product>()
        val jsonProducts = JSONArray(json)

        for (i in 0 until jsonProducts.length()) {
            val jsonProduct = jsonProducts.getJSONObject(i)

            val id = jsonProduct.getInt("id")
            val name = jsonProduct.getString("name")
            val imageUrl = jsonProduct.getString("imageUrl")
            val price = Price(jsonProduct.getInt("price"))

            val product = Product(id.toLong(), name, imageUrl, price)
            products.add(product)
        }

        return products
    }
}
