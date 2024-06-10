package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Orders
import woowacourse.shopping.domain.model.toUi
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.DefaultCouponRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository2
import woowacourse.shopping.domain.repository.OrderRepository2
import woowacourse.shopping.ui.model.CouponUi
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository2: OrderRepository2,
) : ViewModel(), CouponCheckListener {

    // TODO: 이거 왜 LiveData 로 안하면 안 되네
    private val _orders: MutableLiveData<Orders> = MutableLiveData()
    val orders: LiveData<Orders> get() = _orders

    private val _loadedCoupons = MutableSingleLiveData<List<CouponUi>>()
    val loadedCoupons: MutableSingleLiveData<List<CouponUi>> = _loadedCoupons

    private val _discountedPrice: MutableLiveData<Int> = MutableLiveData()
    val discountedPrice: LiveData<Int> get() = _discountedPrice

    fun loadOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository2.loadAllOrders()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _orders.postValue(it)
                    }
                    yield()

                    couponRepository.availableCoupons(orders = orders.value ?: Orders.DEFAULT)
                        .onSuccess { coupons ->
                            withContext(Dispatchers.Main) {
                                _loadedCoupons.postValue(coupons.map { coupon ->
                                    coupon.toUi()
                                })
                            }
                            Log.d(TAG, "loadCoupons: success: $coupons")

                        }
                        .onFailure {
                            // TODO: Error handling
                            Log.e(TAG, "loadCoupons: failure $it")
                            throw it
                        }
                }
                .onFailure {
                    // TODO: Error handling
                    Log.e(TAG, "loadOrders: failure $it")
                    throw it
                }
        }
    }


    override fun onCheck(coupon: CouponUi) {
        Log.d(TAG, "onCheck: $coupon")
        viewModelScope.launch(Dispatchers.IO) {
            couponRepository.discountAmount(coupon.id, orders = orders.value ?: Orders.DEFAULT)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _discountedPrice.postValue(it)
                        _loadedCoupons.postValue(
                            value = loadedCoupons.getValue()?.map { couponUi ->
                                if (couponUi.id == coupon.id) {
                                    couponUi.copy(isChecked = true)
                                } else {
                                    couponUi.copy(isChecked = false)
                                }
                            } ?: emptyList())
                    }
                }
                .onFailure {
                    // TODO: Error handling
                    Log.e(TAG, "onCheck: failure $it")
                    throw it
                }
        }
    }

    companion object {
        private const val TAG = "PaymentViewModel"

        fun factory(): UniversalViewModelFactory =
            UniversalViewModelFactory {
                PaymentViewModel(
                    couponRepository = DefaultCouponRepository(
                        ShoppingApp.couponSource,
                    ),
                    orderRepository2 = DefaultOrderRepository2(
                        ShoppingApp.orderSource2,
                        ShoppingApp.cartSource,
                    )
                )
            }
    }
}
