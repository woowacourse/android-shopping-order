package woowacourse.shopping.domain.model.page

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct

class LoadMore(
    value: Int = 1,
    sizePerPage: Int = 20,
) : Page(value, sizePerPage) {

    override fun getStartPage(): Page = LoadMore(FIRST_PAGE, sizePerPage)

    override fun next(): Page = LoadMore(value + 1, sizePerPage)

    override fun update(value: Int): Page = LoadMore(value, sizePerPage)

    override fun takeItems(cart: Cart): List<CartProduct> =
        cart.items.take(value * sizePerPage)

    override fun getPageForCheckHasNext(): Page {
        return LoadMore(value, sizePerPage + CHECK_FOR_HAS_NEXT)
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val CHECK_FOR_HAS_NEXT = 1
    }
}
