package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState
import java.util.concurrent.CompletableFuture

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    userRepository: UserRepository,
    private val pageSize: Int
) : CartContract.Presenter {
    private var selectedCart = Cart(emptySet())
    private val currentUser: User = userRepository.findCurrent().get().getOrElse { throw it }

    private var currentPage = 0

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
        cartItemRepository.countAll(currentUser).thenAccept {
            val count = it.getOrThrow()
            currentPage = (count - 1) / pageSize + 1
            showCartItems(currentPage, selectedCart, true)
            showPageUI(currentPage)
            showAllSelectionUI(currentPage, selectedCart)
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun deleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId, currentUser).thenAccept {
            selectedCart = Cart(selectedCart.value.filter { it.id != cartItemId }.toSet())
            showPageUI(currentPage)
            updateCartUI()
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean) {
        cartItemRepository.findById(cartItemId, currentUser).thenAccept {
            val loadedCartItem = it.getOrThrow()
            selectedCart = if (isSelected) {
                selectedCart.copy(value = selectedCart.value + loadedCartItem)
            } else {
                selectedCart.copy(value = selectedCart.value - loadedCartItem)
            }
            showAllSelectionUI(currentPage, selectedCart)
            showOrderUI(selectedCart)
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun updateSelectionTotalCartItems(isSelected: Boolean) {
        val offset = (currentPage - 1) * pageSize
        cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset, currentUser
        ).thenAccept { cartItemOfCurrentPageResult ->
            val cartItemsOfCurrentPage = cartItemOfCurrentPageResult.getOrThrow()
            if (isSelected) {
                selectedCart =
                    selectedCart.copy(value = selectedCart.value + cartItemsOfCurrentPage)
                updateCartUI()
            } else if (cartItemsOfCurrentPage.all { it in selectedCart.value }) {
                selectedCart =
                    selectedCart.copy(value = selectedCart.value - cartItemsOfCurrentPage.toSet())
                updateCartUI()
            }
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun plusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).thenAccept { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            val cartItem = loadedCartItem.plusQuantity()
            updateCount(cartItem)
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun minusCount(cartItemId: Long) {
        cartItemRepository.findById(cartItemId, currentUser).thenAccept { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            val cartItem = loadedCartItem.minusQuantity()
            updateCount(cartItem)
        }.exceptionally {
            view.showError(it.message.orEmpty())
            null
        }
    }

    override fun checkPayment() {
        val totalPrice = selectedCart.calculateTotalPrice()
        orderRepository.findDiscountPolicy(totalPrice, currentUser.rank.toString())
            .thenAccept { orderResult ->
                val order = orderResult.getOrThrow()
                view.showPayment(order.toUIState(), totalPrice - order.calculateDiscountPrice())
            }.exceptionally {
                view.showError(it.message.orEmpty())
                null
            }
    }

    override fun placeOrder() {
        orderRepository.save(selectedCart.value.map { it.id }, currentUser)
            .thenAccept { orderIdResult ->
                val orderId = orderIdResult.getOrThrow()
                view.showOrderDetail(orderId)
            }.exceptionally {
                view.showError(it.message.orEmpty())
                null
            }
    }

    private fun showCartItems(page: Int, selectedCartItems: Cart, initScroll: Boolean) {
        getCartItemsOf(page).thenAccept { cartItems ->
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
        getCartItemsOf(currentPage).thenAccept { cartItems ->
            view.setStateOfAllSelection(cartItems.all { it in selectedCartItems.value } && cartItems.isNotEmpty())
        }
    }

    private fun getCartItemsOf(page: Int): CompletableFuture<List<CartItem>> {
        val offset = (page - 1) * pageSize
        return cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset, currentUser
        ).thenApply {
            it.getOrElse {
                emptyList()
            }
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
        getMaxPage().thenAccept { maxPage ->
            view.setStateThatCanRequestNextPage(currentPage < maxPage)
            view.setStateThatCanRequestPage(maxPage > 1)
        }
    }

    private fun getMaxPage(): CompletableFuture<Int> {
        return cartItemRepository.countAll(currentUser).thenApply {
            val count = it.getOrElse {
                return@thenApply 0
            }
            (count - 1) / pageSize + 1
        }
    }

    private fun updateCount(cartItem: CartItem) {
        cartItemRepository.updateCountById(cartItem.id, cartItem.quantity, currentUser).thenAccept {
            it.getOrThrow()
            if (cartItem in selectedCart.value) {
                selectedCart = selectedCart.updateElement(cartItem, cartItem)
                showAllSelectionUI(currentPage, selectedCart)
                showOrderUI(selectedCart)
            }
            showCartItems(currentPage, selectedCart, false)
        }
    }
}
