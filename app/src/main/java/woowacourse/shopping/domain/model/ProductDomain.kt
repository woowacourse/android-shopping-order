package woowacourse.shopping.domain.model

data class ProductDomain(
    val products: List<RemoteProductItemDomain>,
    val last: Boolean,
)

