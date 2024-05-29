package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.common.Event
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.product.remote.retrofit.DataCallback
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartPageAttribute
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.ProductRepository

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: RemoteCartRepository,
) : ViewModel(), CartListener {
    private val _cartUiState = MutableLiveData<Event<CartUiState>>()
    val cartUiState: LiveData<Event<CartUiState>> get() = _cartUiState

    private val _page = MutableLiveData<Int>(INITIALIZE_PAGE)
    val page: LiveData<Int> get() = _page

    private val cartPageAttribute = MutableLiveData<CartPageAttribute>()

    val hasPage: LiveData<Boolean> = cartPageAttribute.map { it.totalPageCount == 1 }
    val hasPreviousPage: LiveData<Boolean> = cartPageAttribute.map { !it.isFirst }
    val hasNextPage: LiveData<Boolean> = cartPageAttribute.map { !it.isLast }

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    init {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ loadCart() }, 1000)
    }

    private fun loadCart(page: Int = INITIALIZE_PAGE) {
        _cartUiState.value = Event(CartUiState.Loading)
        cartRepository.getCartItems(
            page,
            PAGE_SIZE,
            object : DataCallback<List<CartItem>> {
                override fun onSuccess(result: List<CartItem>) {
                    _cartUiState.value = Event(CartUiState.Success(result.toCartUiModels()))
                }

                override fun onFailure(t: Throwable) {
                    _cartUiState.value = Event(CartUiState.Failure)
                }
            },
        )
        loadCartPageAttribute()
    }

    private fun List<CartItem>.toCartUiModels(): List<CartUiModel> {
        return map {
            val product = productRepository.find(it.productId)
            CartUiModel.from(product, it)
        }
    }

    private fun loadCartPageAttribute() {
        cartRepository.getCartPageAttribute(
            _page.value ?: INITIALIZE_PAGE,
            PAGE_SIZE,
            object : DataCallback<CartPageAttribute> {
                override fun onSuccess(result: CartPageAttribute) {
                    cartPageAttribute.value = result
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    override fun deleteCartItem(productId: Int) {
        _changedCartEvent.value = Event(Unit)
        val cartUiModel = productUiModel(productId) ?: return
        cartRepository.deleteCartItem(
            cartUiModel.cartItemId,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    updateDeletedCart()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun updateDeletedCart() {
        if (isEmptyLastPage()) {
            movePreviousPage()
            return
        }
        loadCart(_page.value ?: INITIALIZE_PAGE)
    }

    private fun isEmptyLastPage(): Boolean {
        val page = _page.value ?: return false
        val totalCartCount = cartPageAttribute.value?.totalCartItemCount ?: return false
        return page > 0 && totalCartCount % PAGE_SIZE == 1
    }

    fun moveNextPage() {
        _page.value = nextPage(_page.value ?: INITIALIZE_PAGE)
    }

    private fun nextPage(page: Int): Int {
        runCatching { loadCart(page + 1) }
            .onSuccess { return page + 1 }
            .onFailure { _cartUiState.value = Event(CartUiState.Failure) }
        return page
    }

    fun movePreviousPage() {
        _page.value = previousPage(_page.value ?: INITIALIZE_PAGE)
    }

    private fun previousPage(page: Int): Int {
        runCatching { loadCart(page - 1) }
            .onSuccess { return page - 1 }
            .onFailure { _cartUiState.value = Event(CartUiState.Failure) }
        return page
    }

    override fun increaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = productUiModel(productId) ?: return
        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, ++newQuantity)
    }

    override fun decreaseQuantity(productId: Int) {
        _changedCartEvent.value = Event(Unit)

        val cartUiModel = productUiModel(productId) ?: return
        if (cartUiModel.quantity.count == 1) {
            deleteCartItem(productId)
            return
        }

        var newQuantity = cartUiModel.quantity
        setQuantity(cartUiModel.cartItemId, --newQuantity)
    }

    private fun setQuantity(
        cartItemId: Int,
        quantity: Quantity,
    ) {
        cartRepository.setCartItemQuantity(
            cartItemId,
            quantity,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    loadCart(_page.value ?: INITIALIZE_PAGE)
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun setError() {
        _cartUiState.value = Event(CartUiState.Failure)
    }

    private fun productUiModel(productId: Int): CartUiModel? {
        return productUiModels()?.find { it.productId == productId }
    }

    private fun productUiModels(): List<CartUiModel>? {
        val cartUiState = cartUiState.value?.peekContent() ?: return null
        if (cartUiState is CartUiState.Success) {
            return cartUiState.cartUiModels
        }
        return null
    }

    companion object {
        private const val INITIALIZE_CART_SIZE = 0
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 5
    }
}
