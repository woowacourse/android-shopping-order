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

    private fun loadCoupons() {
        _couponItem.postValue(
            coupons.map { coupon ->
                when (coupon) {
                    is FixedCoupon -> CouponItem(
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        minimumOrderPrice = coupon.minimumOrderPrice
                    )

                    is FreeShippingCoupon -> CouponItem(
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        minimumOrderPrice = coupon.minimumOrderPrice
                    )

                    is BoGoCoupon -> CouponItem(
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                    )

                    is MiracleSaleCoupon -> CouponItem(
                        description = coupon.description,
                        expirationDate = coupon.expirationDate.toString(),
                        availableTime = "사용 가능한 시간 ${coupon.startHour}~${coupon.endHour}"
                    )
                }
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
            }.onFailure {
                //TODO : Handle by event
            }
        }
    }
}