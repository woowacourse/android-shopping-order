package woowacourse.shopping.presentation.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.payment.model.Coupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.cart.toUiModel

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int> get() = _price

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _cartItems: MutableLiveData<List<CartProductUi>> = MutableLiveData()
    val cartItems: LiveData<List<CartProductUi>> get() = _cartItems

    private val _orderPrice: MutableLiveData<Int> = MutableLiveData()
    val orderPrice: LiveData<Int> get() = _orderPrice

    private val _deliveryPrice: MutableLiveData<Int> = MutableLiveData(3000)
    val deliveryPrice: LiveData<Int> get() = _deliveryPrice

    private val _couponDiscountPrice: MutableLiveData<Int> = MutableLiveData(0)
    val couponDiscountPrice: LiveData<Int> get() = _couponDiscountPrice

    private val _finalPrice: MutableLiveData<Int> = MutableLiveData()
    val finalPrice: LiveData<Int> get() = _finalPrice

    init {
        loadCoupons()
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.totalCartProducts()
                .onSuccess {
                    _cartItems.value = it.map { cartProduct -> cartProduct.toUiModel() }
                    _orderPrice.value = _cartItems.value?.sumOf { it.totalPrice }
                    _finalPrice.value = _orderPrice.value
                }.onFailure {
                    Log.d("alsong", "장바구니상품 불러오기 실패")
                }
        }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.loadCoupons()
                .onSuccess {
                    _coupons.value = it.map { couponData -> Coupon.of(couponData) }
                }.onFailure {
                    Log.d("alsong", "쿠폰 불러오기 실패")
                }
        }
    }

    companion object {
        fun factory(
            cartRepository: CartRepository,
            couponRepository: CouponRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory { PaymentViewModel(cartRepository, couponRepository) }
        }
    }
}
