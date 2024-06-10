package woowacourse.shopping.presentation.purchase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.domain.repository.CartRepository
import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository

class PurchaseViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    fun createOrder() {
        /*
        val cartUiModels = cartUiState.value?.cartUiModels ?: return
        val cartItemIds = cartUiModels.filter { it.isSelected }.map { it.cartItemId }
        viewModelScope.launch {
            orderRepository.createOrder(cartItemIds)
        }

         */
        /*
        orderRepository.createOrder(
            cartItemIds,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    _isSuccessCreateOrder.value = Event(true)
                    deleteCartItemIds(cartItemIds)
                }

                override fun onFailure(t: Throwable) {
                    _isSuccessCreateOrder.value = Event(false)
                }
            },
        )
         */
    }
}
