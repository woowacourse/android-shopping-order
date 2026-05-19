package woowacourse.shopping.model

class Product(
    val id: Long,
    val name: ProductName,
    val price: Money,
    val imageUrl: String,
    val category: String,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Product) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    fun getName(): String = name.name

    fun getPrice(): Long = price.amount
}
