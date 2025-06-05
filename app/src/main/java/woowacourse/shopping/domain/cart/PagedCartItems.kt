package woowacourse.shopping.domain.cart

class PagedCartItems(
    val cartItems: List<CartItem>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    companion object {
        fun from(
            cartItems: List<CartItem>,
            pageNumber: Int?,
            totalPages: Int?,
        ): PagedCartItems {
            val hasPrevious: Boolean = if (pageNumber == null) false else pageNumber > 0

            val hasNext: Boolean =
                run {
                    pageNumber ?: return@run false
                    totalPages ?: return@run false

                    pageNumber + 1 < totalPages
                }

            return PagedCartItems(
                cartItems,
                hasPrevious,
                hasNext,
            )
        }
    }
}
