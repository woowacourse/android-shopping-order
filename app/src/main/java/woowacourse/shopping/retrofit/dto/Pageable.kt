package woowacourse.shopping.retrofit.dto

data class Pageable(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)