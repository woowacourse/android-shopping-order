package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.OrderInfo
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.SingleLiveEvent

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<CouponUiModel>>(emptyList())
    val coupons: LiveData<List<CouponUiModel>>
        get() = _coupons

    private val _orderInfo = MutableLiveData<OrderInfo>()
    val orderInfo: LiveData<OrderInfo>
        get() = _orderInfo

    private val _orderEvent = SingleLiveEvent<Unit>()
    val orderEvent: LiveData<Unit>
        get() = _orderEvent

    fun loadSelectedProducts(products: List<ProductUiModel>) {
        _orderInfo.value = OrderInfo(orderProducts = products)
        loadCoupons()
    }

    fun selectCoupon(coupon: CouponUiModel) {
        val coupons = _coupons.value ?: return
        _coupons.value =
            coupons.map {
                when {
                    it.id == coupon.id -> it.copy(isSelected = !it.isSelected)
                    else -> it.copy(isSelected = false)
                }
            }
        calculateOrderInfo(coupon.id)
    }

    fun payToOrder() {
        viewModelScope.launch {
            val orderInfo = _orderInfo.value ?: return@launch
            val ids = orderInfo.orderProducts.map { it.id }
            orderRepository.orderProducts(ids)
                .onSuccess {
                    _orderEvent.value = Unit
                }
        }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val orderAmount = _orderInfo.value?.orderAmount ?: 0

            val result = couponRepository.getCoupons()
            if (result.isFailure) return@launch

            val filtered = result.getOrNull()
                ?.filter { it.isApplicable(orderAmount) }
                ?.map { it.toUiModel() }
                ?: return@launch

            _coupons.value = filtered
        }
    }

    private fun calculateOrderInfo(couponId: Long) {
        viewModelScope.launch {
            val coupons = couponRepository.getCoupons().getOrNull() ?: return@launch
            val targetCoupon = coupons.find { it.id == couponId } ?: return@launch
            val currentOrderInfo = _orderInfo.value ?: return@launch
            _orderInfo.value = targetCoupon.calculateDiscount(currentOrderInfo)
        }
    }

    private fun Coupon.isApplicable(orderAmount: Int): Boolean {
        return when (this) {
            is FixedCoupon -> orderAmount >= minimumAmount
            is FreeShippingCoupon -> orderAmount >= minimumAmount
            else -> true
        }
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    OrderViewModel(
                        couponRepository = RepositoryProvider.couponRepository,
                        orderRepository = RepositoryProvider.orderRepository,
                    )
                }
            }
    }
}
