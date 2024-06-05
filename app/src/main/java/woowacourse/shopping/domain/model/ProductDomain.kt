package woowacourse.shopping.domain.model

data class ProductDomain(
    val products: List<ProductItemDomain>,
    val last: Boolean,
)
