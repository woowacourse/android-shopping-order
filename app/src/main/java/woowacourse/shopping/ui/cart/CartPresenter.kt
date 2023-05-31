package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val pageSize: Int
) : CartContract.Presenter {
    private var selectedCart = Cart(emptySet())
    var currentPage = 0

    override fun loadCartItemsOfNextPage() {
        currentPage++
        showOrderUI(selectedCart)
        showCartItems(currentPage, selectedCart, true)
        showPageUI(currentPage)
        showAllSelectionUI(currentPage, selectedCart)
    }

    override fun loadCartItemsOfPreviousPage() {
        currentPage--
        showCartItems(currentPage, selectedCart, true)
        showPageUI(currentPage)
        showAllSelectionUI(currentPage, selectedCart)
    }

    override fun loadCartItemsOfLastPage() {
        userRepository.findCurrent { currentUser ->
            cartItemRepository.countAll(currentUser) { count ->
                currentPage = (count - 1) / pageSize + 1
                showCartItems(currentPage, selectedCart, true)
                showPageUI(currentPage)
                showAllSelectionUI(currentPage, selectedCart)
            }
        }
    }

    override fun deleteCartItem(cartItemId: Long) {
        userRepository.findCurrent { currentUser ->

            cartItemRepository.deleteById(cartItemId, currentUser) {
                selectedCart = Cart(selectedCart.value.filter { it.id != cartItemId }.toSet())
                showPageUI(currentPage)
                updateCartUI()
            }
        }
    }

    override fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean) {
        userRepository.findCurrent { currentUser ->

            cartItemRepository.findById(cartItemId, currentUser) { loadedCartItem ->
                requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

                selectedCart = if (isSelected) {
                    selectedCart.copy(value = selectedCart.value + loadedCartItem)
                } else {
                    selectedCart.copy(value = selectedCart.value - loadedCartItem)
                }
                showAllSelectionUI(currentPage, selectedCart)
                showOrderUI(selectedCart)
            }
        }
    }

    override fun updateSelectionTotalCartItems(isSelected: Boolean) {
        userRepository.findCurrent { currentUser ->
            val offset = (currentPage - 1) * pageSize
            cartItemRepository.findAllOrderByAddedTime(
                pageSize, offset, currentUser
            ) { cartItemsOfCurrentPage ->
                if (isSelected) {
                    selectedCart =
                        selectedCart.copy(value = selectedCart.value + cartItemsOfCurrentPage)
                    updateCartUI()
                } else if (cartItemsOfCurrentPage.all { it in selectedCart.value }) {
                    selectedCart =
                        selectedCart.copy(value = selectedCart.value - cartItemsOfCurrentPage.toSet())
                    updateCartUI()
                }
            }
        }
    }

    override fun plusCount(cartItemId: Long) {
        userRepository.findCurrent { currentUser ->

            cartItemRepository.findById(cartItemId, currentUser) { loadedCartItem ->
                requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

                val cartItem = loadedCartItem.plusQuantity()
                updateCount(cartItem)
            }
        }
    }

    override fun minusCount(cartItemId: Long) {
        userRepository.findCurrent { currentUser ->

            cartItemRepository.findById(cartItemId, currentUser) { loadedCartItem ->
                requireNotNull(loadedCartItem) { ERROR_CART_ITEM_NULL.format(cartItemId) }

                val cartItem = loadedCartItem.minusQuantity()
                updateCount(cartItem)
            }
        }
    }

    override fun checkPayment() {
        val totalPrice = selectedCart.calculateTotalPrice()
        orderRepository.findDiscountPolicy(totalPrice, "gold") {
            view.showPaymentWindow(it.toUIState(), totalPrice)
        }
    }

    override fun placeOrder() {
        userRepository.findCurrent { currentUser ->
            orderRepository.save(selectedCart.value.map { it.id }, currentUser) {
            view.showOrderDetail(it)
        }
        }
    }

    private fun showCartItems(page: Int, selectedCartItems: Cart, initScroll: Boolean) {
        getCartItemsOf(page) { cartItems ->
            val cartItemUIStates = cartItems.map { it.toUIState(it in selectedCartItems.value) }
            view.setCartItems(cartItemUIStates, initScroll)
        }
    }

    private fun updateCartUI() {
        showAllSelectionUI(currentPage, selectedCart)
        showOrderUI(selectedCart)
        showCartItems(currentPage, selectedCart, false)
    }

    private fun showAllSelectionUI(currentPage: Int, selectedCartItems: Cart) {
        getCartItemsOf(currentPage) { cartItems ->
            view.setStateOfAllSelection(cartItems.all { it in selectedCartItems.value } && cartItems.isNotEmpty())
        }
    }

    private fun getCartItemsOf(page: Int, onFinish: (List<CartItem>) -> Unit) {
        userRepository.findCurrent { currentUser ->
            val offset = (page - 1) * pageSize
            cartItemRepository.findAllOrderByAddedTime(
                pageSize,
                offset,
                currentUser,
                onFinish
            )
        }
    }

    private fun showOrderUI(selectedCartItems: Cart) {
        view.setOrderPrice(selectedCartItems.calculateTotalPrice())
        view.setOrderCount(selectedCartItems.value.size)
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
        userRepository.findCurrent { currentUser ->
            cartItemRepository.countAll(currentUser) { count ->
                onFinish((count - 1) / pageSize + 1)
            }
        }
    }

    private fun updateCount(cartItem: CartItem) {
        userRepository.findCurrent { currentUser ->
            cartItemRepository.updateCountById(cartItem.id, cartItem.quantity, currentUser) {
                if (cartItem in selectedCart.value) {
                    selectedCart = selectedCart.updateElement(cartItem, cartItem)
                    showAllSelectionUI(currentPage, selectedCart)
                    showOrderUI(selectedCart)
                }
                showCartItems(currentPage, selectedCart, false)
            }
        }
    }

    companion object {
        private const val ERROR_CART_ITEM_NULL = "장바구니 상품을 서버에서 조회하지 못했습니다. 상품 ID : %d"
    }
}
