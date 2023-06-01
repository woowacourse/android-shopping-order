package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.CartProducts
import com.example.domain.model.Pagination
import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartBottomNavigationUiModel
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageBottomNavigationUiModel
import woowacourse.shopping.model.PaginationUiModel

class CartPresenter(
    val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    private var _page: PaginationUiModel? = null
        set(value) {
            value?.let {
                _currentPageCartProducts.postValue(it.currentPageCartProducts)
                _pageBottomNavigationUiModel.postValue(it.pageBottomNavigationUiModel)
                _cartBottomNavigationUiModel.postValue(it.cartBottomNavigationUiModel)
            }
            field = value
        }
    private val page: PaginationUiModel
        get() = _page!!

    private val _currentPageCartProducts: MutableLiveData<List<CartProductUiModel>> =
        MutableLiveData()
    override val currentPageCartProducts: LiveData<List<CartProductUiModel>>
        get() = _currentPageCartProducts

    private val _pageBottomNavigationUiModel: MutableLiveData<PageBottomNavigationUiModel> =
        MutableLiveData()
    override val pageBottomNavigationUiModel: LiveData<PageBottomNavigationUiModel>
        get() = _pageBottomNavigationUiModel

    private val _cartBottomNavigationUiModel: MutableLiveData<CartBottomNavigationUiModel> =
        MutableLiveData()
    override val cartBottomNavigationUiModel: LiveData<CartBottomNavigationUiModel>
        get() = _cartBottomNavigationUiModel

    override fun loadInitCartProduct() {
        view.showLoadingView()
        cartRepository.getAll(
            onSuccess = {
                _page = Pagination(CartProducts(it), 1).toPresentation()
                view.hideLoadingView()
            },
            onFailure = {},
        )
    }

    override fun loadPreviousPage() {
        if (page.pageBottomNavigationUiModel.hasPreviousPage.not()) return
        _page = page.toDomain().previousPage().toPresentation()
    }

    override fun loadNextPage() {
        if (page.pageBottomNavigationUiModel.hasNextPage.not()) return
        _page = page.toDomain().nextPage().toPresentation()
    }

    override fun handleDeleteCartProductClick(cartId: Long) {
        cartRepository.deleteCartProduct(
            cartId,
            onSuccess = {
                _page = page.toDomain().remove(cartId).toPresentation()
            },
            onFailure = {},
        )
    }

    override fun handleCartProductCartCountChange(cartId: Long, count: Int) {
        cartRepository.changeCartProductCount(
            cartId,
            count,
            onSuccess = {
                _page = page.toDomain().changeCountState(cartId, count).toPresentation()
            },
            onFailure = {},
        )
    }

    override fun handlePurchaseSelectedCheckedChange(cartId: Long, checked: Boolean) {
        _page = page.toDomain().changeChecked(cartId, checked).toPresentation()
    }

    override fun handleCurrentPageAllCheckedChange(checked: Boolean) {
        _page = page.toDomain().setCurrentPageAllChecked(checked).toPresentation()
    }

    override fun requestOrderConfirmScreen() {
        if (page.cartBottomNavigationUiModel.isAnyChecked.not()) return
        _page?.checkedCartIds?.let { view.showOrderConfirmScreen(it) }
    }

    override fun processRemoveOrderCheckedItems() {
        _page = page.toDomain().removeAllChecked().toPresentation()
    }

    override fun exit() {
        view.exitCartScreen()
    }
}
