package woowacourse.shopping.presentation.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository
): ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons :LiveData<List<Coupon>> = _coupons

    fun getCoupons(){
        viewModelScope.launch {
            val result = couponRepository.getCoupons()

            result.onSuccess { coupons ->
                Log.d("coupons", coupons.toString())
                _coupons.postValue(coupons)
            }
        }
    }

    companion object{
        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PaymentViewModel(
                        couponRepository = RepositoryProvider.couponRepository,
                        orderRepository = RepositoryProvider.orderRepository,
                    )
                }
            }
    }

}