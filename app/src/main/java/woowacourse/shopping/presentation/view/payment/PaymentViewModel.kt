package woowacourse.shopping.presentation.view.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentSummary
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.common.model.CouponUiModel
import woowacourse.shopping.presentation.common.model.PaymentSummaryUiModel
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.common.util.MutableSingleLiveData
import woowacourse.shopping.presentation.common.util.SingleLiveData
import woowacourse.shopping.presentation.view.payment.adapter.CouponAdapter
import woowacourse.shopping.presentation.view.payment.event.PaymentMessageEvent

class PaymentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(),
    CouponAdapter.CouponEventListener {
    private val _toastEvent = MutableSingleLiveData<PaymentMessageEvent>()
    val toastEvent: SingleLiveData<PaymentMessageEvent> = _toastEvent

    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons

    private val _paymentSummary = MutableLiveData<PaymentSummary>()
    val paymentSummary: LiveData<PaymentSummaryUiModel> = _paymentSummary.map { it.toUiModel() }

    private val _orderSuccessEvent = MutableSingleLiveData<Unit>()
    val orderSuccessEvent: SingleLiveData<Unit> = _orderSuccessEvent

    private var allCoupons: List<Coupon> = emptyList()

    init {
        loadInitialPaymentSummary()
    }

    override fun onSelectCoupon(couponId: Long) {
        _coupons.value =
            _coupons.value.orEmpty().map { coupon ->
                coupon.copy(isSelected = coupon.id == couponId && !coupon.isSelected)
            }
        updatePaymentSummaryWithSelectedCoupon()
    }

    fun postOrder() {
        viewModelScope.launch {
            orderRepository
                .postOrder(orderProductIds())
                .onSuccess {
                    emitToast(PaymentMessageEvent.ORDER_SUCCESS)
                    _orderSuccessEvent.setValue(Unit)
                }.onFailure {
                    emitToast(PaymentMessageEvent.ORDER_FAILURE)
                }
        }
    }

    private fun loadInitialPaymentSummary() {
        val productIds: List<Long> = savedStateHandle[EXTRAS_ORDER_PRODUCT_IDS] ?: emptyList()
        viewModelScope.launch {
            orderRepository
                .createPaymentSummary(productIds)
                .onSuccess {
                    _paymentSummary.value = it
                    loadCoupons()
                }.onFailure {
                    emitToast(PaymentMessageEvent.PAYMENT_SUMMARY_FAILURE)
                }
        }
    }

    private fun loadCoupons(isFilterAvailable: Boolean = true) {
        val summary = _paymentSummary.value ?: return
        viewModelScope.launch {
            couponRepository
                .fetchCoupons(summary, isFilterAvailable)
                .onSuccess {
                    allCoupons = it
                    _coupons.value = it.map(Coupon::toUiModel)
                    updatePaymentSummaryWithSelectedCoupon()
                }.onFailure {
                    emitToast(PaymentMessageEvent.COUPON_FETCH_FAILURE)
                }
        }
    }

    private fun updatePaymentSummaryWithSelectedCoupon() {
        val selectedId = selectedCouponId()
        val original = _paymentSummary.value ?: return
        val updatedSummary = PaymentSummary(original.products).applyDiscount(selectedId)
        _paymentSummary.value = updatedSummary
    }

    private fun PaymentSummary.applyDiscount(couponId: Long?): PaymentSummary {
        val coupon = allCoupons.firstOrNull { it.id == couponId } ?: return this
        return coupon.calculateDiscountAmount(this)
    }

    private fun selectedCouponId(): Long? =
        _coupons.value
            .orEmpty()
            .firstOrNull { it.isSelected }
            ?.id

    private fun orderProductIds(): List<Long> =
        _paymentSummary.value
            ?.products
            .orEmpty()
            .map { it.cartId }

    private fun emitToast(event: PaymentMessageEvent) {
        _toastEvent.setValue(event)
    }

    companion object {
        private const val EXTRAS_ORDER_PRODUCT_IDS = "ORDER_PRODUCT_IDS"

        fun Factory(orderProductIds: List<Long>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val handle = extras.createSavedStateHandle()
                    handle[EXTRAS_ORDER_PRODUCT_IDS] = orderProductIds

                    return PaymentViewModel(
                        handle,
                        RepositoryProvider.couponRepository,
                        RepositoryProvider.orderRepository,
                    ) as T
                }
            }
    }
}
