package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.ui.common.pagination.PageStateHolder

class ShoppingCartPageStateHolder(
    shoppingCartItems: List<ShoppingCartItem>,
) : PageStateHolder<ShoppingCartItem>(shoppingCartItems) {
    override val pageItemSize: Int = PAGE_ITEM_SIZE

    override fun getPageRange(): IntRange = currentPage..getExclusiveEndPage()

    fun beforePage() {
        updateCurrentPage(currentPage - 1)
    }

    fun nextPage() {
        updateCurrentPage(currentPage + 1)
    }

    fun canMoveToPreviousPage(): Boolean = isInPageRange(currentPage - 1)

    fun canMoveToNextPage(): Boolean = isInPageRange(currentPage + 1)

    companion object {
        const val PAGE_ITEM_SIZE: Int = 5
    }
}
