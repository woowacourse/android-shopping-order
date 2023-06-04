package woowacourse.shopping.domain.model.page

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct

abstract class Page(
    val value: Int = DEFAULT_PAGE,
    val sizePerPage: Int = DEFAULT_SIZE_PER_PAGE,
) {
    init {
        require(value >= MIN_PAGE) { INVALID_PAGE_NUMBER_ERROR_MESSAGE }
    }

    abstract fun getStartPage(): Page

    abstract fun next(): Page

    abstract fun update(value: Int): Page

    abstract fun takeItems(cart: Cart): List<CartProduct>

    abstract fun getPageForCheckHasNext(): Page

    fun hasPrevious(): Boolean = value > MIN_PAGE

    fun hasNext(cart: Cart): Boolean = cart.items.size > sizePerPage * value

    fun getCheckedProductSize(cart: Cart): Int =
        takeItems(cart).count { item -> item.isChecked }

    companion object {
        private const val DEFAULT_PAGE = 1
        private const val DEFAULT_SIZE_PER_PAGE = 5

        private const val MIN_PAGE = 1
        private const val INVALID_PAGE_NUMBER_ERROR_MESSAGE =
            "페이지 번호는 1 이상의 정수만 가능합니다."
    }
}
