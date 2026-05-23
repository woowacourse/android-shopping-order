package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentItems

data class OrderUiState(
    val totalPrice: Long = 0L,
    val couponDiscountPrice: Long = 0L,
    val deliveryFee: Long = 0L,
    val finalPrice: Long = 0L,
)

class OrderViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val orderRepository: OrderRepository = RepositoryProvider.orderRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<OrderUiState> = MutableStateFlow(OrderUiState())
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(emptySet<Long>())
    val uiState =
        combine(_uiState, paymentItemIds, cart) { state, ids, cart ->
            val paymentItems = PaymentItems(cart.items.filter { it.product.id in ids }.toSet())
            state.copy(
                totalPrice = paymentItems.totalPrice,
                couponDiscountPrice = 0L,
                deliveryFee = 0L,
                finalPrice = paymentItems.totalPrice,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value,
        )

    init {
        viewModelScope.launch {
            cartRepository.loadCart()
            orderRepository.getCoupons()
        }
    }

    fun initializePaymentItems(productIds: List<Long>) {
        paymentItemIds.value = productIds.toSet()
    }
}
