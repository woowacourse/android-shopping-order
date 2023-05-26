package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState.Companion.toUIState

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
    private val pageSize: Int
) : CartContract.Presenter {
    private var selectedCartItems = setOf<CartItem>()
    var currentPage = 0

    override fun loadCartItemsOfNextPage() {
        currentPage++
        showOrderUI(selectedCartItems)
        showCartItems(currentPage, selectedCartItems, true)
        showPageUI(currentPage)
        showAllSelectionUI(currentPage, selectedCartItems)
    }

    override fun loadCartItemsOfPreviousPage() {
        currentPage--
        showCartItems(currentPage, selectedCartItems, true)
        showPageUI(currentPage)
        showAllSelectionUI(currentPage, selectedCartItems)
    }

    override fun loadCartItemsOfLastPage() {
        cartItemRepository.countAll { count ->
            currentPage = (count - 1) / pageSize + 1
            showCartItems(currentPage, selectedCartItems, true)
            showPageUI(currentPage)
            showAllSelectionUI(currentPage, selectedCartItems)
        }
    }

    override fun deleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId) {
            selectedCartItems = selectedCartItems.filter { it.id != cartItemId }.toSet()
            showPageUI(currentPage)
            updateCartUI()
        }
    }

    override fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean) {
        cartItemRepository.findById(cartItemId) { loadedCartItem ->
            requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

            selectedCartItems = if (isSelected) {
                selectedCartItems + loadedCartItem
            } else {
                selectedCartItems - loadedCartItem
            }
            showAllSelectionUI(currentPage, selectedCartItems)
            showOrderUI(selectedCartItems)
        }
    }

    override fun updateSelectionTotalCartItems(isSelected: Boolean) {
        val offset = (currentPage - 1) * pageSize
        cartItemRepository.findAllOrderByAddedTime(pageSize, offset) { cartItemsOfCurrentPage ->
            if (isSelected) {
                selectedCartItems = selectedCartItems + cartItemsOfCurrentPage
                updateCartUI()
            } else if (cartItemsOfCurrentPage.all { it in selectedCartItems }) {
                selectedCartItems = selectedCartItems - cartItemsOfCurrentPage.toSet()
                updateCartUI()
            }
        }
    }

    override fun plusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { loadedCartItem ->
            requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

            val cartItem = loadedCartItem.plusCount()
            updateCount(cartItem)
        }
    }

    override fun minusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { loadedCartItem ->
            requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

            val cartItem = loadedCartItem.minusCount()
            updateCount(cartItem)
        }
    }

    private fun showCartItems(page: Int, selectedCartItems: Set<CartItem>, initScroll: Boolean) {
        getCartItemsOf(page) { cartItems ->
            val cartItemUIStates =
                cartItems.map { it.toUIState(it in selectedCartItems) }
            view.setCartItems(cartItemUIStates, initScroll)
        }
    }

    private fun updateCartUI() {
        showAllSelectionUI(currentPage, selectedCartItems)
        showOrderUI(selectedCartItems)
        showCartItems(currentPage, selectedCartItems, false)
    }

    private fun showAllSelectionUI(currentPage: Int, selectedCartItems: Set<CartItem>) {
        getCartItemsOf(currentPage) { cartItems ->
            view.setStateOfAllSelection(cartItems.all { it in selectedCartItems } && cartItems.isNotEmpty())
        }
    }

    private fun getCartItemsOf(page: Int, onFinish: (List<CartItem>) -> Unit) {
        val offset = (page - 1) * pageSize
        return cartItemRepository.findAllOrderByAddedTime(pageSize, offset, onFinish)
    }

    private fun showOrderUI(selectedCartItems: Set<CartItem>) {
        view.setOrderPrice(selectedCartItems.sumOf(CartItem::calculateOrderPrice))
        view.setOrderCount(selectedCartItems.size)
    }

    private fun showPageUI(currentPage: Int) {
        refreshStateThatCanRequestPage(currentPage)
        view.setPage(currentPage)
    }

    private fun refreshStateThatCanRequestPage(currentPage: Int) {
        view.setStateThatCanRequestPreviousPage(currentPage > 1)
        getMaxPage { maxPage ->
            view.setStateThatCanRequestNextPage(currentPage < maxPage)
            view.setStateThatCanRequestPage(maxPage > 1)
        }
    }

    private fun getMaxPage(onFinish: (Int) -> Unit) {
        cartItemRepository.countAll { count ->
            onFinish((count - 1) / pageSize + 1)
        }
    }

    private fun updateCount(cartItem: CartItem) {
        cartItemRepository.updateCountById(cartItem.id, cartItem.count) {
            if (cartItem in selectedCartItems) {
                selectedCartItems = selectedCartItems - cartItem + cartItem
                showAllSelectionUI(currentPage, selectedCartItems)
                showOrderUI(selectedCartItems)
            }
            showCartItems(currentPage, selectedCartItems, false)
        }
    }

    companion object {
        private const val ERROR_CART_ITEM_NULL = "장바구니 상품을 서버에서 조회하지 못했습니다. 상품 ID : %d"
    }
}
