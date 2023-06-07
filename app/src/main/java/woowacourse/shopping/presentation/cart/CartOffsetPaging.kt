package woowacourse.shopping.presentation.cart

import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.util.OffsetPaging

class CartOffsetPaging(
    override val limit: Int = LIMIT,
    startPage: Int = START_PAGE,
) : OffsetPaging<CartProductInfo>(startPage) {
    fun plusPage() {
        setPage(currentPage.plus(PAGE_STEP))
    }

    fun minusPage() {
        setPage(currentPage.minus(PAGE_STEP))
    }

    fun isPlusPageAble(cartItems: CartProductInfoList): Boolean {
        val page = currentPage.plus(PAGE_STEP)
        return cartItems.getItemsInRange(page.getOffset(limit), limit).items.isNotEmpty()
    }

    fun isMinusPageAble(): Boolean {
        val page = currentPage.value
        return page != MINIMUM_PAGE
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val START_PAGE = 1
        private const val PAGE_STEP = 1
        private const val LIMIT = 5
    }
}
