package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
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
    private lateinit var currentUser: User
    var currentPage = 0

    override fun loadCurrentUser() {
        userRepository.findCurrent().onSuccess { loadedUser ->
            currentUser = loadedUser
        }.onFailure {
        }
    }

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
        val count = cartItemRepository.countAll(currentUser).getOrElse {
            return
        }

        currentPage = (count - 1) / pageSize + 1
        showCartItems(currentPage, selectedCart, true)
        showPageUI(currentPage)
        showAllSelectionUI(currentPage, selectedCart)
    }

    override fun deleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId, currentUser).getOrElse {
            return
        }

        selectedCart = Cart(selectedCart.value.filter { it.id != cartItemId }.toSet())
        showPageUI(currentPage)
        updateCartUI()
    }

    override fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean) {
        val loadedCartItem = cartItemRepository.findById(cartItemId, currentUser).getOrElse {
            return
        }

        selectedCart = if (isSelected) {
            selectedCart.copy(value = selectedCart.value + loadedCartItem)
        } else {
            selectedCart.copy(value = selectedCart.value - loadedCartItem)
        }
        showAllSelectionUI(currentPage, selectedCart)
        showOrderUI(selectedCart)
    }

    override fun updateSelectionTotalCartItems(isSelected: Boolean) {
        val offset = (currentPage - 1) * pageSize
        cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset, currentUser
        ).onSuccess { cartItemsOfCurrentPage ->
            if (isSelected) {
                selectedCart =
                    selectedCart.copy(value = selectedCart.value + cartItemsOfCurrentPage)
                updateCartUI()
            } else if (cartItemsOfCurrentPage.all { it in selectedCart.value }) {
                selectedCart =
                    selectedCart.copy(value = selectedCart.value - cartItemsOfCurrentPage.toSet())
                updateCartUI()
            }
        }.onFailure {
            return
        }
    }

    override fun plusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).onSuccess { loadedCartItem ->
            val cartItem = loadedCartItem.plusQuantity()
            updateCount(cartItem)
        }.onFailure {
            return
        }
    }

    override fun minusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).onSuccess { loadedCartItem ->
            val cartItem = loadedCartItem.minusQuantity()
            updateCount(cartItem)
        }.onFailure {
            return
        }
    }

    override fun checkPayment() {
        val totalPrice = selectedCart.calculateTotalPrice()
        orderRepository.findDiscountPolicy(totalPrice, currentUser.rank.toString())
            .onSuccess { order ->
                view.showPayment(order.toUIState(), totalPrice - order.calculateDiscountPrice())
            }.onFailure {
                return
            }
    }

    override fun placeOrder() {
        orderRepository.save(selectedCart.value.map { it.id }, currentUser).onSuccess { orderId ->
        view.showOrderDetail(orderId)
    }.onFailure {
            return
        }
    }

    private fun showCartItems(page: Int, selectedCartItems: Cart, initScroll: Boolean) {
        val cartItems = getCartItemsOf(page)
        val cartItemUIStates = cartItems.map { it.toUIState(it in selectedCartItems.value) }
        view.setCartItems(cartItemUIStates, initScroll)
    }

    private fun updateCartUI() {
        showAllSelectionUI(currentPage, selectedCart)
        showOrderUI(selectedCart)
        showCartItems(currentPage, selectedCart, false)
    }

    private fun showAllSelectionUI(currentPage: Int, selectedCartItems: Cart) {
        val cartItems = getCartItemsOf(currentPage)
        view.setStateOfAllSelection(cartItems.all { it in selectedCartItems.value } && cartItems.isNotEmpty())
    }

    private fun getCartItemsOf(page: Int): List<CartItem> {
        val offset = (page - 1) * pageSize
        return cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset, currentUser
        ).getOrElse {
            return emptyList()
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
        val maxPage = getMaxPage()
        view.setStateThatCanRequestNextPage(currentPage < maxPage)
        view.setStateThatCanRequestPage(maxPage > 1)
    }

    private fun getMaxPage(): Int {
        val count = cartItemRepository.countAll(currentUser).getOrElse {
            return 0
        }
        return (count - 1) / pageSize + 1
    }

    private fun updateCount(cartItem: CartItem) {
        cartItemRepository.updateCountById(cartItem.id, cartItem.quantity, currentUser).onSuccess {
            if (cartItem in selectedCart.value) {
                selectedCart = selectedCart.updateElement(cartItem, cartItem)
                showAllSelectionUI(currentPage, selectedCart)
                showOrderUI(selectedCart)
            }
            showCartItems(currentPage, selectedCart, false)
        }
    }
}
