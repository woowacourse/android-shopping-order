package woowacourse.shopping.data.util

import org.json.JSONObject
import woowacourse.shopping.data.model.Price
import woowacourse.shopping.data.model.Product

typealias ProductId = Int

fun String.convertJsonToProduct(): Product {
    val jsonObject = JSONObject(this)
    val id = jsonObject.getInt("id")
    val price = Price(jsonObject.getInt("price"))
    val name = jsonObject.getString("name")
    val imageUrl = jsonObject.getString("imageUrl")

    return Product(id, name, price, imageUrl)
}

fun Product.toJson(): String = JSONObject().apply {
    put("id", id)
    put("name", name)
    put("price", price.value)
    put("imageUrl", imageUrl)
}.toString()

fun convertProductIdToJson(id: Int): String = JSONObject().apply {
    put("productId", id)
}.toString()
