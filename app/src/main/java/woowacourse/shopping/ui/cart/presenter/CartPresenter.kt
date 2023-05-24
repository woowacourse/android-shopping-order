package woowacourse.shopping.ui.cart.presenter

import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.ui.cart.CartContract
import woowacourse.shopping.ui.cart.uistate.CartItemUIState

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
) : CartContract.Presenter {

    private var _currentPage = 0
    private var selectedCartItems = setOf<CartItem>()

    override val currentPage: Int
        get() = _currentPage

    override val selectedCartItemIds: List<Long>
        get() = selectedCartItems.map {
            it.id
        }

    override fun restoreCurrentPage(currentPage: Int) {
        this._currentPage = currentPage
    }

    override fun restoreSelectedCartItems(cartItemIds: List<Long>) {
        cartItemRepository.findAllByIds(cartItemIds) { cartItems ->
            this.selectedCartItems = cartItems.toSet()
            showCartItems(_currentPage, selectedCartItems, false)
        }
    }

    override fun onLoadCartItemsOfNextPage() {
        _currentPage++
        showOrderUI(selectedCartItems)
        showCartItems(_currentPage, selectedCartItems, true)
        showPageUI(_currentPage)
        showAllSelectionUI(_currentPage, selectedCartItems)
    }

    override fun onLoadCartItemsOfPreviousPage() {
        _currentPage--
        showCartItems(_currentPage, selectedCartItems, true)
        showPageUI(_currentPage)
        showAllSelectionUI(_currentPage, selectedCartItems)
    }

    override fun onLoadCartItemsOfLastPage() {
        cartItemRepository.countAll { count ->
            _currentPage = (count - 1) / PAGE_SIZE + 1
            showCartItems(_currentPage, selectedCartItems, true)
            showPageUI(_currentPage)
            showAllSelectionUI(_currentPage, selectedCartItems)
        }
    }

    override fun onDeleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId) {
            selectedCartItems = selectedCartItems.filter { it.id != cartItemId }.toSet()
            showPageUI(_currentPage)
            updateCartUI()
        }
    }

    override fun onChangeSelectionOfCartItem(cartItemId: Long, isSelected: Boolean) {
        cartItemRepository.findById(cartItemId) { cartItem ->
            selectedCartItems = if (isSelected) {
                selectedCartItems + cartItem
            } else {
                selectedCartItems - cartItem
            }
            showAllSelectionUI(_currentPage, selectedCartItems)
            showOrderUI(selectedCartItems)
        }
    }

    override fun onChangeSelectionOfAllCartItems(isSelected: Boolean) {
        val offset = (_currentPage - 1) * PAGE_SIZE
        cartItemRepository.findAllOrderByAddedTime(PAGE_SIZE, offset) { cartItemsOfCurrentPage ->
            if (isSelected) {
                selectedCartItems = selectedCartItems + cartItemsOfCurrentPage
                updateCartUI()
            } else if (cartItemsOfCurrentPage.all { it in selectedCartItems }) {
                selectedCartItems = selectedCartItems - cartItemsOfCurrentPage.toSet()
                updateCartUI()
            }
        }
    }

    private fun updateCartUI() {
        showAllSelectionUI(_currentPage, selectedCartItems)
        showOrderUI(selectedCartItems)
        showCartItems(_currentPage, selectedCartItems, false)
    }

    override fun onPlusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { cartItem ->
            cartItem.plusCount()
            cartItemRepository.updateCountById(cartItemId, cartItem.count) {
                if (cartItem in selectedCartItems) {
                    selectedCartItems = selectedCartItems - cartItem + cartItem
                    showAllSelectionUI(_currentPage, selectedCartItems)
                    showOrderUI(selectedCartItems)
                }
                showCartItems(_currentPage, selectedCartItems, false)
            }
        }
    }

    override fun onMinusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId) { cartItem ->
            cartItem.minusCount()
            cartItemRepository.updateCountById(cartItemId, cartItem.count) {
                if (cartItem in selectedCartItems) {
                    selectedCartItems = selectedCartItems - cartItem + cartItem
                    showAllSelectionUI(_currentPage, selectedCartItems)
                    showOrderUI(selectedCartItems)
                }
                showCartItems(_currentPage, selectedCartItems, false)
            }
        }
    }

    private fun showCartItems(page: Int, selectedCartItems: Set<CartItem>, initScroll: Boolean) {
        getCartItemsOf(page) { cartItems ->
            val cartItemUIStates =
                cartItems.map { CartItemUIState.create(it, it in selectedCartItems) }
            view.setCartItems(cartItemUIStates, initScroll)
        }
    }

    private fun getCartItemsOf(page: Int, onFinish: (List<CartItem>) -> Unit) {
        val offset = (page - 1) * PAGE_SIZE
        return cartItemRepository.findAllOrderByAddedTime(PAGE_SIZE, offset, onFinish)
    }

    private fun showAllSelectionUI(currentPage: Int, selectedCartItems: Set<CartItem>) {
        getCartItemsOf(currentPage) { cartItems ->
            view.setStateOfAllSelection(cartItems.all { it in selectedCartItems } && cartItems.isNotEmpty())
        }
    }

    private fun showOrderUI(selectedCartItems: Set<CartItem>) {
        view.setOrderPrice(selectedCartItems.sumOf(CartItem::getOrderPrice))
        view.setOrderCount(selectedCartItems.size)
    }

    private fun showPageUI(currentPage: Int) {
        refreshStateThatCanRequestPreviousPage(currentPage)
        refreshStateThatCanRequestNextPage(currentPage)
        view.setPage(currentPage)
    }

    private fun refreshStateThatCanRequestPreviousPage(currentPage: Int) {
        if (currentPage <= 1) {
            view.setStateThatCanRequestPreviousPage(false)
        } else {
            view.setStateThatCanRequestPreviousPage(true)
        }
    }

    private fun refreshStateThatCanRequestNextPage(currentPage: Int) {
        getMaxPage { count ->
            if (currentPage >= count) {
                view.setStateThatCanRequestNextPage(false)
            } else {
                view.setStateThatCanRequestNextPage(true)
            }
        }
    }

    private fun getMaxPage(onFinish: (Int) -> Unit) {
        cartItemRepository.countAll { count ->
            onFinish((count - 1) / PAGE_SIZE + 1)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
