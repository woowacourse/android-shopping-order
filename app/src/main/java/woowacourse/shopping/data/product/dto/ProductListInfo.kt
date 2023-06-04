package woowacourse.shopping.data.product.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductListInfo(
    val products: List<ProductDetail>,
    val last: Boolean,
)
