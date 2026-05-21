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
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
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
            val recentProducts = recentProductRepository.getRecentProducts(1)
            if (recentProducts.isEmpty()) return@launch
            val inCartProductIds = cartRepository.getCart().items.map { it.product.id }
            val sameCategoryProducts =
                productRepository.getProducts(
                    offset = 0,
                    limit = RECOMMEND_PRODUCT_SIZE,
                    category = recentProducts[0].category,
                )
            val recommend =
                sameCategoryProducts
                    .filter { product ->
                        product.id !in inCartProductIds
                    }.take(10)

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
        val product = recommendProducts.find { it.id == productId } ?: return
        val newPaymentItems = uiState.value.paymentItems.increase(product)
        _uiState.update {
            it.copy(
                paymentItems = newPaymentItems,
            )
        }
        updateRecommendQuantity(productId, newPaymentItems.quantityOf(productId))
    }

    fun decrease(productId: Long) {
        val newPaymentItems = uiState.value.paymentItems.decrease(productId)
        _uiState.update { it.copy(paymentItems = newPaymentItems) }
        updateRecommendQuantity(productId, newPaymentItems.quantityOf(productId))
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
