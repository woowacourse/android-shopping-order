package woowacourse.shopping.presentation.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.navigation.OrderItem
import woowacourse.shopping.presentation.payment.model.PaymentUiState
import woowacourse.shopping.presentation.payment.model.toUiModel
import java.time.LocalDateTime

class PaymentViewModel(
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val couponRepository: CouponRepository = RepositoryProvider.couponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaymentEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun loadAvailableCoupons(orderItems: List<OrderItem>) {
        viewModelScope.launch {
            runCatching {
                val paymentItems = buildPaymentItems(orderItems)
                val now = LocalDateTime.now()
                couponRepository
                    .getCoupons()
                    .filter { it.isApplicable(paymentItems, now) }
                    .map { it.toUiModel() }
            }.onSuccess { coupons ->
                _uiState.update { it.copy(availableCoupons = coupons) }
            }.onFailure { error ->
                println(error)
                _uiEvents.emit(PaymentEvent.ShowError("쿠폰을 불러오지 못했습니다."))
            }
        }
    }

    private suspend fun buildPaymentItems(items: List<OrderItem>): PaymentItems =
        coroutineScope {
            val cartItems =
                items
                    .map { item ->
                        async { CartItem(productRepository.getProductById(item.productId), item.quantity) }
                    }.awaitAll()
            PaymentItems(cartItems.toSet())
        }
}

sealed interface PaymentEvent {
    data class ShowError(
        val message: String,
    ) : PaymentEvent
}
