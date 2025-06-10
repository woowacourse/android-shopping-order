package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.common.mapper.toUiModel
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.coupon.CouponContext
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.GetAvailableCouponsUseCase
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.uimodel.CouponUiModel

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val getAvailableCouponsUseCase: GetAvailableCouponsUseCase,
) : ViewModel(),
    CouponClickListener {
    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons
    private val _paymentSummaryUiState = MutableLiveData<PaymentSummaryUiState>()
    val paymentSummaryUiState: LiveData<PaymentSummaryUiState> = _paymentSummaryUiState

    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _navigateTo = SingleLiveData<Unit>()
    val navigateTo: LiveData<Unit> = _navigateTo

    private lateinit var couponContextMapper: Map<Long, CouponContext>
    private lateinit var purchaseProductIds: List<Long>

    fun loadOrderInfos(productIds: LongArray) {
        runCatching {
            val cartItems =
                productIds.map {
                    cartRepository.fetchCartItemById(it) ?: throw NoSuchElementException()
                }
            _paymentSummaryUiState.value =
                PaymentSummaryUiState(orderPrice = cartItems.sumOf { it.totalPrice })
            loadCoupons(cartItems)
        }.onFailure { _toastMessage.value = R.string.product_toast_load_failure }
    }

    fun purchase() {
        viewModelScope.launch {
            purchaseProductIds.forEach {
                cartRepository.deleteProduct(it)
            }
            _toastMessage.value = R.string.order_success
            _navigateTo.value = Unit
        }
    }

    override fun onClickSelect(couponId: Long) {
        val couponUiModels = _coupons.value ?: return
        var paymentSummaryUiState = _paymentSummaryUiState.value ?: return

        val updateCouponUiModels =
            couponUiModels.map { couponUiModel ->
                when {
                    couponUiModel.id == couponId -> {
                        val (coupon, paymentSummary) =
                            selectCoupon(
                                couponUiModel.id,
                                couponUiModel,
                                paymentSummaryUiState,
                            )
                        paymentSummaryUiState = paymentSummary
                        coupon
                    }

                    couponUiModel.isSelected -> {
                        val (coupon, paymentSummary) =
                            deselectCoupon(
                                couponUiModel.id,
                                couponUiModel,
                                paymentSummaryUiState,
                            )
                        paymentSummaryUiState = paymentSummary
                        coupon
                    }

                    else -> couponUiModel
                }
            }

        _paymentSummaryUiState.value = paymentSummaryUiState
        _coupons.value = updateCouponUiModels
    }

    private fun selectCoupon(
        couponId: Long,
        couponUiModel: CouponUiModel,
        paymentSummaryUiState: PaymentSummaryUiState,
    ): Pair<CouponUiModel, PaymentSummaryUiState> {
        couponContextMapper[couponId]?.let { coupon ->
            val updatePaymentSummary =
                if (couponUiModel.isSelected) {
                    paymentSummaryUiState.cancelCoupon(coupon)
                } else {
                    paymentSummaryUiState.applyCoupon(coupon)
                }
            return couponUiModel.copy(isSelected = !couponUiModel.isSelected) to updatePaymentSummary
        }
            ?: return couponUiModel to paymentSummaryUiState
    }

    private fun deselectCoupon(
        couponId: Long,
        couponUiModel: CouponUiModel,
        paymentSummaryUiState: PaymentSummaryUiState,
    ): Pair<CouponUiModel, PaymentSummaryUiState> {
        couponContextMapper[couponId]?.let { coupon ->
            val updatePaymentSummary = paymentSummaryUiState.cancelCoupon(coupon)
            return couponUiModel.copy(isSelected = false) to updatePaymentSummary
        }
        return couponUiModel to paymentSummaryUiState
    }

    private fun loadCoupons(cartItems: List<CartItem>) {
        viewModelScope.launch {
            getAvailableCouponsUseCase(cartItems)
                .onSuccess { couponContexts ->
                    purchaseProductIds = cartItems.map { it.product.productId }
                    couponContextMapper = couponContexts.associateBy { it.coupon.couponBase.id }
                    val couponUiModels =
                        couponContexts.map { couponContext -> couponContext.coupon.toUiModel() }
                    _coupons.value = couponUiModels
                }.onFailure { _toastMessage.value = R.string.order_coupon_load_fail }
        }
    }
}
