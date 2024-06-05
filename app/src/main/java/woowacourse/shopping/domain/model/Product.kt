package woowacourse.shopping.domain.model

data class Product(
    val id: Long,
    val imgUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int,
) {
    companion object {
        val NULL =
            Product(
                id = -1,
                imgUrl = "0",
                name = "상품이 없습니다.",
                price = 0,
                quantity = 0,
            )
    }
}
