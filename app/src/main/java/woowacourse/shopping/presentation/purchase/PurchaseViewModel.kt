package woowacourse.shopping.presentation.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.datasource.map
import com.example.domain.datasource.onSuccess
import com.example.domain.repository.CartRepository
import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.launch
import woowacourse.shopping.presentation.cart.model.CouponUiModels

class PurchaseViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _couponUiModels: MutableLiveData<CouponUiModels> = MutableLiveData()
    val couponUiModels: MutableLiveData<CouponUiModels> = MutableLiveData()

    init {
        savedStateHandle.get<IntArray>(PurchaseActivity.CART_ITEMS_ID)?.toList()
            ?.let(::getTotalPrice)
    }

    fun getTotalPrice(cartItemIds: List<Int>) {
        viewModelScope.launch {
            val result =
                cartRepository.findAll().map { allCartItems ->
                    allCartItems.filter { cartItem ->
                        cartItemIds.contains(cartItem.id)
                    }.sumOf { it.quantity.count * it.product.price }
                }
            result.onSuccess {
                _totalPrice.value = it
            }
        }
    }

    fun createOrder() {
        /*
        val cartUiModels = cartUiState.value?.cartUiModels ?: return
        val cartItemIds = cartUiModels.filter { it.isSelected }.map { it.cartItemId }
        viewModelScope.launch {
            orderRepository.createOrder(cartItemIds)
        }
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
