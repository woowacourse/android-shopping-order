package woowacourse.shopping.presentation.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.ProductListItem.ShoppingProductItem.Companion.joinProductAndCart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.util.Event

class CartViewModel(private val cartRepository: CartRepository) : ViewModel(), CartHandler {
    private val _isRightButtonEnabled = MutableLiveData<Boolean>()
    val isRightButtonEnabled: LiveData<Boolean> get() = _isRightButtonEnabled

    private val _isLeftButtonEnabled = MutableLiveData<Boolean>()
    val isLeftButtonEnabled: LiveData<Boolean> get() = _isLeftButtonEnabled

    private val _pageControllerIsVisible = MutableLiveData<Boolean>()
    val pageControllerIsVisible: LiveData<Boolean> get() = _pageControllerIsVisible

    private var maxPage: Int = 0

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> get() = _currentPage

    private val _error = MutableLiveData<Event<CartError>>()

    val error: LiveData<Event<CartError>> = _error

    private val _shoppingProducts =
        MutableLiveData<UiState<List<ProductListItem.ShoppingProductItem>>>(UiState.Loading)

    val shoppingProducts: LiveData<UiState<List<ProductListItem.ShoppingProductItem>>> get() = _shoppingProducts

    private val cartProducts = mutableListOf<Cart>()

    private val _changedCartProducts = MutableLiveData<List<Cart>>()
    val changedCartProducts: LiveData<List<Cart>> get() = _changedCartProducts

    override fun onDeleteClick(product: ProductListItem.ShoppingProductItem) {
        cartRepository.deleteExistCartItem(product.cartId, onSuccess = { cartId, newQuantity ->
            val originalChangedCartProducts = changedCartProducts.value ?: emptyList()
            _changedCartProducts.value =
                originalChangedCartProducts.plus(Cart(cartId, product.toProduct(), newQuantity))
            updatePageController()
            loadProductByPage(currentPage.value ?: 0)
        }, onFailure = { _error.value = Event(CartError.CartItemNotDeleted) })
    }

    fun updatePageController() {
        if (currentPage.value != null) {
            _currentPage.value = currentPage.value!!.coerceIn(0, maxPage)
            _isLeftButtonEnabled.value = currentPage.value!! > 0
            _isRightButtonEnabled.value = currentPage.value!! < maxPage - 1
        }
        _pageControllerIsVisible.value = maxPage > 0
    }

    fun loadProductByPage(newPage: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            cartRepository.load(newPage, PAGE_SIZE, onSuccess = { carts, totalPage ->
                maxPage = totalPage
                cartProducts.apply {
                    clear()
                    addAll(carts)
                }
                _shoppingProducts.value =
                    UiState.Success(
                        cartProducts.map {
                            joinProductAndCart(
                                it.product,
                                it,
                            )
                        },
                    )
            }, onFailure = { _error.value = Event(CartError.CartItemsNotFound) })
        }, 500)
    }

    private fun modifyShoppingProductQuantity(
        cartId: Long,
        resultQuantity: Int,
    ) {
        val productIndex = cartProducts.indexOfFirst { it.cartId == cartId }
        val updatedItem = cartProducts[productIndex].copy(quantity = resultQuantity)
        cartProducts[productIndex] = updatedItem
        _shoppingProducts.value =
            UiState.Success(
                cartProducts.map {
                    joinProductAndCart(
                        it.product,
                        it,
                    )
                },
            )
        val originalChangedCartProducts = changedCartProducts.value ?: emptyList()
        _changedCartProducts.value = originalChangedCartProducts.plus(updatedItem)
    }

    override fun onNextPageClick() {
        if (currentPage.value == maxPage - 1) return
        val newPage = ((currentPage.value ?: 0) + 1).coerceAtMost(maxPage)
        _currentPage.value = newPage
        loadProductByPage(newPage)
    }

    override fun onBeforePageClick() {
        if (currentPage.value == 0) return
        val newPage = (currentPage.value ?: 0) - 1
        _currentPage.value = newPage
        loadProductByPage(newPage)
    }

    override fun onDecreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        val updatedQuantity = item?.let { it.quantity - 1 } ?: 1
        if (updatedQuantity > 0) {
            item?.let { item ->
                cartRepository.updateDecrementQuantity(
                    item.cartId,
                    item.id,
                    1,
                    item.quantity,
                    onSuccess = { _, resultQuantity ->
                        modifyShoppingProductQuantity(item.cartId, resultQuantity)
                    },
                    onFailure = {},
                )
            }
        }
    }

    override fun onIncreaseQuantity(item: ProductListItem.ShoppingProductItem?) {
        item?.let { item ->
            cartRepository.updateIncrementQuantity(
                item.cartId,
                item.id,
                1,
                item.quantity,
                onSuccess = { cartId, resultQuantity ->
                    modifyShoppingProductQuantity(item.cartId, resultQuantity)
                },
                onFailure = {},
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
