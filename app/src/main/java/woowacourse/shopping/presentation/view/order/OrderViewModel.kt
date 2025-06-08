package woowacourse.shopping.presentation.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toUiModel

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private var selectedItems = emptyList<CartItemUiModel>() ?: emptyList()

    private val _couponList = MutableLiveData<List<CouponUiModel>>()
    val couponList: LiveData<List<CouponUiModel>> = _couponList

    private val _selectedCoupon = MutableLiveData<CouponUiModel?>()
    val selectedCoupon: LiveData<CouponUiModel?> = _selectedCoupon

    fun loadCoupons() {
        viewModelScope.launch {
            couponRepository
                .getAvailableCoupons(selectedItems.map { it.toDomain() })
                .onSuccess { coupon ->
                    _couponList.value = coupon.map { it.toUiModel() }
                }.onFailure {
                    _couponList.value = emptyList()
                }
        }
    }

    fun selectCoupon(coupon: CouponUiModel) {
        if (_selectedCoupon.value?.id == coupon.id) {
            _selectedCoupon.value = null
            _couponList.value =
                _couponList.value?.map {
                    it.copy(isSelected = false)
                }
        } else {
            _selectedCoupon.value = coupon
            _couponList.value =
                _couponList.value?.map {
                    it.copy(isSelected = it.id == coupon.id)
                }
        }
    }

    fun setSelectedItems(items: List<CartItemUiModel>) {
        selectedItems = items.toList()
    }

    companion object {
        fun factory(
            orderRepository: OrderRepository,
            couponRepository: CouponRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderViewModel(orderRepository, couponRepository) as T
            }
    }
}
