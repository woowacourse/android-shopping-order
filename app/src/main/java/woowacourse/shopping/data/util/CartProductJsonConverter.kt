package woowacourse.shopping.data.util

import org.json.JSONArray
import org.json.JSONObject
import woowacourse.shopping.data.model.CartProduct
import woowacourse.shopping.data.model.DataProductCount

fun String.toCartProducts(): List<CartProduct> {
    val cartProducts = mutableListOf<CartProduct>()
    val jsonArray = JSONArray(this)

    for (i in 0 until jsonArray.length()) {
        val cartProductJson = jsonArray.getJSONObject(i).toString()
        val cartProduct = cartProductJson.toCartProduct()
        cartProducts.add(cartProduct)
    }

    return cartProducts
}

fun String.toCartProduct(): CartProduct {
    val jsonObject = JSONObject(this)
    val id = jsonObject.getInt("id")
    val count = DataProductCount(jsonObject.getInt("quantity"))
    val productJson = jsonObject.getJSONObject("product").toString()
    val product = productJson.convertJsonToProduct()

    return CartProduct(id, product, count, 1)
}

fun CartProduct.toJson(): String = JSONObject().apply {
    put("quantity", selectedCount.value)
    put("product", product.toJson())
}.toString()
