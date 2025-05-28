package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.detail.DetailActivity.Companion.NO_LAST_SEEN_PRODUCT
import woowacourse.shopping.view.detail.DetailUiEvent
import woowacourse.shopping.view.main.state.IncreaseState
import woowacourse.shopping.view.main.state.ProductState

class DetailViewModel(
    private val defaultProductRepository: DefaultProductRepository,
    private val defaultCartRepository: DefaultCartRepository,
    private val cartRepository: CartRepository,
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
        defaultProductRepository.loadProduct(productId) { product ->
            product.fold(
                onSuccess = { productValue ->
                    if (lastSeenProductId != NO_LAST_SEEN_PRODUCT && lastSeenProductId != productId) {
                        defaultProductRepository.loadProduct(lastSeenProductId) { lastSeenProduct ->
                            lastSeenProduct.fold(
                                onSuccess = { lastSeenProductValue ->
                                    _uiState.value = DetailUiState(
                                        ProductState(item = productValue, cartQuantity = Quantity(1)),
                                        lastSeenProduct = lastSeenProductValue
                                    )
                                },
                                onFailure = {},
                            )
                        }
                    } else {
                        _uiState.postValue( DetailUiState(
                            product = ProductState(item = productValue, cartQuantity = Quantity(1)),
                            lastSeenProduct = null,
                        ),
                        )
                    }
                },
                onFailure = {}
            )
        }
    }

    fun increaseCartQuantity() {
        withState(_uiState.value) { state ->
            when (val result = state.product.increaseCartQuantity()) {
                is IncreaseState.CanIncrease -> {
                    _uiState.value = state.copy(product = result.value)
                }

                is IncreaseState.CannotIncrease -> {
                    sendEvent(DetailUiEvent.ShowCannotIncrease(result.quantity))
                }
            }
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
        withState(_uiState.value) { state ->
            cartRepository.getCart(productId) { cart ->
                cartRepository.upsert(productId, state.product.cartQuantity)
            }
            sendEvent(DetailUiEvent.MoveToCart)
        }
    }

    fun loadLastSeenProduct(lastSeenProductId: Long) {
        historyRepository.saveHistory(lastSeenProductId) {
            _event.postValue(DetailUiEvent.MoveToLastSeenProduct(lastSeenProductId))
        }
    }

    private fun sendEvent(event: DetailUiEvent) {
        _event.setValue(event)
    }
}
