package woowacourse.shopping.domain.model

data class PagingProduct(
    val products: List<Product>,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
)
