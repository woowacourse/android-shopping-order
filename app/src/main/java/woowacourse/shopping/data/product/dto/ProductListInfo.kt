package woowacourse.shopping.data.product.dto

data class ProductListInfo(
    val products: List<ProductDetail>,
    val last: Boolean,
)
