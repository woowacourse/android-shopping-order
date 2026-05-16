package woowacourse.shopping.domain

class Product(
    val name: String,
    private val price: Money,
    val imageUrl: String,
    val id: Long = 0L,
    val category: String = "",
) {
    init {
        require(name.isNotBlank()) { "상품 제목은 공백일 수 없습니다." }
    }

    fun priceAmount(): Int = price.amount
}
