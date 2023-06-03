package woowacourse.shopping.data.remote.dto.response

data class ProductListDto(
    val products: List<ProductDto>,
    val last: Boolean,
)
