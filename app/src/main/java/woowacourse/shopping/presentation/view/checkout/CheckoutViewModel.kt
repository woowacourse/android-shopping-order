package woowacourse.shopping.presentation.view.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.RepositoryProvider

class CheckoutViewModel(private val couponRepository: CouponRepository) : ViewModel() {
    fun loadCoupons() {
        viewModelScope.launch {
            val coupons = couponRepository.loadCoupons()
            coupons.forEach { coupon ->
                Log.d("Coupon", "$coupon")
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val couponRepository = RepositoryProvider.couponRepository
                    return CheckoutViewModel(couponRepository) as T
                }
            }
    }
}
