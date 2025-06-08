package woowacourse.shopping.presentation.view.checkout

import android.util.Log
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
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.presentation.view.checkout.adapter.CouponUiModel
import woowacourse.shopping.presentation.view.checkout.adapter.toUiModel
import java.time.LocalDateTime

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    val totalPrice: LiveData<Int> =
        _cartItems.map { cartItems ->
            cartItems.sumOf { cartItem -> cartItem.totalPrice }
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
            couponRepository.loadCoupons().forEach {
                val isAvailable = it.isAvailable(_cartItems.value.orEmpty(), LocalDateTime.now())
                val discount = if (isAvailable) it.discount(_cartItems.value.orEmpty()) else -1
                Log.d("Coupon", "${it.info.description}: $isAvailable, $discount")
                it.isAvailable(_cartItems.value.orEmpty(), LocalDateTime.now())
            }
        }
    }

    fun loadCoupons() {
        viewModelScope.launch {
            val availableCoupons =
                couponRepository.loadCoupons()
                    .filter { coupon ->
                        coupon.isAvailable(_cartItems.value.orEmpty(), LocalDateTime.now())
                    }
                    .map(Coupon::toUiModel)
            _coupons.value = availableCoupons
        }
    }

    fun setCouponSelection(
        target: CouponUiModel,
        isSelected: Boolean,
    ) {
        _coupons.value =
            _coupons.value?.map { coupon ->
                if (coupon.coupon.info.id == target.coupon.info.id) {
                    coupon.copy(isSelected = isSelected)
                } else {
                    coupon.copy(isSelected = false)
                }
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
