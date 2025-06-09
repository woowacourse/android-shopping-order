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
        var paymentSummary = _paymentSummaryUiState.value ?: return

        val updateCouponUiModels =
            couponUiModels.map { couponUiModel ->
                if (couponUiModel.id == couponId) {
                    if (!couponUiModel.isSelected) {
                        val coupon = couponContextMapper[couponId]!!
                        paymentSummary =
                            paymentSummary.update(
                                -coupon.getDiscountPrice(),
                                -coupon.getDiscountDeliveryFee(),
                            )
                        couponUiModel.copy(isSelected = true)
                    } else {
                        val coupon = couponContextMapper[couponId]!!
                        paymentSummary =
                            paymentSummary.update(
                                coupon.getDiscountPrice(),
                                coupon.getDiscountDeliveryFee(),
                            )
                        couponUiModel.copy(isSelected = false)
                    }
                } else if (couponUiModel.isSelected) {
                    val coupon = couponContextMapper[couponUiModel.id]!!
                    paymentSummary =
                        paymentSummary.update(
                            coupon.getDiscountPrice(),
                            coupon.getDiscountDeliveryFee(),
                        )
                    couponUiModel.copy(isSelected = false)
                } else {
                    couponUiModel
                }
            }
        _paymentSummaryUiState.value = paymentSummary
        _coupons.value = updateCouponUiModels
    }

    private fun loadCoupons(cartItems: List<CartItem>) {
        viewModelScope.launch {
            getAvailableCouponsUseCase(cartItems).onSuccess { couponContexts ->
                purchaseProductIds = cartItems.map { it.product.productId }
                couponContextMapper = couponContexts.associateBy { it.coupon.couponBase.id }
                val couponUiModels =
                    couponContexts.map { couponContext -> couponContext.coupon.toUiModel() }
                _coupons.value = couponUiModels
            }
        }
    }
}
