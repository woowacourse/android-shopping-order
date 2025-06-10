package woowacourse.shopping.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.OrderingProducts
import woowacourse.shopping.product.catalog.ProductUiModel
import java.time.LocalDateTime

class OrderViewModel(
    products: Array<ProductUiModel>,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val orderingProducts: OrderingProducts =
        OrderingProducts(LocalDateTime.now(), products.toList())

    private val _availableDisplayingCoupons = MutableLiveData<List<CouponUiModel>>(emptyList())
    val availableDisplayingCoupons: LiveData<List<CouponUiModel>> = _availableDisplayingCoupons

    private lateinit var availableCoupons: List<Coupon>

    private val _currentOrderPriceAmount = MutableLiveData<Int>(0)
    val currentOrderPriceAmount: LiveData<Int> = _currentOrderPriceAmount

    private val _couponDiscountAmount = MutableLiveData<Int>(0)
    val couponDiscountAmount: LiveData<Int> = _couponDiscountAmount

    private val defaultShippingFee = 3000

    private val _shippingFee = MutableLiveData<Int>(defaultShippingFee)
    val shippingFee: LiveData<Int> = _shippingFee

    private val _totalPayingAmount = MutableLiveData<Int>(0)
    val totalPayingAmount: LiveData<Int> = _totalPayingAmount

    private val _checkSelected = MutableLiveData<Coupon>()
    val checkSelected: LiveData<Coupon> = _checkSelected

    private val _isOrderMade = MutableLiveData<Boolean>()
    val isOrderMade: LiveData<Boolean> = _isOrderMade

    init {
        loadCoupons()
        setAmountsByOrderingProducts()
    }

    fun applyCoupon(couponId: Long) {
        val selectedCoupon: Coupon = availableCoupons.first { it.id == couponId }
        orderingProducts.applyCoupon(selectedCoupon)
        _checkSelected.postValue(selectedCoupon)
        setAmountsByOrderingProducts()
    }

    fun makeOrder() {
        val cartItemIds: List<Long> = orderingProducts.products.mapNotNull { it.cartItemId }
        viewModelScope.launch {
            val result: Result<Unit> = orderRepository.insertOrders(cartItemIds)
            _isOrderMade.value = result.isSuccess
        }
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val allCoupons: List<Coupon> = couponRepository.getCoupons()
            val filteredAvailableCoupons: List<Coupon> =
                orderingProducts.availableCoupons(allCoupons)
            availableCoupons = filteredAvailableCoupons
            _availableDisplayingCoupons.value = filteredAvailableCoupons.map { it.toUiModel() }
        }
    }

    private fun setAmountsByOrderingProducts() {
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
