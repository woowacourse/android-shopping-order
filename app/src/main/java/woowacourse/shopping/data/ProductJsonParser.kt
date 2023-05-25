package woowacourse.shopping.data

import com.example.domain.model.Product
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object ProductJsonParser {

    fun parse(json: String?): List<Product> {
        return json?.let {
            val productsJsonElement = JsonParser.parseString(it)

            when {
                productsJsonElement is JsonArray -> parseJsonArray(productsJsonElement)
                productsJsonElement is JsonObject -> parseJsonObject(productsJsonElement)
                else -> emptyList()
            }
        } ?: emptyList()
    }

    private fun parseJsonArray(jsonArray: JsonArray): List<Product> {
        val products = mutableListOf<Product>()

        for (i in 0 until jsonArray.size()) {
            val productJson = jsonArray.get(i).asJsonObject
            val product = parseProduct(productJson)
            products.add(product)
        }

        return products
    }

    private fun parseJsonObject(jsonObject: JsonObject): List<Product> {
        val product = parseProduct(jsonObject)
        return listOf(product)
    }

    private fun parseProduct(json: JsonObject): Product {
        val id = json.get("id").asLong
        val name = json.get("name").asString
        val price = json.get("price").asInt
        val imageUrl = json.get("imageUrl").asString

        return Product(id, name, price, imageUrl)
    }
}
