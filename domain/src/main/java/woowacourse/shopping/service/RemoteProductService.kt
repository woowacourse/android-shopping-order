package woowacourse.shopping.service

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class RemoteProductService(baseUrl: String) : ProductRepository {
    private val baseUrl = baseUrl.removeSuffix("/")
    private val client = OkHttpClient()

    override fun getAll(): List<Product> {
        val request = Request.Builder()
            .url("$baseUrl/products")
            .build()

        var products: List<Product>? = null
        executeRequest(
            request,
            onSuccess = { response ->
                val responseBody = response.body?.string()
                    ?: throw RuntimeException("Product get all failed")
                products = parseProductsResponse(responseBody)
            },
            onFailure = { println("Product get all failed") }
        )
        while (products == null) {
            Thread.sleep(10)
        }
        return products!!
    }

    override fun getNext(count: Int): List<Product> {
        // TODO()
        return emptyList()
    }

    override fun insert(product: Product): Int {
        val request = Request.Builder()
            .url("$baseUrl/products")
            .post(product.toJson().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        executeRequest(
            request,
            onSuccess = { println("Product insert success") },
            onFailure = { println("Product insert failed") }
        )

        return 0
    }

    override fun findById(id: Int): Product {
        val request = Request.Builder()
            .url("$baseUrl/products/$id")
            .build()

        var product: Product? = null
        executeRequest(
            request,
            onSuccess = {
                val responseBody = it.body?.string()
                    ?: throw RuntimeException("Product find by id failed")
                product = parseProductResponse(responseBody)
            },
            onFailure = { println("Product find by id failed") }
        )

        while (product == null) { Thread.sleep(10) }
        return product!!
    }

    private fun executeRequest(
        request: Request,
        onSuccess: (Response) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        client.newCall(request).enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    onFailure(e)
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    onSuccess(response)
                }
            }
        )
    }

    private fun parseProductsResponse(responseBody: String?): List<Product> {
        return responseBody?.let {
            val productsJsonArray = JSONArray(it)
            val products = mutableListOf<Product>()
            for (i in 0 until productsJsonArray.length()) {
                val productJson = productsJsonArray.getJSONObject(i)
                val product = Product.fromJson(productJson)
                products.add(product)
            }
            products
        } ?: emptyList()
    }

    private fun parseProductResponse(responseBody: String?): Product {
        return responseBody?.let {
            val productsJSONObject = JSONObject(it)
            Product.fromJson(productsJSONObject)
        } ?: throw RuntimeException("Product not found")
    }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }
}
