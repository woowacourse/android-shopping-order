package woowacourse.shopping.pay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.PayRepository
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.product.catalog.ProductUiModel
import java.time.LocalDate

class PayViewModel(
    private val payRepository: PayRepository,
) : ViewModel() {
    private val _orderProducts = MutableLiveData<List<ProductUiModel>>()
    val orderProducts: LiveData<List<ProductUiModel>> = _orderProducts

    private val _couponList = MutableLiveData<List<Map<Coupon, Boolean>>>()
    val couponList: LiveData<List<Map<Coupon, Boolean>>> = _couponList

    private val _orderAmount = MutableLiveData<Int>()
    val orderAmount: LiveData<Int> = _orderAmount

    private val _discountAmount = MutableLiveData<Int>()
    val discountAmount: LiveData<Int> = _discountAmount

    private val _totalAmount = MutableLiveData<Int>()
    val totalAmount: LiveData<Int> = _totalAmount

    val shipmentFee: Int = SHIPMENT_FEE

    init {
        getCoupons()
    }

    fun getOrderProducts(products: List<ProductUiModel>) {
        _orderProducts.value = products
        getOrderAmount()
        getTotalAmount()
    }

    fun getOrderAmount() {
        _orderAmount.value = _orderProducts.value?.sumOf { it.price }
    }

    fun getCoupons() {
        viewModelScope.launch {
            val coupons =
                payRepository.getCoupons().map { coupon -> mapOf(coupon to false) }
            _couponList.postValue(coupons)
        }
    }

    fun getDiscountAmount() {
        _couponList.value
            ?.forEach { couponMap ->
                couponMap.forEach { (coupon, isChecked) ->
                    if (isChecked) {
                        _discountAmount.value = coupon.discount
                    }
                }
            }
    }

    fun getTotalAmount() {
        _orderAmount.value?.let { orderAmount ->
            _discountAmount.value?.let { discountAmount ->
                _totalAmount.value = orderAmount - discountAmount + shipmentFee
            }
        }
    }

    fun updateCoupon(coupon: Coupon) {
        val coupons = _couponList.value.orEmpty()

        val updatedList = coupons.map { map ->
            map.mapValues { (key, value) ->
                if (key == coupon && coupon.minimumAmount > (_totalAmount.value
                        ?: 0) && coupon.expirationDate.isBefore(LocalDate.now())
                ) {
                    !value
                } else {
                    false
                }
            }
        }

        _couponList.value = updatedList
        getDiscountAmount()
        getTotalAmount()
    }

    companion object {
        private const val SHIPMENT_FEE = 3000
    }
}
