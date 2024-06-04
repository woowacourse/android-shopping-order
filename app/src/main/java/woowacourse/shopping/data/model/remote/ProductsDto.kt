package woowacourse.shopping.data.model.remote

data class ProductsDto(
    val content: List<ProductDto>,
    val pageable: PageableDto,
)
