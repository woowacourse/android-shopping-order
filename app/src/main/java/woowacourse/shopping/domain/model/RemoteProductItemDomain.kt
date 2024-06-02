package woowacourse.shopping.domain.model

data class RemoteProductItemDomain(
    val category: String,
    val id: Int,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
