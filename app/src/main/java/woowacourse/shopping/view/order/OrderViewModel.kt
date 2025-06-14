package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository
import woowacourse.shopping.data.order.repository.DefaultOrderRepository
import woowacourse.shopping.data.order.repository.OrderRepository
import woowacourse.shopping.domain.order.Coupon
import woowacourse.shopping.domain.order.ShippingFee
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import java.time.LocalTime

class OrderViewModel(
    private val productsToOrder: List<ShoppingCartProduct>,
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
    private val orderRepository: OrderRepository = DefaultOrderRepository.get(),
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponItem>> = MutableLiveData()
    val coupons: LiveData<List<CouponItem>> get() = _coupons

    val applyingCoupon: LiveData<CouponItem?> =
        _coupons.map { list ->
            list.find { it.isSelected }
        }

    private val _price: MutableLiveData<Int> = MutableLiveData(productsToOrder.sumOf { it.price })
    val price: LiveData<Int> get() = _price

    private val _shippingFee: MutableLiveData<ShippingFee> = MutableLiveData(ShippingFee())
    val shippingFee: LiveData<ShippingFee> get() = _shippingFee

    private val _couponDiscount: MediatorLiveData<Int> = MediatorLiveData(INITIAL_AMOUNT)
    val couponDiscount: LiveData<Int> get() = _couponDiscount

    private val _totalPrice: MediatorLiveData<Int> = MediatorLiveData(INITIAL_AMOUNT)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _event: MutableSingleLiveData<OrderEvent> = MutableSingleLiveData()
    val event: SingleLiveData<OrderEvent> get() = _event

    init {
        _totalPrice.apply {
            addSource(price) { calculateTotalPrice() }
            addSource(shippingFee) { calculateTotalPrice() }
            addSource(couponDiscount) { calculateTotalPrice() }
        }
        _couponDiscount.apply {
            addSource(applyingCoupon) { calculateCouponDiscount() }
            addSource(price) { calculateCouponDiscount() }
            addSource(shippingFee) { calculateCouponDiscount() }
        }

        getCoupons()
    }

    private fun calculateTotalPrice() {
        val basePrice = price.value ?: INITIAL_AMOUNT
        val shipping = shippingFee.value?.amount ?: INITIAL_AMOUNT
        val discount = couponDiscount.value ?: INITIAL_AMOUNT

        _totalPrice.value = basePrice + shipping + discount
    }

    private fun calculateCouponDiscount() {
        val couponItem = applyingCoupon.value
        val priceToOrder = price.value ?: INITIAL_AMOUNT

        val discountAmount =
            when (val coupon = couponItem?.origin) {
                is Coupon.PriceDiscount -> coupon.calculateDiscount()
                is Coupon.PercentageDiscount -> coupon.calculateDiscount(priceToOrder)
                is Coupon.Bonus -> coupon.calculateDiscount(productsToOrder)
                is Coupon.FreeShipping -> -coupon.calculateDiscount(shippingFee.value ?: return)
                null -> INITIAL_AMOUNT
            }

        _couponDiscount.value = discountAmount
    }

    private fun getCoupons() {
        viewModelScope.launch {
            couponRepository
                .getCoupons()
                .onSuccess { coupons ->
                    handleAvailableCoupons(coupons)
                }.onFailure {
                    _event.setValue(OrderEvent.GET_COUPON_FAILURE)
                }
        }
    }

    private fun handleAvailableCoupons(coupons: List<Coupon>) {
        val availableCoupons =
            coupons.filter { coupon ->
                if (coupon.isExpiration) return@filter false

                when (coupon) {
                    is Coupon.PriceDiscount -> coupon.isAvailable(price.value ?: return)
                    is Coupon.FreeShipping -> coupon.isAvailable(price.value ?: return)
                    is Coupon.PercentageDiscount -> coupon.isAvailable(LocalTime.now())
                    is Coupon.Bonus -> coupon.isAvailable(productsToOrder)
                }
            }

        _coupons.value = availableCoupons.map { it.toUiModel() }
    }

    fun updateApplyingCoupon(couponId: Int) {
        val updatedList =
            _coupons.value?.map {
                if (it.id == couponId) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it.copy(isSelected = false)
                }
            }
        _coupons.value = updatedList
    }

    fun createOrder() {
        viewModelScope.launch {
            orderRepository
                .placeOrder(productsToOrder.map { it.id })
                .onSuccess {
                    _event.setValue(OrderEvent.CREATE_ORDER_SUCCESS)
                }.onFailure {
                    _event.setValue(OrderEvent.CREATE_ORDER_FAILURE)
                }
        }
    }

    companion object {
        private const val INITIAL_AMOUNT = 0

        fun provideFactory(productsToOrder: Array<ShoppingCartProduct>): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    OrderViewModel(
                        productsToOrder.toList(),
                    ) as T
            }
    }
}
