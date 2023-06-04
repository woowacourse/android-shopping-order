package woowacourse.shopping.data.product.model

data class ProductsResponse(
    val products: ProductsDto,
    val pagination: PaginationDto,
)
