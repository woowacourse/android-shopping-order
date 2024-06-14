package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.Product

data class ProductData(
    val id: Long,
    val imgUrl: String,
    val name: String,
    val price: Int,
    val category: String = "",
) {
    companion object {
        val NULL = ProductData(-1, "0", "상품이 없습니다.", 0)

        val DEFAULT = ProductData(0, "", "", 0)
    }
}

fun ProductData.toDomain(quantity: Int = 0): Product =
    Product(
        id,
        imgUrl,
        name,
        price,
        quantity = quantity,
        category = category,
    )
