package woowacourse.shopping.domain.model

data class ProductDetail(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: Int,
) {
    companion object {
        val EMPTY_PRODUCT_DETAIL = ProductDetail(0, "", "", 0)
    }
}
