package woowacourse.shopping.data.model

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) {
    companion object {
        val EMPTY =
            ProductResponse(
                id = -1L,
                name = "",
                price = 0,
                imageUrl = "",
                category = "",
            )
    }
}
