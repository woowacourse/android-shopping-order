package woowacourse.shopping.presentation.recommend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.addToCartUseCase
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.recommendProductUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.recommend.model.RecommendUiState
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel

class RecommendViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            cartRepository.loadCart()
            productRepository.loadProducts(0, 20)
        }
    }

    private val _uiState = MutableStateFlow(RecommendUiState())
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(emptySet<Long>())

    val uiState =
        combine(cart, paymentItemIds, _uiState) { cart, paymentIds, state ->
            val payment = PaymentItems(cart.items.filter { it.product.id in paymentIds }.toSet())
            state.copy(
                totalQuantity = payment.totalQuantity,
                totalPrice = payment.totalPrice,
                recommendProducts =
                    state.recommendProducts.map { item ->
                        item.copy(
                            quantity = cart.items.find { it.product.id == item.product.id }?.quantity ?: 0,
                        )
                    },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RecommendUiState(),
        )

    fun loadRecommendProducts() {
        viewModelScope.launch {
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
    }

    fun initializePaymentItems(productIds: LongArray) {
        paymentItemIds.value = productIds.toSet()
    }

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
}
