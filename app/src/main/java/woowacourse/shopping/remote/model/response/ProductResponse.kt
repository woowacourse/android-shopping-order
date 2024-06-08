package woowacourse.shopping.remote.model.response

import woowacourse.shopping.data.model.ProductData

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) {
    companion object {
        val DEFAULT = ProductResponse(0, "", 0, "", "")
    }
}

fun ProductResponse.toData(): ProductData =
    ProductData(
        id = id,
        name = name,
        price = price,
        imgUrl = imageUrl,
        category = category,
    )
