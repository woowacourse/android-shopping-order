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
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class OrderViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<CouponUiModel>>(emptyList())
    val coupons: LiveData<List<CouponUiModel>>
        get() = _coupons

    private val _orderInfo = MutableLiveData<OrderInfo>()
    val orderInfo: LiveData<OrderInfo>
        get() = _orderInfo

    fun loadSelectedProducts(products: List<ProductUiModel>) {
        _orderInfo.value = OrderInfo(orderProducts = products)
        loadCoupons()
    }

    fun selectCoupon(coupon: CouponUiModel) {
        val coupons = _coupons.value ?: return
        _coupons.value = coupons.map {
            when {
                it.id == coupon.id -> it.copy(isSelected = !it.isSelected)
                else -> it.copy(isSelected = false)
            }
        }
        calculateOrderInfo(coupon.id)
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val orderAmount = _orderInfo.value?.orderAmount ?: 0
            couponRepository.getCoupons()
                .onSuccess { coupons ->
                    _coupons.value = coupons
                        .filter { coupon ->
                            when (coupon) {
                                is FixedCoupon -> orderAmount >= coupon.minimumAmount
                                is FreeShippingCoupon -> orderAmount >= coupon.minimumAmount
                                else -> true
                            }
                        }
                        .map { it.toUiModel() }
                }
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

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OrderViewModel(
                    couponRepository = RepositoryProvider.couponRepository,
                )
            }
        }
    }
}