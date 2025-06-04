package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.exception.NetworkError
import woowacourse.shopping.domain.exception.onFailure
import woowacourse.shopping.domain.exception.onSuccess
import woowacourse.shopping.domain.product.Product
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
import kotlin.onFailure

class DetailViewModel(
    private val defaultProductRepository: ProductRepository,
    private val defaultCartRepository: CartRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<DetailUiState>()
    val uiState: LiveData<DetailUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<DetailUiEvent>()
    val uiEvent: SingleLiveData<DetailUiEvent> get() = _uiEvent

    fun load(
        productId: Long,
        lastSeenProductId: Long,
    ) {
        viewModelScope.launch {
            defaultProductRepository.loadProduct(productId)
                .onSuccess { initializeUiState(productId, lastSeenProductId, it) }
                .onFailure(::handleFailure)

            historyRepository.saveHistory(productId)
        }
    }

    private fun initializeUiState(
        productId: Long,
        lastSeenProductId: Long,
        product: Product,
    ) {
        if (lastSeenProductId != NO_LAST_SEEN_PRODUCT && lastSeenProductId != productId) {
            loadLastSeenProduct(lastSeenProductId, product)
        } else {
            _uiState.value = DetailUiState(ProductState(item = product, cartQuantity = Quantity(1)))
        }
    }

    private fun loadLastSeenProduct(
        lastSeenProductId: Long,
        product: Product,
    ) {
        viewModelScope.launch {
            defaultProductRepository.loadProduct(lastSeenProductId)
                .onSuccess { lastSeenProduct ->
                    _uiState.value =
                        DetailUiState(
                            ProductState(item = product, cartQuantity = Quantity(1)),
                            lastSeenProduct,
                        )
                }
                .onFailure(::handleFailure)
        }
    }

    fun increaseCartQuantity() =
        withState(_uiState.value) { state ->
            _uiState.value = state.increaseQuantity()
        }

    fun decreaseCartQuantity() =
        withState(_uiState.value) { state ->
            val (newState, event) = state.decreaseQuantity()

            _uiState.value = newState
            event?.let(_uiEvent::setValue)
        }

    fun saveCart(productId: Long) {
        viewModelScope.launch {
            defaultCartRepository.loadSinglePage(null, null)
                .onSuccess { value ->
                    value.isSavedInCart(productId)
                        ?.let { whenProductSavedInCart(it) }
                        ?: run { whenProductNotSavedInCart(productId) }
                }
                .onFailure(::handleFailure)
        }
    }

    private fun whenProductSavedInCart(cart: ShoppingCart) =
        withState(_uiState.value) { state ->
            viewModelScope.launch {
                defaultCartRepository.updateQuantity(
                    cart.id,
                    state.addQuantity(cart.quantity),
                )
                    .onSuccess {
                        _uiEvent.setValue(DetailUiEvent.NavigateToCart(state.category))
                    }
                    .onFailure(::handleFailure)
            }
        }

    private fun whenProductNotSavedInCart(productId: Long) =
        withState(_uiState.value) { state ->
            val cart = Cart(state.cartQuantity, productId)
            viewModelScope.launch {
                defaultCartRepository.addCart(cart)
                    .onSuccess {
                        _uiEvent.setValue(DetailUiEvent.NavigateToCart(state.category))
                    }
                    .onFailure(::handleFailure)
            }
        }

    fun saveLastSeenProduct(lastSeenProductId: Long) {
        viewModelScope.launch {
            historyRepository.saveHistory(lastSeenProductId)
        }
    }

    private fun handleFailure(throwable: NetworkError) {
        _uiEvent.setValue(DetailUiEvent.ShowErrorMessage(throwable))
    }

    val cartQuantityEventHandler =
        object : CartQuantityHandler {
            override fun onClickIncrease(cartId: Long) = increaseCartQuantity()

            override fun onClickDecrease(cartId: Long) = decreaseCartQuantity()
        }
}
