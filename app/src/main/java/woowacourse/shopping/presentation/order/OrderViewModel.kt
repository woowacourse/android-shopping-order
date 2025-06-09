package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.common.mapper.toUiModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.uimodel.CouponUiModel

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(),
    CouponClickListener {
    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons
    private val _paymentUiState = MutableLiveData<PaymentSummaryUiState>()
    val paymentSummaryUiState: LiveData<PaymentSummaryUiState> = _paymentUiState

    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    fun loadOrderInfos(productIds: LongArray) {
        runCatching {
            val cartItems =
                productIds.map {
                    cartRepository.fetchCartItemById(it) ?: throw NoSuchElementException()
                }
            loadCoupons(cartItems)
        }.onFailure { _toastMessage.value = R.string.product_toast_load_failure }
    }

    override fun onClickSelect(couponId: Long) {
    }

    private fun loadCoupons(cartItems: List<CartItem>) {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess { coupons ->
                val couponUiModels = coupons.map { coupon -> coupon.toUiModel() }
                _coupons.value = couponUiModels
            }
        }
    }
}
