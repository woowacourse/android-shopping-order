package woowacourse.shopping.data.datasource.remote.model.response.product

data class ProductContent(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
