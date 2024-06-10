package woowacourse.shopping.presentation.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.event.SingleLiveEvent
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.SharedChangedIdsDB
import java.time.LocalDateTime

class PaymentViewModel(private val orderRepository: OrderRepository) : ViewModel(),
    PaymentEventHandler, CouponEventHandler {
    private val order = OrderDatabase.getOrder()
    private lateinit var availableCoupons: List<Coupon>
    private var selectedCoupon: Coupon? = null

    private val _availableCouponsUiState: MutableLiveData<UIState<List<CouponUiModel>>> =
        MutableLiveData()
    val availableCouponsUiState: LiveData<UIState<List<CouponUiModel>>>
        get() = _availableCouponsUiState

    val orderAmount: Int = order.getTotalPrice().toInt()

    private val _couponDiscountAmount: MutableLiveData<Int> = MutableLiveData(0)
    val couponDiscountAmount: LiveData<Int>
        get() = _couponDiscountAmount

    val shippingFee: Int = Order.DEFAULT_SHIPPING_FEE

    private val _totalPaymentAmount: MutableLiveData<Int> = MutableLiveData(0)
    val totalPaymentAmount: LiveData<Int>
        get() = _totalPaymentAmount

    private val _moveBack = SingleLiveEvent<Boolean>()
    val moveBack: LiveData<Boolean>
        get() = _moveBack

    private val _isOrderSuccess = SingleLiveEvent<Boolean>()
    val isOrderSuccess: LiveData<Boolean>
        get() = _isOrderSuccess

    private val _toastMessage = SingleLiveEvent<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _changedCouponIds: MutableLiveData<Set<Long>> = MutableLiveData()
    val changedCouponIds: LiveData<Set<Long>>
        get() = _changedCouponIds

    fun getCoupons() {
        val currentDateTime = LocalDateTime.now()
        val availableCoupons = mutableListOf<Coupon>()
        viewModelScope.launch {
            val result = orderRepository.fetchCouponList()
            result.onSuccess { coupons ->
                coupons.forEach {
                    if (it.checkAvailability(order, currentDateTime)) {
                        availableCoupons.add(it)
                    }
                }
            }.onFailure {
                _availableCouponsUiState.value = UIState.Error(it)
                Log.d(this::class.java.simpleName, "$it")
            }
            this@PaymentViewModel.availableCoupons = availableCoupons
            changeCouponsToUiModel()
            updatePaymentInfo()
        }
    }

    private fun changeCouponsToUiModel() {
        if (availableCoupons.isEmpty()) {
            _availableCouponsUiState.value = UIState.Empty
        } else {
            val uiCoupons = availableCoupons.map { it.toUiModel() }
            _availableCouponsUiState.value = UIState.Success(uiCoupons)
        }
    }

    override fun onCouponClick(coupon: CouponUiModel) {
        if (coupon.isChecked) {
            unselectCoupon(coupon)
        } else {
            selectCoupon(coupon)
        }
        updatePaymentInfo()
    }

    private fun unselectCoupon(coupon: CouponUiModel) {
        selectedCoupon = null
        uncheckSelectedUiCoupon(coupon)
    }

    private fun uncheckSelectedUiCoupon(coupon: CouponUiModel) {
        val uiCoupons = (availableCouponsUiState.value as UIState.Success<List<CouponUiModel>>).data
        val updatedUiCoupons = uiCoupons.map {
            if (it.id == coupon.id) {
                it.copy(isChecked = false)
            } else {
                it
            }
        }
        _availableCouponsUiState.value = UIState.Success(updatedUiCoupons)
        _changedCouponIds.value = setOf(coupon.id)
    }

    private fun selectCoupon(coupon: CouponUiModel) {
        selectedCoupon = availableCoupons.find { it.id == coupon.id }
        checkSelectedUiCouponAndUncheckAll(coupon)
    }

    private fun checkSelectedUiCouponAndUncheckAll(coupon: CouponUiModel) {
        val uiCoupons = (availableCouponsUiState.value as UIState.Success<List<CouponUiModel>>).data
        val updatedUiCoupons = uiCoupons.map {
            if (it.id == coupon.id) {
                it.copy(isChecked = true)
            } else {
                it.copy(isChecked = false)
            }
        }
        _availableCouponsUiState.value = UIState.Success(updatedUiCoupons)
        _changedCouponIds.value = updatedUiCoupons.map { it.id }.toSet()
    }

    private fun updatePaymentInfo() {
        val discountAmount = calculateDiscountAmount()
        _totalPaymentAmount.value = orderAmount + shippingFee - discountAmount
    }

    private fun calculateDiscountAmount(): Int {
        val discountAmount = (selectedCoupon?.discountAmount(order)) ?: 0
        _couponDiscountAmount.value = -discountAmount
        return discountAmount
    }

    override fun onMakePaymentClick() {
        val paymentAmount = totalPaymentAmount.value
        if (paymentAmount != null) {
            viewModelScope.launch {
                val orderResult = orderRepository.makeOrder(paymentAmount)
                orderResult.onSuccess {
                    _isOrderSuccess.value = true
                    _toastMessage.value = MESSAGE_ORDER_SUCCESS
                    SharedChangedIdsDB.addChangedProductsId(order.map.values.map { it.productId }.toSet())
                }.onFailure {
                    _toastMessage.value = MESSAGE_ORDER_FAILURE
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        }
    }

    override fun onBackButtonClicked() {
        _moveBack.value = true
    }

    companion object {
        const val MESSAGE_ORDER_SUCCESS = "야호~ 주문 및 결제가 완료되었어요!"
        const val MESSAGE_ORDER_FAILURE = "앗, 결제에 실패했어요! 잠시 후에 다시 시도해주세요."
    }
}
