package woowacourse.shopping.data.remote.dto

data class ProductDTO(val id: Int?, val name: String?, val price: Int?, val imageUrl: String?) {
    val isNotNull: Boolean
        get() = id != null && name != null && price != null && imageUrl != null
}
