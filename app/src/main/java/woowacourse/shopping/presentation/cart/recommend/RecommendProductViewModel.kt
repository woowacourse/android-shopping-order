package woowacourse.shopping.presentation.cart.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.RecommendProductsUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartItemListener
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.shopping.toCartUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class RecommendProductViewModel(
    orders: List<CartProductUi>,
    private val cartRepository: CartRepository,
    recommendProductsUseCase: RecommendProductsUseCase,
) : ViewModel(), CartItemListener {
    private val _uiState = MutableLiveData(RecommendOrderUiState(orders))
    val uiState: LiveData<RecommendOrderUiState> get() = _uiState
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _showOrderDialogEvent = MutableSingleLiveData<Unit>()
    val showOrderDialogEvent: SingleLiveData<Unit> get() = _showOrderDialogEvent
    private val _finishOrderEvent = MutableSingleLiveData<Unit>()
    val finishOrderEvent: SingleLiveData<Unit> get() = _finishOrderEvent
    private val _errorEvent = MutableSingleLiveData<RecommendProductErrorEvent>()
    val errorEvent: SingleLiveData<RecommendProductErrorEvent> get() = _errorEvent

    init {
        val uiState = _uiState.value
        val recommendProducts = recommendProductsUseCase().map { it.toCartUiModel(initCount = 0) }
        if (uiState != null) {
            _uiState.value = uiState.copy(recommendProducts = recommendProducts)
        } else {
            _uiState.value = RecommendOrderUiState(orders, recommendProducts)
        }
    }

    fun startOrder() {
        _showOrderDialogEvent.setValue(Unit)
    }

    fun orderProducts() {
        val uiState = _uiState.value ?: return
        cartRepository.orderCartProducts(uiState.totalOrderIds)
            .onSuccess {
                _updateCartEvent.setValue(Unit)
                _finishOrderEvent.setValue(Unit)
            }.onFailure {
                _errorEvent.setValue(RecommendProductErrorEvent.OrderProducts)
            }
    }

    override fun increaseProductCount(id: Long) {
        val uiState = _uiState.value ?: return
        val product = uiState.findProduct(id) ?: return
        cartRepository.updateCartProduct(id, product.count + INCREMENT_AMOUNT)
            .onSuccess {
                _uiState.value =
                    uiState.increaseProductCount(id, INCREMENT_AMOUNT)
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                _errorEvent.setValue(RecommendProductErrorEvent.IncreaseCartProduct)
            }
    }

    override fun decreaseProductCount(id: Long) {
        val uiState = _uiState.value ?: return
        if (uiState.shouldDeleteFromCart(id)) {
            cartRepository.deleteCartProduct(id).onSuccess {
                _uiState.value =
                    uiState.decreaseProductCount(
                        id,
                        INCREMENT_AMOUNT,
                    )
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                _errorEvent.setValue(RecommendProductErrorEvent.DeleteCartProduct)
            }
            return
        }
        val product = uiState.findProduct(id) ?: return _updateCartEvent.setValue(Unit)
        cartRepository.updateCartProduct(id, product.count - INCREMENT_AMOUNT)
            .onSuccess {
                _uiState.value =
                    uiState.decreaseProductCount(id, INCREMENT_AMOUNT)
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                _errorEvent.setValue(RecommendProductErrorEvent.DecreaseCartProduct)
            }
    }

    companion object {
        private const val INCREMENT_AMOUNT = 1

        fun factory(
            orders: List<CartProductUi>,
            cartRepository: CartRepository,
            recommendProductsUseCase: RecommendProductsUseCase,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                RecommendProductViewModel(
                    orders,
                    cartRepository,
                    recommendProductsUseCase,
                )
            }
        }
    }
}

private fun RecommendOrderUiState.increaseProductCount(
    productId: Long,
    amount: Int,
): RecommendOrderUiState =
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

private fun RecommendOrderUiState.decreaseProductCount(
    productId: Long,
    amount: Int,
): RecommendOrderUiState {
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

private fun RecommendOrderUiState.shouldDeleteFromCart(productId: Long): Boolean {
    val product = findProduct(productId) ?: return false
    return product.count <= 1
}

private fun RecommendOrderUiState.findProduct(productId: Long): CartProductUi? = recommendProducts.find { it.product.id == productId }
