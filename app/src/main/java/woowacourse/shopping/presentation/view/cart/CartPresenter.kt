package woowacourse.shopping.presentation.view.cart

import okio.IOException
import retrofit2.HttpException
import woowacourse.shopping.R
import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.model.page.PageNation

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    private lateinit var pageNation: PageNation

    private fun onFailure(throwable: Throwable) {
        val messageId = when (throwable) {
            is IOException -> { R.string.toast_message_network_error }
            is HttpException -> { R.string.toast_message_http_error }
            else -> { R.string.toast_message_system_error }
        }
        view.handleErrorView(messageId)
    }

    override fun initCartItems() {
        cartRepository.loadAllCarts(::onFailure) {
            setPageNation(it.map { cartRemoteEntity -> cartRemoteEntity.toUIModel() }, 1)
            setEnabledOrder()
            loadLocalCartItemChecked()
            loadCartItems()
            calculateTotalPrice()
            view.setLayoutVisibility()
        }
    }

    private fun setEnabledOrder() {
        view.setEnableOrderButton(pageNation.currentItems.isNotEmpty())
    }

    override fun setPageNation(cartProducts: List<CartProductModel>, currentPage: Int) {
        pageNation = PageNation(
            CartProductsModel(cartProducts).toModel(),
            currentPage
        )
    }

    override fun loadCartItems() {
        view.setEnableLeftButton(pageNation.hasPreviousPage())
        view.setEnableRightButton(pageNation.hasNextPage())

        val newCarts = pageNation.currentItems.map { it.toUIModel() }
        view.showCartItemsView(newCarts)
        view.setAllCartChecked(pageNation.isAllChecked)
    }

    private fun loadLocalCartItemChecked() {
        cartRepository.loadAllCartChecked().forEach { cart ->
            pageNation = pageNation.updateCartCheckedByCartId(cart.id, cart.checked)
        }
    }

    override fun deleteCartItem(cartId: Long) {
        pageNation = pageNation.deleteCartByCartId(cartId)

        cartRepository.deleteCart(cartId)

        setEnabledOrder()
        calculateTotalPrice()

        view.setEnableLeftButton(pageNation.hasPreviousPage())
        view.setEnableRightButton(pageNation.hasNextPage())

        view.showChangedCartItemsView(pageNation.currentItems.map { it.toUIModel() })
    }

    override fun setPreviousPage() {
        pageNation = pageNation.previousPage()
        view.showPageCountView(pageNation.currentPage)
    }

    override fun setNextPage() {
        pageNation = pageNation.nextPage()
        view.showPageCountView(pageNation.currentPage)
    }

    override fun calculateTotalPrice() {
        view.showTotalPriceView(pageNation.totalPrice)
    }

    override fun updateProductCount(cartId: Long, count: Int) {
        pageNation = pageNation.updateCartCountByCartId(cartId, count)

        pageNation.getCartByCartId(cartId)?.let {
            cartRepository.updateCartCount(it, ::onFailure) {
                if (count == 0) {
                    deleteCartItem(cartId)
                }
                calculateTotalPrice()
            }
        }
    }

    override fun updateProductChecked(cartId: Long, isChecked: Boolean) {
        pageNation = pageNation.updateCartCheckedByCartId(cartId, isChecked)

        cartRepository.updateCartChecked(cartId, isChecked)
        calculateTotalPrice()
        view.setAllCartChecked(pageNation.isAllChecked)
    }

    override fun updateCurrentPageAllProductChecked(isChecked: Boolean) {
        pageNation = pageNation.updateAllCartsChecked(isChecked)
        calculateTotalPrice()
        view.showChangedCartItemsView(pageNation.currentItems.map { it.toUIModel() })
    }

    override fun showOrder() {
        view.showOrderView(pageNation.getAllCheckedCartIds())
    }
}
