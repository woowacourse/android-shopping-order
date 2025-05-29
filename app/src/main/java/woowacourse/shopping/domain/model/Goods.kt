package woowacourse.shopping.domain.model

data class Goods(
    val name: String,
    val price: Int,
    val thumbnailUrl: String,
    val id: Int = 0,
    val category: String,
)
