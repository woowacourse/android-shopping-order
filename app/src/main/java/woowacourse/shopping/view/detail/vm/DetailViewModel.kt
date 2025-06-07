package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.core.handler.CartQuantityHandler
import woowacourse.shopping.view.detail.DetailActivity.Companion.NO_LAST_SEEN_PRODUCT
import woowacourse.shopping.view.detail.DetailUiEvent
import woowacourse.shopping.view.main.state.ProductState

class DetailViewModel(
    private val defaultProductRepository: ProductRepository,
    private val defaultCartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<DetailUiState>()
    val uiState: LiveData<DetailUiState> get() = _uiState

    private val _event = MutableSingleLiveData<DetailUiEvent>()
    val event: SingleLiveData<DetailUiEvent> get() = _event

    fun load(
        productId: Long,
        lastSeenProductId: Long,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val productResult = defaultProductRepository.loadProduct(productId)
            productResult.onSuccess { productValue ->
                val lastSeenProduct =
                    if (lastSeenProductId != NO_LAST_SEEN_PRODUCT && lastSeenProductId != productId) {
                        defaultProductRepository.loadProduct(lastSeenProductId).getOrNull()
                    } else null

                _uiState.postValue(
                    DetailUiState(
                        product = ProductState(item = productValue, cartQuantity = Quantity(1)),
                        lastSeenProduct = lastSeenProduct,
                    )
                )
            }
        }
        saveHistory(productId)
    }

    fun increaseCartQuantity() {
        withState(_uiState.value) { state ->
            val result = state.product.increaseCartQuantity()
            _uiState.value = state.copy(product = result)
        }
    }

    fun decreaseCartQuantity() {
        withState(_uiState.value) { state ->
            val product = state.product
            val decreasedCartQuantity = (product.cartQuantity - 1)

            val quantity =
                if (!decreasedCartQuantity.hasQuantity()) {
                    _event.setValue(DetailUiEvent.ShowCannotDecrease)
                    Quantity(1)
                } else {
                    decreasedCartQuantity
                }

            _uiState.value = state.copy(product = product.copy(cartQuantity = quantity))
        }
    }

    fun saveCart(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _uiState.value ?: return@launch
            val products = defaultCartRepository.loadSinglePage(null, null)
            products.onSuccess { value ->
                val savedCart = value.carts.find { it.productId == productId }
                if (savedCart != null) {
                    defaultCartRepository.updateQuantity(
                        savedCart.id,
                        state.addQuantity(savedCart.quantity)
                    )
                    sendEvent(DetailUiEvent.NavigateToCart(state.category))
                } else {
                    defaultCartRepository.addCart(
                        Cart(state.cartQuantity, productId)
                    )
                    sendEvent(DetailUiEvent.NavigateToCart(state.category))
                }
            }
        }
    }

    fun loadLastSeenProduct(lastSeenProductId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.saveHistory(lastSeenProductId)
            _event.postValue(
                DetailUiEvent.NavigateToLastSeenProduct(
                    lastSeenProductId
                )
            )
        }
    }

    private fun saveHistory(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.saveHistory(productId)
        }
    }

    private fun sendEvent(event: DetailUiEvent) {
        _event.postValue(event)
    }

    val cartQuantityEventHandler =
        object : CartQuantityHandler {
            override fun onClickIncrease(cartId: Long) {
                increaseCartQuantity()
            }

            override fun onClickDecrease(cartId: Long) {
                decreaseCartQuantity()
            }
        }
}
