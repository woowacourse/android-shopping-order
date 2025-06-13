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

    private val _orderAmount = MutableLiveData<Int>(0)
    val orderAmount: LiveData<Int> = _orderAmount

    private val _discountAmount = MutableLiveData<Int>(0)
    val discountAmount: LiveData<Int> = _discountAmount

    private val _totalAmount = MutableLiveData<Int>(0)
    val totalAmount: LiveData<Int> = _totalAmount

    val shipmentFee: Int = SHIPMENT_FEE

    init {
        getCoupons()
    }

    fun getOrderProducts(products: List<ProductUiModel>) {
        _orderProducts.value = products
        setOrderAmount()
        setTotalAmount()
    }

    fun setOrderAmount() {
        _orderAmount.value = _orderProducts.value?.sumOf { it.price }
    }

    fun getCoupons() {
        viewModelScope.launch {
            val coupons =
                payRepository.getCoupons().map { coupon -> mapOf(coupon to false) }
            _couponList.postValue(coupons)
        }
    }

    fun setDiscountAmount() {
        _couponList.value
            ?.forEach { couponMap ->
                couponMap.forEach { (coupon, isChecked) ->
                    if (isChecked) {
                        _discountAmount.value = coupon.discount
                    }
                }
            }
    }

    fun setTotalAmount() {
        _orderAmount.value?.let { orderAmount ->
            _discountAmount.value?.let { discountAmount ->
                _totalAmount.value = orderAmount - discountAmount + shipmentFee
            }
        }
    }

    fun updateCoupon(coupon: Coupon) {
        val coupons = _couponList.value.orEmpty()

        val updatedList = coupons.map { map ->
            map.mapValues { (key, _) ->
                if (key == coupon) {
                    val isOrderAmountValid = (_orderAmount.value ?: 0) >= coupon.minimumAmount
                    val isNotExpired = !coupon.expirationDate.isBefore(LocalDate.now())

                    if (isOrderAmountValid && isNotExpired) {
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
        }

        _couponList.value = updatedList
        setDiscountAmount()
        setTotalAmount()
    }

    companion object {
        private const val SHIPMENT_FEE = 3000
    }
}
