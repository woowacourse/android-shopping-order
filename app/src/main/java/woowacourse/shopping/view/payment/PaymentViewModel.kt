package woowacourse.shopping.view.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.payment.adapter.PaymentItem

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val couponItems = MutableLiveData<List<PaymentItem.CouponItem>>(emptyList())

    private val _paymentItems =
        MediatorLiveData<List<PaymentItem>>().apply {
            addSource(couponItems) { value = buildPaymentItems() }
        }
    val paymentItems: LiveData<List<PaymentItem>> get() = _paymentItems

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

    private fun buildPaymentItems(): List<PaymentItem> =
        buildList {
            add(PaymentItem.CouponHeaderItem)
            addAll(couponItems.value.orEmpty())
            add(PaymentItem.PaymentInformationItem)
        }
}
