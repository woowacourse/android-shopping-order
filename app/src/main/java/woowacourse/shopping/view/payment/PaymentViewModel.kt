package woowacourse.shopping.view.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentDetail
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.payment.adapter.CouponItem
import woowacourse.shopping.view.util.Error
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private var selectedProducts: List<CartProduct> = listOf()

    private val _paymentDetail = MutableLiveData<PaymentDetail>()
    val paymentDetail: LiveData<PaymentDetail> get() = _paymentDetail

    private val _coupons = MutableLiveData<List<CouponItem>>()
    val coupons: LiveData<List<CouponItem>> get() = _coupons

    private val _onFinishOrder = MutableSingleLiveData<Unit>()
    val onFinishOrder: SingleLiveData<Unit> get() = _onFinishOrder

    private val _onError = MutableSingleLiveData<Error>()
    val onError: SingleLiveData<Error> get() = _onError

    private var selectedCouponId: Int? = null

    fun initSelectedProducts(selectedCartProducts: List<CartProduct>) {
        selectedProducts = selectedCartProducts
        initPaymentDetail(selectedCartProducts)
        initCoupon()
    }

    private fun initCoupon() {
        viewModelScope.launch {
            val result = couponRepository.getCoupons()
            result.onSuccess { coupons ->
                _coupons.value =
                    coupons.map { CouponItem(it) }
                        .filter { it.coupon.isValid(selectedProducts) }
            }.onFailure {
                Log.e("error", it.message.toString())
                _onError.setValue(Error.FailToLoadCoupon)
            }
        }
    }

    private fun initPaymentDetail(selectedCartProducts: List<CartProduct>) {
        _paymentDetail.value = PaymentDetail(selectedCartProducts)
    }

    fun selectCoupon(coupon: Coupon) {
        val oldSelectedId = selectedCouponId
        val newlySelectedId = coupon.id

        selectedCouponId = if (newlySelectedId == oldSelectedId) null else newlySelectedId

        _coupons.value =
            _coupons.value?.map { item ->
                when (item.coupon.id) {
                    newlySelectedId -> item.copy(isSelected = newlySelectedId != oldSelectedId)
                    oldSelectedId -> item.copy(isSelected = false)
                    else -> item
                }
            }

        updatePaymentDetail(coupon)
    }

    private fun updatePaymentDetail(coupon: Coupon) {
        if (selectedCouponId != null) {
            _paymentDetail.value = _paymentDetail.value?.discountByCoupon(coupon)
        } else {
            initPaymentDetail(selectedProducts)
        }
    }

    fun submitOrder() {
        viewModelScope.launch {
            val result = orderRepository.submitOrder(selectedProducts.map { it.id })

            result
                .onFailure {
                    Log.e("error", it.message.toString())
                    _onError.setValue(Error.FailToOrder)
                }

            _onFinishOrder.setValue(Unit)
        }
    }
}
