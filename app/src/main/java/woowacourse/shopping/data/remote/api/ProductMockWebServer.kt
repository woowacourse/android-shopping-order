package woowacourse.shopping.data.remote.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockWebServer
import woowacourse.shopping.domain.model.product.Product

class ProductMockWebServer : ProductServerApi {
    private val mockWebServer = MockWebServer()
    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val gson = Gson()

    override fun start() {
        mockWebServer.dispatcher = ProductMockWebServerDispatcher
        mockWebServer.start(ProductMockWebServerPath.BASE_PORT)
    }

    override fun find(id: Long): Product {
        val request =
            Request.Builder()
                .url(ProductMockWebServerPath.FIND_PRODUCT_URL.format(id))
                .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        return gson.fromJson(responseBody, object : TypeToken<Product>() {}.type)
    }

    override fun findAll(): List<Product> {
        val request =
            Request.Builder()
                .url(ProductMockWebServerPath.FIND_PRODUCTS_URL)
                .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        return gson.fromJson(responseBody, object : TypeToken<List<Product>>() {}.type)
    }

    override fun getProducts(): List<Product> {
        val request =
            Request.Builder()
                .url(ProductMockWebServerPath.FIND_PRODUCT_PAGE_URL)
                .build()

        val response = client.newCall(request).execute()

        val responseBody = response.body?.string()
        return gson.fromJson(responseBody, object : TypeToken<List<Product>>() {}.type)
    }

    override fun shutdown() {
        mockWebServer.shutdown()
    }
}
