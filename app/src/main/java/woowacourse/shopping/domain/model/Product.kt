package woowacourse.shopping.domain.model

data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val imageUrl: String,
    val quantity: Int = INIT_QUANTITY,
) {
    companion object {
        const val INIT_QUANTITY = 0
    }
}
