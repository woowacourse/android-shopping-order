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
import woowacourse.shopping.model.PageModel
import woowacourse.shopping.model.PriceModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.mapper.toDomain
import woowacourse.shopping.model.mapper.toUi
import woowacourse.shopping.ui.cart.CartContract.Presenter
import woowacourse.shopping.ui.cart.CartContract.View

class CartPresenter(
    private val view: View,
    private val cartRepository: CartRepository,
    cartSize: Int = 5,
) : Presenter {
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
        view.updatePageState(mapToPageModel(currentPage))
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

    override fun deleteProduct(cartProductModel: CartProductModel) {
        cartRepository.deleteCartProductById(cartProductModel.id)
        updateCart(cart.delete(cartProductModel.toDomain()))
    }

    override fun updateProductCount(cartProductModel: CartProductModel, count: Int) {
        val cartProduct = cartProductModel.toDomain()
        val newCart = cart.updateProductCount(cartProduct, count)

        newCart.findCartProductByProductId(cartProduct.productId)?.let { cartItem ->
            cartRepository.updateProductCountById(cartItem.id, cartItem.selectedCount)
        }
        updateCart(newCart)
    }

    override fun toggleAllCheckState() {
        val toggledCart = if (isAllChecked.value == true) {
            cart.unselectAll(currentPage)
        } else {
            cart.selectAll(currentPage)
        }
        updateCart(toggledCart)
    }

    override fun updateProductSelectState(cartProductModel: CartProductModel, isSelect: Boolean) {
        updateCart(changeSelectState(cartProductModel.product, isSelect))
    }

    private fun changeSelectState(productModel: ProductModel, isSelect: Boolean): Cart {
        val product = productModel.toDomain()
        return if (isSelect) cart.select(product) else cart.unselect(product)
    }

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

        view.updatePageState(mapToPageModel(currentPage))
        view.updateTotalPrice(cart.checkedProductTotalPrice)
        view.updateCart(currentPage.takeItems(cart).toUi())
    }

    private fun mapToPageModel(page: Page): PageModel = page.toUi(
        hasPrevious = currentPage.hasPrevious(),
        hasNext = currentPage.hasNext(cart)
    )

    companion object {
        private const val DEFAULT_TOTAL_CHECK_COUNT = 0
    }
}
