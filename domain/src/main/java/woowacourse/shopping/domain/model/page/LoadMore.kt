package woowacourse.shopping.domain.model.page

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct

typealias DomainLoadMore = LoadMore

class LoadMore(
    value: Int = 1,
    sizePerPage: Int = 20,
) : Page(value, sizePerPage) {
    override fun getStartPage(): Page = LoadMore(FIRST_PAGE, sizePerPage)

    override fun next(): Page = LoadMore(value + 1, sizePerPage)

    override fun update(value: Int): Page = LoadMore(value, sizePerPage)

    override fun takeItems(cart: Cart): List<CartProduct> =
        cart.items.take(value * sizePerPage)

    companion object {
        private const val FIRST_PAGE = 1
    }
}
