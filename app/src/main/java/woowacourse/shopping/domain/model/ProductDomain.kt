package woowacourse.shopping.domain.model

data class ProductDomain(
    val orderableProducts: List<OrderableProduct>,
    val last: Boolean,
)
