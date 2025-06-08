package woowacourse.shopping.view.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.payment.adapter.PaymentItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class PaymentViewModel(
    selectedProducts: List<CartProduct>,
    private val couponRepository: CouponRepository,
) : ViewModel(),
    PaymentEventHandler {
    private val couponItems = MutableLiveData<List<PaymentItem.CouponItem>>(emptyList())

    val order: LiveData<Order> =
        couponItems.map { couponItems ->
            Order(
                selectedProducts,
                couponItems.firstOrNull { it.isSelected }?.coupon,
            )
        }

    private val _paymentItems =
        MediatorLiveData<List<PaymentItem>>().apply {
            addSource(couponItems) { value = buildPaymentItems() }
            addSource(order) { value = buildPaymentItems() }
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
        val currentItems = couponItems.value.orEmpty()
        val updatedItems =
            currentItems.map {
                if (it.coupon.id == item.id) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it.copy(isSelected = false)
                }
            }
        couponItems.value = updatedItems
    }

    override fun onPayClick() {
        _finishOrderEvent.postValue(Unit)
    }

    private fun buildPaymentItems(): List<PaymentItem> =
        buildList {
            add(PaymentItem.CouponHeaderItem)
            addAll(couponItems.value.orEmpty())
            order.value?.let {
                add(PaymentItem.PaymentInformationItem(it))
            }
        }
}
