package woowacourse.shopping.data.remote.dto

data class ProductsWithCartItemDTO(
    val products: List<ProductWithCartInfoDTO?>?,
    val last: Boolean?,
) {
    val isNotNull: Boolean
        get() = products != null &&
            products.all { it?.isNotNull ?: false } &&
            last != null
}
