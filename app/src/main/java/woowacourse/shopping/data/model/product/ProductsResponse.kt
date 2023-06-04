package woowacourse.shopping.data.model.product

data class ProductsResponse(
    val products: ProductsDto,
    val pagination: PaginationDto,
)
