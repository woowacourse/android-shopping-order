package woowacourse.shopping.data.network.response

import woowacourse.shopping.domain.product.ProductSinglePage

data class ProductPageResponse(
    val products: List<ProductResponse>,
    val hasNext: Boolean,
) {
    fun toDomain(): ProductSinglePage {
        return ProductSinglePage(
            products = products.map { it.toDomain() },
            hasNextPage = hasNext,
        )
    }
}
