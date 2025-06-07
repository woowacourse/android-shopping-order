package woowacourse.shopping.view.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.payment.adapter.CouponItem

class PaymentViewModel(
    private val cartProductRepository: CartProductRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private var selectedProducts: List<CartProduct> = listOf()

    val totalPrice: Int get() = selectedProducts.sumOf { it.totalPrice }

    private val _coupons = MutableLiveData<List<CouponItem>>()
    val coupons: LiveData<List<CouponItem>> get() = _coupons

    private var selectedCouponId: Int? = null

    init {
        viewModelScope.launch {
            val result = couponRepository.getCoupons()
            result
                .onSuccess { coupons ->
                    _coupons.value = coupons.map { CouponItem(it) }
                }
                .onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    fun initSelectedProducts(selectedCartProducts: List<CartProduct>) {
        selectedProducts = selectedCartProducts
    }

    fun selectCoupon(coupon: Coupon) {
        val oldSelectedId = selectedCouponId
        val newlySelectedId = coupon.id

        selectedCouponId = if (newlySelectedId == oldSelectedId) null else newlySelectedId

        _coupons.value =
            _coupons.value?.map { item ->
                when (item.coupon.id) {
                    newlySelectedId -> item.copy(isSelected = newlySelectedId != oldSelectedId)
                    oldSelectedId -> item.copy(isSelected = false)
                    else -> item
                }
            }
    }
}
