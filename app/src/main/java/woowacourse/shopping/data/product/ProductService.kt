package woowacourse.shopping.data.product

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import woowacourse.shopping.Product
import woowacourse.shopping.data.ApiClient
import woowacourse.shopping.data.mapper.toDomain

class ProductService : ProductRemoteDataSource {
    override val products: List<Product> = getAllProducts()
    override fun findProductById(id: Int): Product {
        ApiClient.getApiService("products/$id")?.let {
            return parseToProduct(it)
        }
        return Product.defaultProduct
    }

    private fun getAllProducts(): List<Product> {
        val responseBody = ApiClient.getApiService("products") ?: return emptyList()
        return parseToProducts(responseBody)
    }

    private fun parseToProducts(responseBody: String?): List<Product> {
        val gson = GsonBuilder().create()
        val result = gson.fromJson(responseBody, Array<ProductDataModel>::class.java)
        return result.map {
            it.toDomain()
        }
    }

    private fun parseToProduct(responseBody: String?): Product {
        val jsonObject = JsonParser.parseString(responseBody).asJsonObject
        val data = Gson().fromJson(jsonObject, ProductDataModel::class.java)
        return data.toDomain()
    }
}
