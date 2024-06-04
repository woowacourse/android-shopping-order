package woowacourse.shopping.remote.model

data class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
){
    companion object {
        val DEFAULT = ProductDto(0, "", 0, "", "")
    }
}
