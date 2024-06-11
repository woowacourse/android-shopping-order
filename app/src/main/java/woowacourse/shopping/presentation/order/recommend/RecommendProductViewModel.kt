package woowacourse.shopping.presentation.order.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import woowacourse.shopping.domain.usecase.cart.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.cart.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.product.RecommendProductsUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartItemListener
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.shopping.toCartUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class RecommendProductViewModel(
    orders: List<CartProductUi>,
    private val decreaseCartProductUseCase: DecreaseCartProductUseCase,
    private val increaseCartProductCountUseCase: IncreaseCartProductUseCase,
    recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel(), CartItemListener {
    private val _uiState = MutableLiveData(RecommendProductUiState(orders))
    val uiState: LiveData<RecommendProductUiState> get() = _uiState
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _navigateToPaymentEvent = MutableSingleLiveData<List<CartProductUi>>()
    val navigateToPaymentEvent: SingleLiveData<List<CartProductUi>> get() = _navigateToPaymentEvent
    private val _errorEvent = MutableSingleLiveData<RecommendProductErrorEvent>()
    val errorEvent: SingleLiveData<RecommendProductErrorEvent> get() = _errorEvent

    init {
        viewModelScope.launch {
            val recommendProducts =
                recommendProductsUseCase(RECOMMEND_PRODUCT_SIZE).map { it.toCartUiModel(initCount = 0) }
            val uiState = _uiState.value
            if (uiState != null) {
                _uiState.value = uiState.copy(recommendProducts = recommendProducts)
            } else {
                _uiState.value = RecommendProductUiState(orders, recommendProducts)
            }
        }
    }

    fun navigateToPayment() {
        val uiState = _uiState.value ?: return
        _navigateToPaymentEvent.setValue(uiState.totalProducts)
    }

    override fun increaseProductCount(id: Long) {
        viewModelScope.launch {
            increaseCartProductCountUseCase(id, INCREMENT_AMOUNT)
                .onSuccess {
                    val uiState = _uiState.value ?: return@launch
                    _uiState.value = uiState.increaseProductCount(id, INCREMENT_AMOUNT)
                    _updateCartEvent.setValue(Unit)
                }.onFailure {
                    Timber.e(it)
                    _errorEvent.setValue(RecommendProductErrorEvent.IncreaseCartProduct)
                }
        }
    }

    override fun decreaseProductCount(id: Long) {
        viewModelScope.launch {
            decreaseCartProductUseCase(id).onSuccess {
                val uiState = _uiState.value ?: return@launch
                _uiState.value =
                    uiState.decreaseProductCount(
                        id,
                        INCREMENT_AMOUNT,
                    )
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(RecommendProductErrorEvent.DecreaseCartProduct)
            }
        }
    }

    companion object {
        private const val INCREMENT_AMOUNT = 1
        private const val RECOMMEND_PRODUCT_SIZE = 10

        fun factory(
            orders: List<CartProductUi>,
            decreaseCartProductUseCase: DecreaseCartProductUseCase,
            increaseCartProductCountUseCase: IncreaseCartProductUseCase,
            recommendProductsUseCase: RecommendProductsUseCase,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                RecommendProductViewModel(
                    orders,
                    decreaseCartProductUseCase,
                    increaseCartProductCountUseCase,
                    recommendProductsUseCase,
                )
            }
        }
    }
}

private fun RecommendProductUiState.increaseProductCount(
    productId: Long,
    amount: Int,
): RecommendProductUiState =
    copy(
        recommendProducts =
            recommendProducts.map {
                if (it.product.id == productId) {
                    it.copy(count = it.count + amount)
                } else {
                    it
                }
            },
    )

private fun RecommendProductUiState.decreaseProductCount(
    productId: Long,
    amount: Int,
): RecommendProductUiState {
    val newProducts =
        recommendProducts.map {
            if (it.product.id == productId) {
                it.copy(count = it.count - amount)
            } else {
                it
            }
        }
    return copy(recommendProducts = newProducts)
}
