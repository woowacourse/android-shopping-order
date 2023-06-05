package woowacourse.shopping.presentation.view.cart

import com.example.domain.page.PageNation
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.respository.cart.CartRepository
import woowacourse.shopping.presentation.model.CartModel

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var currentPage: Int = 1,
) : CartContract.Presenter {

    private lateinit var pageNation: PageNation

    private fun onFailure() {
        view.handleErrorView()
    }

    private val startPosition: Int
        get() = (currentPage - 1) * DISPLAY_CART_COUNT_CONDITION

    override fun initCartItems() {
        cartRepository.loadAllCarts(::onFailure) { cartProducts ->
            pageNation = PageNation(
                cartProducts,
                currentPage,
            )

            loadLocalCartItemChecked()
            loadCartItems()
            calculateTotalPrice()
            view.setLayoutVisibility()
        }
    }

    override fun loadCartItems() {
        view.setEnableLeftButton(pageNation.hasPreviousPage())
        view.setEnableRightButton(pageNation.hasNextPage())

        val newCarts = getCurrentPageCarts()
        view.setCartItemsView(newCarts)
        view.setAllCartChecked(isAllChecked())
    }

    private fun loadLocalCartItemChecked() {
        cartRepository.getAllLocalCart().forEach { cart ->
            pageNation.currentPageCartProducts.find { it.id == cart.id } ?: return@forEach
            pageNation = pageNation.updateCheckedState(cart.id, cart.checked == 1)
        }
    }

    private fun getCurrentPageCarts(): List<CartModel> {
        return pageNation.currentPageCartProducts.map { cartProduct -> cartProduct.toUiModel() }
    }

    private fun getCurrentPageCartLastIndex(): Int =
        if (pageNation.allSize > startPosition + DISPLAY_CART_COUNT_CONDITION) {
            startPosition + DISPLAY_CART_COUNT_CONDITION
        } else {
            pageNation.allSize
        }

    override fun deleteCartItem(itemId: Long) {
        pageNation = pageNation.remove(itemId)
        cartRepository.deleteLocalCart(itemId)
        cartRepository.deleteCart(itemId)

        view.setEnableLeftButton(pageNation.hasPreviousPage())
        view.setEnableRightButton(pageNation.hasNextPage())

        view.setChangedCartItemsView(
            pageNation.allList.map { it.toUiModel() }
                .subList(startPosition, getCurrentPageCartLastIndex()),
        )
    }

    override fun calculatePreviousPage() {
        pageNation = pageNation.previousPage()
        view.setPageCountView(pageNation.currentPage)
    }

    override fun calculateNextPage() {
        pageNation = pageNation.nextPage()
        view.setPageCountView(pageNation.currentPage)
    }

    override fun calculateTotalPrice() {
        val totalPrice = pageNation.totalCheckedPrice
        view.setTotalPriceView(totalPrice)
    }

    override fun updateProductCount(cartId: Long, count: Int) {
        pageNation.currentPageCartProducts.find { it.id == cartId } ?: return
        pageNation = pageNation.updateCountState(cartId, count)
        val cartProduct = pageNation.currentPageCartProducts.find { it.id == cartId } ?: return

        cartRepository.updateCartCount(cartProduct, ::onFailure) {
            if (count == 0) {
                deleteCartItem(cartId)
            }
        }
    }

    override fun updateProductChecked(cartId: Long, isChecked: Boolean) {
        pageNation.currentPageCartProducts.find { it.id == cartId }?.run {
            if (checked == isChecked) return@run
            pageNation = pageNation.updateCheckedState(cartId, isChecked)
            cartRepository.updateLocalCartChecked(id, isChecked)
            view.setAllCartChecked(isAllChecked())
        }
    }

    private fun isAllChecked(): Boolean {
        return pageNation.isAllCheckedCurrentPage
    }

    override fun updateCurrentPageAllProductChecked(isChecked: Boolean) {
        val currentPageCarts = getCurrentPageCarts()
        val currentPage = pageNation.currentPage

        currentPageCarts.forEachIndexed { index, cartModel ->
            updateProductChecked(pageNation.currentPageCartProducts[DISPLAY_CART_COUNT_CONDITION * (currentPage - 1) + index].id, isChecked)
        }

        view.updateAllChecking(DISPLAY_CART_COUNT_CONDITION * (currentPage - 1), DISPLAY_CART_COUNT_CONDITION)
        view.setChangedCartItemsView(
            pageNation.currentPageCartProducts.map { it.toUiModel() }
                .subList(startPosition, getCurrentPageCartLastIndex()),
        )
    }

    override fun order() {
        val cartItems = pageNation.getCheckedProducts().map { it.toUiModel() }
        view.moveToOrderView(cartItems)
    }

    companion object {
        private const val DISPLAY_CART_COUNT_CONDITION = 5
    }
}
