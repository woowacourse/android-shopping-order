package woowacourse.shopping.domain.model

class Product(
    val id: Long = -1L,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val category: String? = null,
) {
    companion object {
        private var currentId = 0L

        fun of(
            name: String,
            price: Long,
            imageUrl: String,
        ): Product =
            Product(
                name = name,
                price = price,
                imageUrl = imageUrl,
            )
    }
}
