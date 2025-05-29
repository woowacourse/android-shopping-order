package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.DefaultCartRepository
import woowacourse.shopping.data.repository.DefaultProductRepository
import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.repository.HistoryRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.detail.DetailActivity.Companion.NO_LAST_SEEN_PRODUCT
import woowacourse.shopping.view.detail.DetailUiEvent
import woowacourse.shopping.view.main.state.ProductState

class DetailViewModel(
    private val defaultProductRepository: DefaultProductRepository,
    private val defaultCartRepository: DefaultCartRepository,
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
                                    _uiState.value =
                                        DetailUiState(
                                            ProductState(
                                                item = productValue,
                                                cartQuantity = Quantity(1),
                                            ),
                                            lastSeenProduct = lastSeenProductValue,
                                        )
                                },
                                onFailure = {},
                            )
                        }
                    } else {
                        _uiState.postValue(
                            DetailUiState(
                                product =
                                    ProductState(
                                        item = productValue,
                                        cartQuantity = Quantity(1),
                                    ),
                                lastSeenProduct = null,
                            ),
                        )
                    }
                },
                onFailure = {},
            )
        }
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
        withState(_uiState.value) { state ->
            defaultCartRepository.loadSinglePage(null, null) { result ->
                result.fold(
                    onSuccess = { value ->
                        val savedCart = value.carts.find { it.product.id == productId }

                        savedCart?.let {
                            defaultCartRepository.updateQuantity(
                                it.id,
                                state.addQuantity(it.quantity),
                            ) { result ->
                                result.fold(
                                    onSuccess = { sendEvent(DetailUiEvent.MoveToCart) },
                                    onFailure = {},
                                )
                            }
                        } ?: run {
                            defaultCartRepository.addCart(
                                Cart(
                                    state.cartQuantity,
                                    productId,
                                ),
                            ) { result ->
                                result.fold(
                                    onSuccess = { sendEvent(DetailUiEvent.MoveToCart) },
                                    onFailure = {},
                                )
                            }
                        }
                    },
                    onFailure = {},
                )
            }
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
