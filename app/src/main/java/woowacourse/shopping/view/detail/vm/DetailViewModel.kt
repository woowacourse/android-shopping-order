package woowacourse.shopping.view.detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        saveHistory(productId)

        defaultProductRepository.loadProduct(productId) { product ->
            product.onSuccess { productValue ->
                if (lastSeenProductId != NO_LAST_SEEN_PRODUCT && lastSeenProductId != productId) {
                    defaultProductRepository.loadProduct(lastSeenProductId) { lastSeenProduct ->
                        lastSeenProduct.onSuccess { lastSeenProductValue ->
                            _uiState.value =
                                DetailUiState(
                                    ProductState(
                                        item = productValue,
                                        cartQuantity = Quantity(1),
                                    ),
                                    lastSeenProduct = lastSeenProductValue,
                                )
                        }
                            .onFailure(::handleFailure)
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
            }
                .onFailure(::handleFailure)
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
                    _uiEvent.setValue(DetailUiEvent.ShowCannotDecrease)
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
                result.onSuccess { value ->
                    val savedCart = value.carts.find { it.productId == productId }

                    savedCart?.let {
                        defaultCartRepository.updateQuantity(
                            it.id,
                            state.addQuantity(it.quantity),
                        ) { result ->
                            result
                                .onSuccess {
                                    _uiEvent.setValue(DetailUiEvent.NavigateToCart(state.category))
                                }
                                .onFailure(::handleFailure)
                        }
                    } ?: run {
                        defaultCartRepository.addCart(
                            Cart(
                                state.cartQuantity,
                                productId,
                            ),
                        ) { result ->
                            result
                                .onSuccess {
                                    _uiEvent.setValue(DetailUiEvent.NavigateToCart(state.category))
                                }
                                .onFailure(::handleFailure)
                        }
                    }
                }
                    .onFailure(::handleFailure)
            }
        }
    }

    fun loadLastSeenProduct(lastSeenProductId: Long) {
        historyRepository.saveHistory(lastSeenProductId) {
            _uiEvent.postValue(DetailUiEvent.NavigateToLastSeenProduct(lastSeenProductId))
        }
    }

    private fun saveHistory(productId: Long) {
        historyRepository.saveHistory(productId) {}
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(DetailUiEvent.ShowErrorMessage(throwable))
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
