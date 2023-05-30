package woowacourse.shopping.domain.product

class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
) {
    override fun equals(other: Any?): Boolean = if (other is Product) id == other.id else false

    override fun hashCode(): Int = id.hashCode()
}
