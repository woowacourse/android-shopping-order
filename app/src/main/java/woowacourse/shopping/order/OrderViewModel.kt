package woowacourse.shopping.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.domain.OrderingProducts
import woowacourse.shopping.product.catalog.ProductUiModel

class OrderViewModel(
    products: Array<ProductUiModel>,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    val orderingProducts: OrderingProducts = OrderingProducts(products.toList())

    private val _availableCoupons = MutableLiveData<List<Coupon>>()
    val availableCoupons: LiveData<List<Coupon>> = _availableCoupons

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val allCoupons: List<Coupon> = couponRepository.getCoupons()
            val availableCoupons: List<Coupon> = orderingProducts.availableCoupons(allCoupons)
            _availableCoupons.postValue(availableCoupons)
        }
    }
}
