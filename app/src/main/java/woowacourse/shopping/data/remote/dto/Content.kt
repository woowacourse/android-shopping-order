package woowacourse.shopping.data.remote.dto

data class Content(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
