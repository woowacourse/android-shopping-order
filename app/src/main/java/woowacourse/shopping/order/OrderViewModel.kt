package woowacourse.shopping.order

import android.util.Log
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

    private val _availableCoupons = MutableLiveData<List<Coupon>>(emptyList())
    val availableCoupons: LiveData<List<Coupon>> = _availableCoupons

    private val _currentOrderPriceAmount = MutableLiveData<Int>(0)
    val currentOrderPriceAmount: LiveData<Int> = _currentOrderPriceAmount

    private val _couponDiscountAmount = MutableLiveData<Int>(0)
    val couponDiscountAmount: LiveData<Int> = _couponDiscountAmount

    private val defaultShippingFee = 3000

    private val _shippingFee = MutableLiveData<Int>(defaultShippingFee)
    val shippingFee: LiveData<Int> = _shippingFee

    private val _totalPayingAmount = MutableLiveData<Int>(0)
    val totalPayingAmount: LiveData<Int> = _totalPayingAmount

    init {
        loadCoupons()
        setAmountsByOrderingProducts()
        Log.d("쿠폰", "products : $orderingProducts")
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val allCoupons: List<Coupon> = couponRepository.getCoupons()
            val availableCoupons: List<Coupon> = orderingProducts.availableCoupons(allCoupons)
            _availableCoupons.postValue(availableCoupons)
            Log.d("쿠폰", "$availableCoupons")
        }
    }

    private fun setAmountsByOrderingProducts() {
        Log.d(
            "products",
            "${orderingProducts.originalTotalPrice()}, ${orderingProducts.discountAmount()}, ${orderingProducts.totalPrice()} ",
        )

        _currentOrderPriceAmount.postValue(orderingProducts.originalTotalPrice())
        _couponDiscountAmount.postValue(orderingProducts.discountAmount())
        if (orderingProducts.isFreeShipping()) {
            _shippingFee.postValue(0)
        } else {
            _shippingFee.postValue(defaultShippingFee)
        }
        _totalPayingAmount.postValue(orderingProducts.totalPrice())
    }
}
