package woowacouse.shopping.model.page

import woowacouse.shopping.model.cart.CartProduct
import woowacouse.shopping.model.cart.CartProducts
import kotlin.math.ceil

data class PageNation(
    private val cartProducts: CartProducts,
    val currentPage: Int
) {
    val pageCount: Int
        get() = calculatePageCount(cartProducts.getAll().size)

    val totalPrice: Int
        get() = cartProducts.totalPrice

    val currentStartIndex: Int
        get() = (currentPage - 1) * PAGE_MAX_CART_COUNT

    val currentLastIndex: Int
        get() = if (cartProducts.getAll().size > currentStartIndex + PAGE_MAX_CART_COUNT) {
            currentStartIndex + PAGE_MAX_CART_COUNT
        } else {
            cartProducts.getAll().size
        }

    val currentItems: List<CartProduct>
        get() = cartProducts.getAll().subList(currentStartIndex, currentLastIndex)

    fun hasNextPage(): Boolean = currentPage < pageCount

    fun nextPage(): PageNation {
        if (hasNextPage().not())
            return this

        return copy(currentPage = currentPage + PAGE_NATION_COUNT)
    }

    fun hasPreviousPage(): Boolean = currentPage > PAGE_NATION_COUNT

    fun previousPage(): PageNation {
        if (hasPreviousPage().not())
            return this

        return copy(currentPage = currentPage - PAGE_NATION_COUNT)
    }

    // TODO 테스트 작성
    fun updateCartCheckedByCartId(cartId: Long, checked: Boolean): PageNation {
        return PageNation(
            cartProducts.updateCartChecked(cartId, checked),
            currentPage
        )
    }

    // TODO 테스트 작성
    fun updateCartCountByCartId(cartId: Long, count: Int): PageNation {
        return PageNation(
            cartProducts.updateCartCountByCartId(cartId, count),
            currentPage
        )
    }

    fun updateAllCartsChecked(checked: Boolean): PageNation {
        val ids = cartProducts.getAll().map { it.id }
        return copy(cartProducts = cartProducts.updateAllCartsChecked(ids, checked))
    }

    // TODO 테스트 작성
    fun deleteCartByCartId(cartId: Long): PageNation {
        return PageNation(
            cartProducts.deleteCart(cartId),
            currentPage
        )
    }

    fun getCartByCartId(cartId: Long): CartProduct? {
        return cartProducts.getCartByCartId(cartId)
    }

    private fun calculatePageCount(size: Int): Int {
        if (size == 0) return 1
        return ceil((size.toDouble() / PAGE_MAX_CART_COUNT)).toInt()
    }

    companion object {
        private const val PAGE_NATION_COUNT = 1
        private const val PAGE_MAX_CART_COUNT = 3
    }
}
