package woowacourse.shopping.presentation.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.payment.model.Coupon
import woowacourse.shopping.data.payment.model.CouponData
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.cart.CartProductUi
import woowacourse.shopping.presentation.cart.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int> get() = _price

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _couponsData: MutableLiveData<List<CouponData>> = MutableLiveData()
    val couponsData: LiveData<List<CouponData>> get() = _couponsData

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

    private val _checkedCoupon: MutableLiveData<CouponData> = MutableLiveData()
    val checkedCoupon: LiveData<CouponData> get() = _checkedCoupon

    private val _checkedState: MutableLiveData<CheckBoxState> = MutableLiveData()
    val checkedState: LiveData<CheckBoxState> get() = _checkedState

    private val _finishOrderEvent = MutableSingleLiveData<Unit>()
    val finishOrderEvent: SingleLiveData<Unit> get() = _finishOrderEvent

    init {
        loadCoupons()
        loadCartItems()
    }

    fun pay() {
        viewModelScope.launch {
            cartRepository.orderCartProducts(_cartItems.value?.map { it.product.id } ?: emptyList())
                .onSuccess {
                    _finishOrderEvent.setValue(Unit)
                }
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.totalCartProducts()
                .onSuccess {
                    _cartItems.value = it.map { cartProduct -> cartProduct.toUiModel() }
                    _orderPrice.value = _cartItems.value?.sumOf { it.totalPrice }
                    _finalPrice.value = (_orderPrice.value ?: 0) + (_deliveryPrice.value ?: 0)
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
                    _couponsData.value = it
                }.onFailure {
                    Log.d("alsong", "쿠폰 불러오기 실패")
                }
        }
    }

    fun toggleCouponCheckBox(coupon: Coupon) {
        val coupons: MutableList<Coupon> = _coupons.value?.toMutableList() ?: return
        coupons.forEachIndexed { index, couponOfList ->
            if (couponOfList.name == coupon.name) {
                coupons[index] = coupons[index].copy(isSelected = true)
                val couponsData = _couponsData.value ?: return
                updatePrices(couponsData[index])
            } else {
                coupons[index] = coupons[index].copy(isSelected = false)
            }
        }
        _coupons.value = coupons
    }

    private fun updatePrices(selectedCoupon: CouponData) {
        _deliveryPrice.value = 3000
        _couponDiscountPrice.value = 0
        when (selectedCoupon) {
            is CouponData.Fixed5000 -> {
                if ((orderPrice.value ?: 0) >= 100000) {
                    _couponDiscountPrice.value = -5000
                }
            }

            is CouponData.Bogo -> {
                val cartItems = _cartItems.value ?: return
                if (cartItems.any { it.count >= 3 }) {
                    var mostExpensiveItemIndex = 0
                    cartItems.forEachIndexed { index, item ->
                        if (item.count >= 3) {
                            if (cartItems[mostExpensiveItemIndex].product.price <= cartItems[index].product.price) {
                                mostExpensiveItemIndex = index
                            }
                        }
                    }
                    _couponDiscountPrice.value = -cartItems[mostExpensiveItemIndex].product.price
                }
            }

            is CouponData.Freeshipping -> {
                if ((orderPrice.value ?: 0) >= 50000) {
                    _deliveryPrice.value = 0
                }
            }

            is CouponData.Miraclesale -> {
                val orderPrice = _orderPrice.value ?: 0
                _couponDiscountPrice.value = -(orderPrice * 0.3).toInt()
            }
        }
        _finalPrice.value =
            (_orderPrice.value ?: 0) + (_deliveryPrice.value ?: 0) + (
                _couponDiscountPrice.value
                    ?: 0
            )
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
