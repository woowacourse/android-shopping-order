package woowacourse.shopping.domain.model

data class ProductItemDomain(
    val id: Int,
    val category: String,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
