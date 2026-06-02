package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.addToCartUseCase
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.recommendProductUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.productlist.model.ShoppingItemUiModel
import woowacourse.shopping.presentation.recommend.model.RecommendUiState
import woowacourse.shopping.route.RecommendItem

class RecommendItemViewModel(
    productIds: List<Long>,
    private val cartRepository: CartRepository = AppContainer.cartRepository,
    private val productRepository: ProductRepository = AppContainer.productRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState())
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(productIds.toSet())

    init {
        viewModelScope.launch {
            cartRepository.loadCart()
            productRepository.loadProducts(0, 20)
            loadRecommendProducts()
        }
    }

    val uiState =
        combine(cart, paymentItemIds, _uiState) { cart, paymentIds, state ->
            val payment = PaymentItems(cart.items.filter { it.product.id in paymentIds }.toSet())
            state.copy(
                totalQuantity = payment.totalQuantity,
                totalPrice = payment.totalPrice,
                recommendProducts =
                    state.recommendProducts.map { item ->
                        item.copy(
                            quantity =
                                cart.items.find { it.product.id == item.product.id }?.quantity
                                    ?: 0,
                        )
                    },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RecommendUiState(),
        )

    private suspend fun loadRecommendProducts() {
        val recommendProducts = recommendProductUseCase(productRepository, cartRepository)
        _uiState.update {
            it.copy(
                recommendProducts =
                    recommendProducts.map { product ->
                        ShoppingItemUiModel(
                            product = product.toUiModel(),
                            quantity = 0,
                        )
                    },
            )
        }
    }

    fun getPaymentItemIds(): List<Long> = paymentItemIds.value.toList()

    fun addItemToCart(productId: Long) {
        viewModelScope.launch {
            addToCartUseCase(cartRepository, productId)
            paymentItemIds.update { it + productId }
        }
    }

    fun removeItemFromCart(productId: Long) {
        viewModelScope.launch {
            val cartItem = cart.value.items.find { it.product.id == productId } ?: return@launch
            if (cartItem.quantity == 1) {
                cartRepository.deleteItem(productId)
                paymentItemIds.update { it - productId }
            } else {
                cartRepository.changeCartItem(productId, cartItem.decrease().quantity)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    RecommendItemViewModel(
                        productIds = savedStateHandle.toRoute<RecommendItem>().productIds,
                    )
                }
            }
    }
}
