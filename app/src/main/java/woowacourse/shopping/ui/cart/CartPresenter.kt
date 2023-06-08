package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.cart.uistate.CartItemUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState
import woowacourse.shopping.utils.ErrorHandler.handle
import java.util.concurrent.CompletableFuture

class CartPresenter(
    private val view: CartContract.View,
    private val cartItemRepository: CartItemRepository,
    private val orderRepository: OrderRepository,
    private val pageSize: Int,
    private var selectedCart: Cart = Cart(emptySet()),
    private var currentPage: Int = 0
) : CartContract.Presenter {
    private val mainLooperHandler = Handler(Looper.getMainLooper())

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
        cartItemRepository.countAll().thenAccept {
            val count = it.getOrThrow()
            currentPage = (count - 1) / pageSize + 1
            showCartItems(currentPage, selectedCart, true)
            showPageUI(currentPage)
            showAllSelectionUI(currentPage, selectedCart)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun deleteCartItem(cartItemId: Long) {
        cartItemRepository.deleteById(cartItemId).thenAccept {
            selectedCart = Cart(selectedCart.value.filter { it.id != cartItemId }.toSet())
            showPageUI(currentPage)
            updateCartUI()
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean) {
        cartItemRepository.findById(cartItemId).thenAccept {
            val loadedCartItem = it.getOrThrow()
            selectedCart = if (isSelected) {
                selectedCart.copy(value = selectedCart.value + loadedCartItem)
            } else {
                selectedCart.copy(value = selectedCart.value - loadedCartItem)
            }
            showAllSelectionUI(currentPage, selectedCart)
            showOrderUI(selectedCart)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun updateSelectionTotalCartItems(isSelected: Boolean) {
        val offset = (currentPage - 1) * pageSize
        cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset
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
            it.handle(view)
            null
        }
    }

    override fun plusQuantity(cartItemId: Long) {
        cartItemRepository.findById(cartItemId).thenAccept { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            val cartItem = loadedCartItem.plusQuantity()
            updateCount(cartItem)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun minusQuantity(cartItemId: Long) {
        cartItemRepository.findById(cartItemId).thenAccept { loadedCartItemResult ->
            val loadedCartItem = loadedCartItemResult.getOrThrow()
            val cartItem = loadedCartItem.minusQuantity()
            updateCount(cartItem)
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun checkPayment() {
        val totalPrice = selectedCart.calculateTotalPrice()
        orderRepository.findDiscountPolicy(totalPrice).thenAccept { orderResult ->
            val order = orderResult.getOrThrow()
            mainLooperHandler.post {
                view.showPayment(order.toUIState(), totalPrice - order.calculateDiscountPrice())
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun placeOrder() {
        orderRepository.save(selectedCart.value.map { it.id }).thenAccept { orderIdResult ->
            val orderId = orderIdResult.getOrThrow()
            mainLooperHandler.post {
                view.showOrderDetail(orderId)
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    private fun showCartItems(page: Int, selectedCartItems: Cart, initScroll: Boolean) {
        getCartItemsOf(page).thenAccept { cartItems ->
            val cartItemUIStates =
                cartItems.map { cartItem -> cartItem.toUIState(cartItem.id in selectedCartItems.value.map { it.id }) }

            mainLooperHandler.post {
                view.setCartItems(cartItemUIStates, initScroll)
            }
        }
    }

    private fun updateCartUI() {
        showAllSelectionUI(currentPage, selectedCart)
        showOrderUI(selectedCart)
        showCartItems(currentPage, selectedCart, false)
    }

    private fun showAllSelectionUI(currentPage: Int, selectedCartItems: Cart) {
        getCartItemsOf(currentPage).thenAccept { cartItems ->
            mainLooperHandler.post {
                view.setStateOfAllSelection(cartItems.all { it in selectedCartItems.value } && cartItems.isNotEmpty())
            }
        }
    }

    private fun getCartItemsOf(page: Int): CompletableFuture<List<CartItem>> {
        val offset = (page - 1) * pageSize
        return cartItemRepository.findAllOrderByAddedTime(
            pageSize, offset
        ).thenApply {
            it.getOrElse {
                emptyList()
            }
        }
    }

    private fun showOrderUI(selectedCartItems: Cart) {
        mainLooperHandler.post {
            view.setOrderPrice(selectedCartItems.calculateTotalPrice())
            view.setOrderCount(selectedCartItems.value.size)
        }
    }

    private fun showPageUI(currentPage: Int) {
        refreshStateThatCanRequestPage(currentPage)
        mainLooperHandler.post {
            view.setPage(currentPage)
        }
    }

    private fun refreshStateThatCanRequestPage(currentPage: Int) {
        mainLooperHandler.post {
            view.setStateThatCanRequestPreviousPage(currentPage > 1)
        }
        getMaxPage().thenAccept { maxPage ->
            mainLooperHandler.post {
                view.setStateThatCanRequestNextPage(currentPage < maxPage)
                view.setStateThatCanRequestPage(maxPage > 1)
            }
        }
    }

    private fun getMaxPage(): CompletableFuture<Int> {
        return cartItemRepository.countAll().thenApply {
            val count = it.getOrElse {
                return@thenApply 0
            }
            (count - 1) / pageSize + 1
        }
    }

    private fun updateCount(cartItem: CartItem) {
        cartItemRepository.updateCountById(cartItem.id, cartItem.quantity).thenAccept { result ->
            result.getOrThrow()
            showCartItems(currentPage, selectedCart, false)
        }
    }
}
