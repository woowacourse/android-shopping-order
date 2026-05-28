package woowacourse.shopping.ui.cart.component

import woowacourse.shopping.model.CartItem

data class CartPage(
    val index: Int,
    val items: List<CartItem>,
    val canMoveNext: Boolean,
)

fun List<CartItem>.paginate(
    page: Int,
    pageSize: Int,
): CartPage {
    val maxPage = ((size - 1).coerceAtLeast(0)) / pageSize
    val currentPage = page.coerceIn(0, maxPage)

    return CartPage(
        index = currentPage,
        items = drop(currentPage * pageSize).take(pageSize),
        canMoveNext = (currentPage + 1) * pageSize < size,
    )
}
