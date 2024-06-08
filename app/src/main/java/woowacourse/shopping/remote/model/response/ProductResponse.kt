package woowacourse.shopping.remote.model.response

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
