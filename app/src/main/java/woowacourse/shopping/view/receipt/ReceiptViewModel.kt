package woowacourse.shopping.view.receipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.repository.CouponRepository
import woowacourse.shopping.data.coupon.repository.DefaultCouponRepository
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.Receipt
import woowacourse.shopping.domain.coupon.BoGoCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponService
import woowacourse.shopping.domain.coupon.FixedCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.MiracleSaleCoupon
import java.time.LocalDateTime

class ReceiptViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository(),
) : ViewModel() {
    private var _couponItem: MutableLiveData<List<CouponItem>> = MutableLiveData()
    val couponItem: LiveData<List<CouponItem>> = _couponItem

    private var _receipt: MutableLiveData<Receipt> = MutableLiveData()
    val receipt: LiveData<Receipt> = _receipt

    private var coupons: List<Coupon> = emptyList()

    private var _selectedCoupon: MutableLiveData<CouponItem> = MutableLiveData()
    val selectedCoupon: LiveData<CouponItem> = _selectedCoupon

    private var _discount: MutableLiveData<Int> = MutableLiveData()
    val discount: LiveData<Int> = _discount

    private var _result: MutableLiveData<Int> = MutableLiveData()
    val result: LiveData<Int> = _result


    fun select(couponItem: CouponItem) {
        _selectedCoupon.value = couponItem
        loadCoupons()
    }

    fun unselect() {
        _selectedCoupon.value = null
        loadCoupons()
    }

    private fun calculator(couponItem: CouponItem?) {
        val receipt = this.receipt.value ?: Receipt(emptyList())
        if (couponItem == null) {
            _discount.postValue(0)
            _result.postValue(receipt.totalPrice + receipt.shippingPrice)
            return
        }

        val coupon = findCouponById(couponItem.couponId)
        val discountPrice = coupon.discountPrice(receipt)
        _discount.postValue(discountPrice)
        _result.postValue(receipt.totalPrice - discountPrice + receipt.shippingPrice)
    }

    private fun loadScreen() {
        _result.value = receipt.value?.totalPrice?.plus(receipt.value?.shippingPrice ?: 0)
    }

    private fun findCouponById(couponId: Long): Coupon {
        return coupons.find {
            it.couponId == couponId
        } ?: error("")
    }

    private fun loadCoupons() {
        val currentSelected = selectedCoupon.value
        _couponItem.postValue(
            coupons.map { coupon ->
                val item = when (coupon) {
                    is FixedCoupon -> CouponItem(
                        couponId = coupon.couponId,
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        minimumOrderPrice = coupon.minimumOrderPrice
                    )

                    is FreeShippingCoupon -> CouponItem(
                        couponId = coupon.couponId,
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        minimumOrderPrice = coupon.minimumOrderPrice
                    )

                    is BoGoCoupon -> CouponItem(
                        couponId = coupon.couponId,
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                    )

                    is MiracleSaleCoupon -> CouponItem(
                        couponId = coupon.couponId,
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        availableTime = "사용 가능한 시간 ${coupon.startHour}~${coupon.endHour}"
                    )
                }

                if (item.couponId == currentSelected?.couponId) {
                    _selectedCoupon.postValue(item)
                    calculator(selectedCoupon.value)
                } else if (selectedCoupon.value == null) {
                    calculator(null)
                }

                item
            }
        )
    }


    fun showAvailableCoupons(cartItems: List<CartItem>) {
        viewModelScope.launch {
            runCatching {
                couponRepository.loadCoupons()
            }.onSuccess { coupons ->
                val couponService = CouponService(coupons)
                val receipt = Receipt(cartItems)
                val result = couponService.applyApplicableCoupons(
                    receipt = receipt,
                    current = LocalDateTime.now()
                )
                this@ReceiptViewModel._receipt.value = receipt

                this@ReceiptViewModel.coupons = result
                loadCoupons()
                loadScreen()
            }.onFailure {
                //TODO : Handle by event
            }
        }
    }
}