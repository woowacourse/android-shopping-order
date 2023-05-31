package woowacourse.shopping.data.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import woowacourse.shopping.data.product.ProductDataSource2
import woowacourse.shopping.data.product.ProductEntity
import java.io.IOException

class ProductApi(private val baseUrl: String) : ProductDataSource2 {
    private val client = OkHttpClient.Builder().build()
    private val requestFactory = RequestFactory(baseUrl)

    private val products = mutableListOf<ProductEntity>()

    override fun getProductEntity(id: Long): ProductEntity? {
        return products.find { it.id == id }
    }

    override fun getProductEntities(
        unit: Int,
        lastId: Long,
        callback: (List<ProductEntity>?) -> Unit,
    ) {
        client.newCall(requestFactory.getGetRequest("/products")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                products.clear()
                // products.addAll()
            }
        })
    }

    override fun addProductEntity(name: String, price: Int, itemImage: String): Long {
        TODO("Not yet implemented")
    }

    override fun isLastProductEntity(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val AUTH_KEY = "Authorization"
        private const val USER_EMAIL_INFO = "Basic YUBhLmNvbToxMjM0"
    }
}
