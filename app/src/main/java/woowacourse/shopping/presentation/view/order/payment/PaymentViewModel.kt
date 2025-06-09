package woowacourse.shopping.presentation.view.order.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.DisplayModel
import woowacourse.shopping.presentation.model.OrderUiModel
import woowacourse.shopping.presentation.model.toUiModel
import java.time.LocalTime

class PaymentViewModel(
    private val orderCartIds: List<Long>,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(),
    CouponStateEventListener {
    private val _order = MutableLiveData<OrderUiModel>()
    val order: LiveData<OrderUiModel> = _order

    private val _coupons = MutableLiveData<List<DisplayModel<CouponUiModel>>>()
    val coupons: LiveData<List<DisplayModel<CouponUiModel>>> = _coupons

    init {
        viewModelScope.launch {
            initOrder()
        }
    }

    private fun initOrder() {
        viewModelScope.launch {
            val order = orderRepository.fetchOrder(orderCartIds, null)
            _order.value = order.toUiModel()
            couponRepository
                .fetchFilteredCoupons(order.purchases, LocalTime.now())
                .onSuccess { coupons ->
                    _coupons.value =
                        coupons.value.map { coupon ->
                            DisplayModel(coupon.toUiModel())
                        }
                }.onFailure {
                }
        }
    }

    private fun loadOrder(
        orderCartIds: List<Long>,
        coupon: CouponUiModel?,
    ) {
        viewModelScope.launch {
            _order.value = orderRepository.fetchOrder(orderCartIds, coupon?.id).toUiModel()
        }
    }

    override fun onSelectCoupon(coupon: CouponUiModel) {
        _coupons.value =
            coupons.value.orEmpty().map { currentCoupon ->
                if (currentCoupon.data.id == coupon.id) {
                    val currentIsSelected = currentCoupon.isSelected
                    currentCoupon.copy(isSelected = !currentIsSelected)
                } else {
                    currentCoupon.copy(isSelected = false)
                }
            }
        val selectedCoupon = _coupons.value?.find { it.isSelected }?.data
        loadOrder(orderCartIds, selectedCoupon)
    }

    companion object {
        fun Factory(orderCartIds: List<Long>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val couponRepository = RepositoryProvider.couponRepository
                    val orderRepository = RepositoryProvider.orderRepository
                    return PaymentViewModel(orderCartIds, couponRepository, orderRepository) as T
                }
            }
    }
}
