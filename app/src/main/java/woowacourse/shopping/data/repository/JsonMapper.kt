package woowacourse.shopping.data.repository

import org.json.JSONObject
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

fun CartProduct.Companion.fromJson(productJSON: JSONObject): CartProduct {
    return CartProduct(
        productJSON.getInt("id"),
        productJSON.getInt("quantity"),
        Product.fromJson(productJSON.getJSONObject("product")),
    )
}

fun Product.Companion.fromJson(productJSON: JSONObject): Product {
    return Product(
        productJSON.getInt("id"),
        productJSON.getString("name"),
        productJSON.getString("imageUrl"),
        Price(productJSON.getInt("price")),
    )
}
