package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.entity.CartItemEntity
import woowacourse.shopping.domain.cart.PagedCartItems

class PagedCartItemData(
    private val cartItems: List<CartItemEntity>,
    private val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    fun toDomain(): PagedCartItems =
        PagedCartItems(
            this.cartItems.map { it.toDomain() },
            this.hasPrevious,
            this.hasNext,
        )

    companion object {
        fun from(
            cartItems: List<CartItemEntity>,
            pageNumber: Int?,
            totalPages: Int?,
        ): PagedCartItemData {
            val hasPrevious: Boolean = if (pageNumber == null) false else pageNumber > 0

            val hasNext: Boolean =
                run {
                    pageNumber ?: return@run false
                    totalPages ?: return@run false

                    pageNumber + 1 < totalPages
                }
            return PagedCartItemData(
                cartItems,
                hasPrevious,
                hasNext,
            )
        }
    }
}
