package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.model.remote.ProductsDto
import woowacourse.shopping.domain.model.Products

fun ProductsDto.toDomain(): Products {
    return Products(
        content = this.content.map { it.toDomain() },
        pageable = this.pageable.toDomain(),
    )
}
