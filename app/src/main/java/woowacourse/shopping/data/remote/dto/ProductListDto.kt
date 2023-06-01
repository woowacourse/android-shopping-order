package woowacourse.shopping.data.remote.dto

data class ProductListDto(
    val products: List<ProductDto>,
    val last: Boolean,
)
