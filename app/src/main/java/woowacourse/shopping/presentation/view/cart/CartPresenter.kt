package woowacourse.shopping.presentation.view.cart

import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.model.page.PageNation

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    private lateinit var pageNation: PageNation

    private fun onFailure() {
        view.handleErrorView()
    }

    override fun initCartItems() {
        cartRepository.loadAllCarts(::onFailure) {
            pageNation = PageNation(
                CartProductsModel(
                    it.map { cartRemoteEntity -> cartRemoteEntity.toUIModel() }
                ).toModel(),
                1
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

        val newCarts = pageNation.currentItems.map { it.toUIModel() }
        view.setCartItemsView(newCarts)
        view.setAllCartChecked(pageNation.isAllChecked)
    }

    private fun loadLocalCartItemChecked() {
        cartRepository.getAllLocalCart().forEach { cart ->
            pageNation = pageNation.updateCartCheckedByCartId(cart.id, cart.checked)
        }
    }

    override fun deleteCartItem(cartId: Long) {
        pageNation = pageNation.deleteCartByCartId(cartId)

        cartRepository.deleteLocalCart(cartId)
        cartRepository.deleteCart(cartId)

        view.setEnableLeftButton(pageNation.hasPreviousPage())
        view.setEnableRightButton(pageNation.hasNextPage())

        view.setChangedCartItemsView(pageNation.currentItems.map { it.toUIModel() })
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
        view.setTotalPriceView(pageNation.totalPrice)
    }

    override fun updateProductCount(cartId: Long, count: Int) {
        pageNation = pageNation.updateCartCountByCartId(cartId, count)

        pageNation.getCartByCartId(cartId)?.let {
            cartRepository.updateCartCount(it, ::onFailure) {
                if (count == 0) {
                    deleteCartItem(cartId)
                }
            }
        }
    }

    override fun updateProductChecked(cartId: Long, isChecked: Boolean) {
        pageNation = pageNation.updateCartCheckedByCartId(cartId, isChecked)

        cartRepository.updateLocalCartChecked(cartId, isChecked)
        view.setAllCartChecked(pageNation.isAllChecked)
    }

    override fun updateCurrentPageAllProductChecked(isChecked: Boolean) {
        pageNation = pageNation.updateAllCartsChecked(isChecked)
        view.setChangedCartItemsView(pageNation.currentItems.map { it.toUIModel() })
    }
}
