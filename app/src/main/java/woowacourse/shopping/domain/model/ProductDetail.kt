package woowacourse.shopping.domain.model

import java.io.Serializable

data class ProductDetail(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val category: String,
) : Serializable {
    companion object {
        val EMPTY_PRODUCT_DETAIL = ProductDetail(0, "", "", 0, "")
    }
}
