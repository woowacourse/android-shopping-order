package woowacourse.shopping.data.model

data class ProductsResponse(
    val content: List<ProductResponse>,
    val pageable: Pageable,
    val last: Boolean,
    val first: Boolean,
) {
    companion object {
        val EMPTY =
            ProductsResponse(
                content = emptyList(),
                pageable = Pageable.EMPTY,
                last = false,
                first = false,
            )
    }
}
