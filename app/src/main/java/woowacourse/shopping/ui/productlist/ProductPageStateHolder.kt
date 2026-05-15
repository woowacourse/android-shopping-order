package woowacourse.shopping.ui.productlist

import woowacourse.shopping.domain.model.ShoppingItem
import woowacourse.shopping.ui.common.pagination.PageStateHolder

class ProductPageStateHolder(
    shoppingItems: List<ShoppingItem>,
    initialPage: Int = 0,
) : PageStateHolder<ShoppingItem>(shoppingItems) {
    /**
     * 로미는 StateHolder와 ViewModel의 공통점과 차이점은 무엇이라고 생각하시나요?
     */
    init {
        restoreCurrentPage(initialPage)
    }

    override val pageItemSize: Int = 20

    override fun getPageRange(): IntRange = initialPage..getExclusiveEndPage()

    fun nextPage() {
        updateCurrentPage(currentPage + 1)
    }

    fun canMoveToNextPage(): Boolean = isInPageRange(currentPage + 1)

    fun restoreCurrentPage(page: Int) {
        updateCurrentPage(page)
    }
}
