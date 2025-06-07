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

class PaymentViewModel(
    private val cartProductRepository: CartProductRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private var selectedProducts: List<CartProduct> = listOf()

    val totalPrice: Int get() = selectedProducts.sumOf { it.totalPrice }

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    init {
        viewModelScope.launch {
            val result = couponRepository.getCoupons()
            result
                .onSuccess {
                    _coupons.value = it
                }
                .onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    fun initSelectedProducts(selectedCartProducts: List<CartProduct>) {
        selectedProducts = selectedCartProducts
    }
}
