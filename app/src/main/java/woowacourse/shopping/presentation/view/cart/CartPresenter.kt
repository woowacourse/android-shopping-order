package woowacourse.shopping.presentation.view.cart

import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacouse.shopping.data.repository.cart.CartRepository

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var currentPage: Int = 1,
) : CartContract.Presenter {
    private var carts = CartProductsModel(listOf())

    private fun onFailure() {
        view.handleErrorView()
    }

    private val startPosition: Int
        get() = (currentPage - 1) * DISPLAY_CART_COUNT_CONDITION

    override fun initCartItems() {
        cartRepository.loadAllCarts(::onFailure) {
            carts = CartProductsModel(it.map { cartRemoteEntity -> cartRemoteEntity.toUIModel() })
            loadLocalCartItemChecked()
            loadCartItems()
            calculateTotalPrice()
            view.setLayoutVisibility()
        }
    }

    override fun loadCartItems() {
        view.setEnableLeftButton(currentPage != FIRST_PAGE_NUMBER)
        view.setEnableRightButton(carts.toModel().size > startPosition + DISPLAY_CART_COUNT_CONDITION)

        val newCarts = getCurrentPageCarts()
        view.setCartItemsView(newCarts)
        view.setAllCartChecked(isAllChecked())
    }

    private fun loadLocalCartItemChecked() {
        cartRepository.getAllLocalCart().forEach { cart ->
            carts = carts.toModel().updateCartChecked(cart.id, cart.checked).toUIModel()
        }
    }

    private fun getCurrentPageCarts(): List<CartModel> {
        return carts.toModel().getAll().subList(startPosition, getCurrentPageCartLastIndex())
            .map { it.toUIModel() }
    }

    private fun getCurrentPageCartLastIndex(): Int {
        val cartsSize = carts.toModel().size
        if (cartsSize > startPosition + DISPLAY_CART_COUNT_CONDITION) {
            return startPosition + DISPLAY_CART_COUNT_CONDITION
        }
        return cartsSize
    }

    override fun deleteCartItem(cartId: Long) {
        carts = carts.toModel().deleteCart(cartId).toUIModel()

        cartRepository.deleteLocalCart(cartId)
        cartRepository.deleteCart(cartId)

        view.setEnableLeftButton(currentPage != FIRST_PAGE_NUMBER)
        view.setEnableRightButton(carts.toModel().size > startPosition + DISPLAY_CART_COUNT_CONDITION)

        view.setChangedCartItemsView(
            carts.toModel().getAll()
                .subList(startPosition, getCurrentPageCartLastIndex())
                .map { it.toUIModel() }
        )
    }

    override fun calculatePreviousPage() {
        view.setPageCountView(--currentPage)
    }

    override fun calculateNextPage() {
        view.setPageCountView(++currentPage)
    }

    override fun calculateTotalPrice() {
        val totalPrice = carts.toModel().totalPrice
        view.setTotalPriceView(totalPrice)
    }

    override fun updateProductCount(cartId: Long, count: Int) {
        carts = carts.toModel().updateCartCountByCartId(cartId, count).toUIModel()

        carts.toModel().getCartByCartId(cartId)?.let { cartProduct ->
            cartRepository.updateCartCount(cartProduct, ::onFailure) {
                if (count == 0) {
                    deleteCartItem(cartId)
                }
            }
        }
    }

    override fun updateProductChecked(cartId: Long, isChecked: Boolean) {
        carts = carts.toModel().updateCartChecked(cartId, isChecked).toUIModel()
        cartRepository.updateLocalCartChecked(cartId, isChecked)
        view.setAllCartChecked(isAllChecked())
    }

    private fun isAllChecked(): Boolean {
        return getCurrentPageCarts().all { it.checked }
    }

    override fun updateCurrentPageAllProductChecked(isChecked: Boolean) {
        for (index in startPosition until getCurrentPageCartLastIndex()) {
            updateProductChecked(carts.carts[index].id, isChecked)
        }
        view.setChangedCartItemsView(carts.carts.subList(startPosition, getCurrentPageCartLastIndex()))
    }

    companion object {
        private const val FIRST_PAGE_NUMBER = 1
        private const val DISPLAY_CART_COUNT_CONDITION = 3
    }
}
