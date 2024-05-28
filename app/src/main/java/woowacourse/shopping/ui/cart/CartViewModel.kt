package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.common.Event
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.entity.CartItem
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), CartListener {
    private val _cartUiState = MutableLiveData<Event<CartUiState>>()
    val cartUiState: LiveData<Event<CartUiState>> get() = _cartUiState

    private val totalCartCount = MutableLiveData<Int>(INITIALIZE_CART_SIZE)

    private val _page = MutableLiveData<Int>(INITIALIZE_PAGE)
    val page: LiveData<Int> get() = _page

    private val maxPage = MutableLiveData<Int>(INITIALIZE_PAGE)

    val hasPage: LiveData<Boolean> = totalCartCount.map { it > PAGE_SIZE }
    val hasPreviousPage: LiveData<Boolean> = _page.map { it > INITIALIZE_PAGE }
    val hasNextPage: LiveData<Boolean> = _page.map { it < (maxPage.value ?: INITIALIZE_PAGE) }

    private val _changedCartEvent = MutableLiveData<Event<Unit>>()
    val changedCartEvent: LiveData<Event<Unit>> get() = _changedCartEvent

    init {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ loadCart() }, 1000)
    }

    private fun loadCart(page: Int = INITIALIZE_PAGE) {
        _cartUiState.value = Event(CartUiState.Loading)
        runCatching { cartRepository.findRange(page, PAGE_SIZE) }
            .onSuccess { _cartUiState.value = Event(CartUiState.Success(it.toProductUiModels())) }
            .onFailure { _cartUiState.value = Event(CartUiState.Failure) }
        loadTotalCartCount()
    }

    private fun List<CartItem>.toProductUiModels(): List<ProductUiModel> {
        return map {
            val product = productRepository.find(it.productId)
            ProductUiModel.from(product, it.quantity)
        }
    }

    private fun loadTotalCartCount() {
        val totalCartCount = cartRepository.totalCartItemCount()
        this.totalCartCount.value = totalCartCount
        maxPage.value = (totalCartCount - 1) / PAGE_SIZE
    }

    override fun deleteCartItem(productId: Long) {
        _changedCartEvent.value = Event(Unit)
        cartRepository.deleteCartItem(productId)
        updateDeletedCart()
    }

    private fun updateDeletedCart() {
        if (isEmptyLastPage()) {
            movePreviousPage()
            return
        }
        loadCart(_page.value ?: INITIALIZE_PAGE)
    }

    private fun isEmptyLastPage(): Boolean {
        val page = _page.value ?: INITIALIZE_PAGE
        val totalCartCount = totalCartCount.value ?: INITIALIZE_CART_SIZE
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

    override fun increaseQuantity(productId: Long) {
        _changedCartEvent.value = Event(Unit)
        cartRepository.increaseQuantity(productId)
        loadCart(_page.value ?: INITIALIZE_PAGE)
    }

    override fun decreaseQuantity(productId: Long) {
        _changedCartEvent.value = Event(Unit)
        cartRepository.decreaseQuantity(productId)
        runCatching {
            cartRepository.find(productId)
        }.onSuccess {
            loadCart(_page.value ?: INITIALIZE_PAGE)
        }.onFailure {
            updateDeletedCart()
        }
    }

    companion object {
        private const val INITIALIZE_CART_SIZE = 0
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 5
    }
}
