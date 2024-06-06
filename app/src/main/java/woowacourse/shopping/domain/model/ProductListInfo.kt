package woowacourse.shopping.domain.model

data class ProductListInfo(
    val products: List<Product>,
    val pageInfo: PageInfo,
)
