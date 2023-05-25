package woowacourse.shopping.data.product

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import woowacourse.shopping.Product
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.mapper.toDataModel

class ProductService : ProductRemoteDataSource {
    override val products: List<ProductDataModel> by lazy { getAllProducts() }
    override fun findProductById(id: Int): ProductDataModel {
        var product: ProductDataModel? = null
        val thread = Thread {
            val response = ApiClient().getApiService("products/$id")
            val responseBody = response.body?.string()
            product = parseToProduct(responseBody)
        }
        thread.start()
        thread.join()
        return product ?: Product.defaultProduct.toDataModel()
    }

    private fun getAllProducts(): List<ProductDataModel> {
        var products = emptyList<ProductDataModel>()
        val thread = Thread {
            val response = ApiClient().getApiService("products")
            val responseBody = response.body?.string()
            products = parseToProducts(responseBody)
        }
        thread.start()
        thread.join()
        return products
    }

    private fun parseToProducts(responseBody: String?): List<ProductDataModel> {
        val gson = GsonBuilder().create()
        return gson.fromJson(responseBody, Array<ProductDataModel>::class.java).toList()
    }

    private fun parseToProduct(responseBody: String?): ProductDataModel {
        val jsonObject = JsonParser.parseString(responseBody).asJsonObject
        return Gson().fromJson(jsonObject, ProductDataModel::class.java)
    }
}
