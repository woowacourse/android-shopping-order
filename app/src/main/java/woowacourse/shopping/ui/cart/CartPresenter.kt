package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.OrderProduct
import woowacourse.shopping.domain.model.Payment
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.model.page.Pagination
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.cart.CartContract.Presenter
import woowacourse.shopping.ui.cart.CartContract.View

class CartPresenter(
    view: View,
    private val cartRepository: CartRepository,
    cartSize: Int = 5,
) : Presenter(view) {
    private var cart: Cart = Cart(minProductSize = 1)
    private var currentPage: Page = Pagination(sizePerPage = cartSize)

    private val _totalCheckSize = MutableLiveData(0)
    val totalCheckSize: LiveData<Int> get() = _totalCheckSize

    private val _pageCheckSize = MutableLiveData(currentPage.getCheckedProductSize(cart))
    val isAllChecked: LiveData<Boolean> = Transformations.map(_pageCheckSize) { pageCheckSize ->
        pageCheckSize == currentPage.takeItems(cart).size
    }

    init {
        fetchCartProducts()
    }

    override fun fetchCart(page: Int) {
        currentPage = currentPage.update(page)

        view.updateNavigatorEnabled(currentPage.hasPrevious(), currentPage.hasNext(cart))
        view.updatePageNumber(currentPage.toUi())
        fetchView()
    }

    override fun order() {
        val checkedCartItemsPrice = PriceModel(cart.checkedProductTotalPrice).toDomain()
        val order = OrderModel(
            orderProducts = cart.getCheckedCartItems().map(OrderProduct::of).toUi(),
            payment = Payment.of(checkedCartItemsPrice).toUi()
        )
        view.navigateToOrder(order)
    }

    override fun removeProduct(cartProduct: CartProductModel) {
        cartRepository.deleteCartProductById(cartProduct.id)
        updateCart(cart.delete(cartProduct.toDomain()))
    }

    override fun changeProductCount(cartProduct: CartProductModel, count: Int) {
        val domainCartProduct = cartProduct.toDomain()
        val newCart = cart.changeProductCount(domainCartProduct, count)

        newCart.findCartProductByProductId(domainCartProduct.productId)?.let { _cartProduct ->
            cartRepository.updateProductCountById(_cartProduct.id, _cartProduct.selectedCount)
        }
        updateCart(newCart)
    }

    override fun toggleAllCheckState() {
        updateCart(
            if (isAllChecked.value == true) {
                cart.unselectAll(currentPage)
            } else {
                cart.selectAll(currentPage)
            }
        )
    }

    override fun changeProductSelectState(cartProduct: CartProductModel, isSelect: Boolean) {
        updateCart(changeSelectState(cartProduct.product, isSelect))
    }

    private fun changeSelectState(product: ProductModel, isSelect: Boolean): Cart =
        if (isSelect) cart.select(product.toDomain()) else cart.unselect(product.toDomain())

    override fun navigateToHome() {
        view.navigateToHome()
    }

    private fun fetchCartProducts() {
        cartRepository.getAllCartProducts(
            onSuccess = { cartProducts ->
                updateCart(cart.update(cartProducts))
                _totalCheckSize.postValue(cartProducts.size)
            },
            onFailed = {
                view.showErrorMessage(it.message ?: "")
                _totalCheckSize.postValue(DEFAULT_TOTAL_CHECK_COUNT)
            },
        )
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

    companion object {
        private const val DEFAULT_TOTAL_CHECK_COUNT = 0
    }
}
