package woowacourse.shopping.data.network.response.products

import woowacourse.shopping.domain.product.ProductSinglePage

@Serializable
data class ProductsResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Int,
    val totalPages: Int,
) {
    fun toDomain(): ProductSinglePage {
        val products = content.map { it.toDomain() }
        return ProductSinglePage(products, last)
    }
}
