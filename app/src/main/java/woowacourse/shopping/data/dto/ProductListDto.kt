package woowacourse.shopping.data.dto

data class ProductListDto(
    val products: List<ProductDto>,
    val last: Boolean,
)
