package woowacourse.shopping.view.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.payment.adapter.PaymentItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel(),
    PaymentEventHandler {
    private val couponItems = MutableLiveData<List<PaymentItem.CouponItem>>(emptyList())

    private val _paymentItems =
        MediatorLiveData<List<PaymentItem>>().apply {
            addSource(couponItems) { value = buildPaymentItems() }
        }
    val paymentItems: LiveData<List<PaymentItem>> get() = _paymentItems

    private val _finishOrderEvent = MutableSingleLiveData<Unit>()
    val finishOrderEvent: SingleLiveData<Unit> get() = _finishOrderEvent

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository
                .getCoupons()
                .onSuccess { coupons ->
                    couponItems.postValue(coupons.map { PaymentItem.CouponItem(it) })
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onSelectItem(item: Coupon) {
        couponItems.value =
            couponItems.value.orEmpty().map {
                if (it.coupon.id == item.id) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it.copy(isSelected = false)
                }
            }
    }

    override fun onPayClick() {
        _finishOrderEvent.postValue(Unit)
    }

    private fun buildPaymentItems(): List<PaymentItem> =
        buildList {
            add(PaymentItem.CouponHeaderItem)
            addAll(couponItems.value.orEmpty())
            add(PaymentItem.PaymentInformationItem)
        }
}
