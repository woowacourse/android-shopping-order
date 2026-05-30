package woowacourse.shopping.presentation.recommend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppModule
import woowacourse.shopping.di.AppModule.cartRepository
import woowacourse.shopping.domain.model.AddItemResult
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateItemResult
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.AddToCartUseCase
import woowacourse.shopping.domain.usecase.GetRecommendProductsUseCase
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.recommend.model.RecommendUiState
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel

class RecommendViewModel(
    private val addToCartUseCase: AddToCartUseCase = AppModule.addToCartUseCase,
    private val getRecommendProductsUseCase: GetRecommendProductsUseCase = AppModule.getRecommendProductsUseCase,
    private val cartRepository: CartRepository = AppModule.cartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<RecommendEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, _ ->
            viewModelScope.launch {
                _uiEvents.emit(RecommendEvent.ShowError("알 수 없는 오류가 발생했습니다."))
            }
        }

    private var recommendProducts: List<Product> = emptyList()

    fun loadRecommendProducts() {
        viewModelScope.launch(exceptionHandler) {
            val recommend = getRecommendProductsUseCase()
            recommendProducts = recommend
            _uiState.update {
                it.copy(
                    recommendProducts =
                        recommend.map {
                            ShoppingItemUiModel(
                                product = it.toUiModel(),
                                quantity = 0,
                            )
                        },
                )
            }
        }
    }

    fun loadPaymentId(productIds: LongArray) {
        viewModelScope.launch(exceptionHandler) {
            val cart = cartRepository.getCart()
            val newPaymentItems =
                PaymentItems(
                    cart.items
                        .filter { cartItem ->
                            productIds.contains(cartItem.product.id)
                        }.toSet(),
                )

            _uiState.update {
                it.copy(paymentItems = newPaymentItems)
            }
        }
    }

    fun increase(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            when (val result = addToCartUseCase(productId)) {
                is AddItemResult.NewAdded -> applyCart(result.cart, productId)
                is AddItemResult.Incremented -> applyCart(result.cart, productId)
                is AddItemResult.Error -> _uiEvents.emit(RecommendEvent.ShowError(result.message))
            }
        }
    }

    fun decrease(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val current = uiState.value.paymentItems.quantityOf(productId)
            val newQuantity = current - 1

            val result =
                if (newQuantity <= 0) {
                    cartRepository.deleteItem(productId)
                    cartRepository.getCart()
                } else {
                    when (val r = cartRepository.changeCartItem(productId, newQuantity)) {
                        is UpdateItemResult.Success -> r.cart
                        is UpdateItemResult.Error -> {
                            _uiEvents.emit(RecommendEvent.ShowError(r.message))
                            return@launch
                        }
                    }
                }

            val newPaymentItems =
                PaymentItems(
                    result.items.filter { uiState.value.paymentItems.isContain(it.product.id) }.toSet(),
                )
            _uiState.update { it.copy(paymentItems = newPaymentItems) }
            updateRecommendQuantity(productId, newPaymentItems.quantityOf(productId))
        }
    }

    private fun applyCart(
        cart: Cart,
        productId: Long,
    ) {
        val item = cart.items.find { it.product.id == productId } ?: return
        val newPaymentItems = uiState.value.paymentItems.add(item)
        _uiState.update { it.copy(paymentItems = newPaymentItems) }
        updateRecommendQuantity(productId, item.quantity)
    }

    private fun updateRecommendQuantity(
        productId: Long,
        quantity: Int,
    ) {
        _uiState.update { state ->
            state.copy(
                recommendProducts =
                    state.recommendProducts.map { item ->
                        if (item.product.id == productId) {
                            item.copy(quantity = quantity)
                        } else {
                            item
                        }
                    },
            )
        }
    }

    companion object {
        private const val RECOMMEND_PRODUCT_SIZE = 20
    }
}

sealed interface RecommendEvent {
    data class ShowError(
        val message: String,
    ) : RecommendEvent
}
