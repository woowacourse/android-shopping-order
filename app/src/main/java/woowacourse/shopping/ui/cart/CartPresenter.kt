package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.model.page.Pagination
import woowacourse.shopping.domain.repository.CartRemoteRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.ui.cart.CartContract.Presenter
import woowacourse.shopping.ui.cart.CartContract.View

class CartPresenter(
    view: View,
    private val cartRepository: CartRemoteRepository,
    cartSize: Int = 5,
) : Presenter(view) {
    private var cart: Cart = Cart(minProductSize = 1)
    private var currentPage: Page = Pagination(sizePerPage = cartSize)

    private val _totalCheckSize = MutableLiveData(cartRepository.getAllCartProducts().size)
    val totalCheckSize: LiveData<Int> get() = _totalCheckSize

    private val _pageCheckSize = MutableLiveData(currentPage.getCheckedProductSize(cart))
    val isAllChecked: LiveData<Boolean> = Transformations.map(_pageCheckSize) { pageCheckSize ->
        pageCheckSize == currentPage.takeItems(cart).size
    }

    init {
        updateCart(cart.update(loadCartProducts()))
    }

    override fun fetchCart(page: Int) {
        currentPage = currentPage.update(page)

        view.updateNavigatorEnabled(currentPage.hasPrevious(), currentPage.hasNext(cart))
        view.updatePageNumber(currentPage.toUi())
        fetchView()
    }

    override fun changeProductCount(cartProduct: UiCartProduct, count: Int) {
        val domainCartProduct = cartProduct.toDomain()
        val newCart = cart.changeProductCount(domainCartProduct, count)
        newCart.findCartProductById(domainCartProduct.productId)?.let { _cartProduct ->
            cartRepository.updateProductCountById(_cartProduct.id, _cartProduct.selectedCount)
        }
        updateCart(newCart)
    }

    override fun changeProductSelectState(cartProduct: UiCartProduct, isSelect: Boolean) {
        updateCart(changeSelectState(cartProduct.product, isSelect))
    }

    private fun changeSelectState(product: UiProduct, isSelect: Boolean): Cart =
        if (isSelect) cart.select(product.toDomain()) else cart.unselect(product.toDomain())

    override fun toggleAllCheckState() {
        updateCart(
            if (isAllChecked.value == true) {
                cart.unselectAll(currentPage)
            } else {
                cart.selectAll(currentPage)
            }
        )
    }

    override fun removeProduct(cartProduct: UiCartProduct) {
        cartRepository.deleteCartProductById(cartProduct.id)
        updateCart(cart.delete(cartProduct.toDomain()))
    }

    override fun order() {
        if (_totalCheckSize.value == 0) {
            view.showOrderFailed(); return
        }
        cart.items.forEach { cartRepository.deleteCartProductById(it.id) }
        view.showOrderComplete(_totalCheckSize.value ?: 0)
    }

    override fun navigateToHome() {
        view.navigateToHome()
    }

    private fun loadCartProducts(): List<CartProduct> {
        return cartRepository.getAllCartProducts()
    }

    private fun updateCart(newCart: Cart) {
        cart = cart.update(newCart)
        fetchView()
    }

    private fun fetchView() {
        _totalCheckSize.value = cart.checkedCount
        _pageCheckSize.value = currentPage.getCheckedProductSize(cart)
        view.updateNavigatorEnabled(currentPage.hasPrevious(), currentPage.hasNext(cart))
        view.updateTotalPrice(cart.checkedProductTotalPrice)
        view.updateCart(currentPage.takeItems(cart).toUi())
    }
}
