package woowacourse.shopping.presentation.view.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.domain.Coupon

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    val totalPrice: LiveData<Int> =
        _cartItems.map { cartItems ->
            cartItems.sumOf { cartItem -> cartItem.totalPrice }
        }

    fun loadCoupons() {
        viewModelScope.launch {
            _coupons.value = couponRepository.loadCoupons()
        }
    }

    fun loadSelectedCartItems(ids: List<Long>) {
        viewModelScope.launch {
            val selectedCartItems =
                buildList {
                    ids.forEach { id ->
                        cartRepository.loadCartItemByProductId(id)?.let { cartItem ->
                            add(cartItem)
                        }
                    }
                }
            _cartItems.postValue(selectedCartItems)
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val cartRepository = RepositoryProvider.cartRepository
                    val couponRepository = RepositoryProvider.couponRepository
                    return CheckoutViewModel(
                        cartRepository,
                        couponRepository,
                    ) as T
                }
            }
    }
}
