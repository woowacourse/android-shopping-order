package woowacourse.shopping.data.model

data class CartItemResponse(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Long,
    val totalPages: Int,
) {
    data class Content(
        val id: Long,
        val product: ProductResponse,
        val quantity: Int,
    )

    companion object {
        val EMPTY =
            CartItemResponse(
                content = emptyList(),
                empty = false,
                first = false,
                last = false,
                number = 0,
                numberOfElements = 0,
                pageable = Pageable.EMPTY,
                size = 0,
                sort = Sort.EMPTY,
                totalElements = 0,
                totalPages = 0,
            )
    }
}
