package woowacourse.shopping.data.datasource.product

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.ShoppingOkHttpClient
import woowacourse.shopping.data.ShoppingOkHttpClient.buildRequest
import woowacourse.shopping.data.model.Page
import woowacourse.shopping.data.model.Price
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.server.GET
import woowacourse.shopping.server.ShoppingMockWebServer
import java.io.IOException
import java.util.concurrent.CountDownLatch

class RemoteProductDataSource : ProductDataSource.Remote {
    private val shoppingMockServer: ShoppingMockWebServer = ShoppingMockWebServer()
    private var BASE_URL: String

    init {
        shoppingMockServer.start()
        shoppingMockServer.join()
        BASE_URL = shoppingMockServer.baseUrl
    }

    override fun getProductByPage(page: Page): List<Product> {
        shoppingMockServer.join()
        val url = "${BASE_URL}/products?start=${page.start}&count=${page.sizePerPage}"

        val products = mutableListOf<Product>()
        val countDownLatch = CountDownLatch(1)

        ShoppingOkHttpClient.newCall(buildRequest(url, GET)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonArray = JSONArray(input)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    products.add(convertToProduct(jsonObject))
                }
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
        return products
    }

    override fun findProductById(id: Int): Product? {
        shoppingMockServer.join()
        val url = "${BASE_URL}/products?productId=${id}"
        val countDownLatch = CountDownLatch(1)
        var product: Product? = null

        ShoppingOkHttpClient.newCall(buildRequest(url, GET)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                val input = response.body?.string()
                val jsonObject = JSONObject(input)
                if (jsonObject.getInt("id") == id) {
                    product = convertToProduct(jsonObject)
                }
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
        return product
    }

    private fun convertToProduct(response: JSONObject): Product = Product(
        id = response.getInt("id"),
        imageUrl = response.getString("imageUrl"),
        name = response.getString("name"),
        price = Price(response.getInt("price"))
    )
}
