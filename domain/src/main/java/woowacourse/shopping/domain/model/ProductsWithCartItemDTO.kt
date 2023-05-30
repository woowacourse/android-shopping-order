package woowacourse.shopping.domain.model

data class ProductsWithCartItemDTO(val products: List<ProductWithCartInfo>, val last: Boolean) {
}
