package woowacourse.shopping.model

import org.json.JSONObject

data class CartProduct(
    val id: Int,
    val name: String,
    val count: Int,
    val checked: Boolean,
    val price: Int,
    val imageUrl: String,
    val productId: Int
) {
    fun toJson(): String {
        return """{
            "id": $id,
            "name": "$name",
            "count": $count,
            "checked": $checked,
            "price": $price,
            "imageUrl": "$imageUrl",
            "productId": $productId
        }"""
    }

    companion object {
        fun fromJson(json: JSONObject): CartProduct {
            val product = json.getJSONObject("product")
            return CartProduct(
                id = json.getInt("id"),
                count = json.getInt("quantity"),
                productId = product.getInt("id"),
                name = product.getString("name"),
                price = product.getInt("price"),
                imageUrl = product.getString("imageUrl"),
                checked = true
            )
        }
    }
}
