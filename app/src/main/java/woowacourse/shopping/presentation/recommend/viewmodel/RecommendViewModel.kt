package woowacourse.shopping.presentation.recommend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.presentation.common.addToCartUseCase
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.recommend.model.RecommendUiState
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel

class RecommendViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val recentProductRepository: RecentProductRepository = RepositoryProvider.recentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private val _uiEvents = Channel<RecommendEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, _ ->
            viewModelScope.launch {
                _uiEvents.send(RecommendEvent.ShowError("알 수 없는 오류가 발생했습니다."))
            }
        }

    fun loadRecommendProducts() {
        viewModelScope.launch(exceptionHandler) {
            val recentProducts = recentProductRepository.getRecentProducts(1)
            if (recentProducts.isEmpty()) return@launch
            val inCartProductIds = cartRepository.getCart().items.map { it.product.id }
            val products = productRepository.getProducts(0, 100000)
            val sameCategoryProducts = products.filter { it.category == recentProducts[0].category }
            val recommendProducts =
                sameCategoryProducts
                    .filter { product ->
                        product.id !in inCartProductIds
                    }.take(10)

            _uiState.update {
                it.copy(
                    recommendProducts =
                        recommendProducts.map {
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
            addToCartUseCase(cartRepository, productId)

            val cartItem =
                cartRepository
                    .getCart()
                    .items
                    .find { it.product.id == productId } ?: return@launch

            _uiState.update {
                it.copy(
                    paymentItems = it.paymentItems.add(cartItem),
                )
            }
            updateRecommendQuantity(productId, cartItem.quantity)
        }
    }

    fun decrease(productId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val cartItem =
                cartRepository
                    .getCart()
                    .items
                    .find { it.product.id == productId } ?: return@launch

            cartRepository.changeCartItem(productId, cartItem.decrease().quantity)

            val updated = cartRepository.getCart().items.find { it.product.id == productId }
            val newPaymentItems =
                if (updated == null) {
                    uiState.value.paymentItems.remove(productId)
                } else {
                    uiState.value.paymentItems.add(updated)
                }

            _uiState.update {
                it.copy(paymentItems = newPaymentItems)
            }

            updateRecommendQuantity(productId, updated?.quantity ?: 0)
        }
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
}

sealed interface RecommendEvent {
    data class ShowError(
        val message: String,
    ) : RecommendEvent
}
