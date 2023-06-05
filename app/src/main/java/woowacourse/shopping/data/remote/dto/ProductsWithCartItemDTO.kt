package woowacourse.shopping.data.remote.dto

import woowacourse.shopping.domain.model.ProductWithCartInfo

data class ProductsWithCartItemDTO(
    val products: List<ProductWithCartInfo>,
    val last: Boolean,
)
