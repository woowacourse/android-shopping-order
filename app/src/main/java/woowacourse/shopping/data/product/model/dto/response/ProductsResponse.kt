package woowacourse.shopping.data.product.model.dto.response

import woowacourse.shopping.data.product.model.dto.PaginationDto
import woowacourse.shopping.data.product.model.dto.ProductsDto

data class ProductsResponse(
    val products: ProductsDto,
    val pagination: PaginationDto,
)
