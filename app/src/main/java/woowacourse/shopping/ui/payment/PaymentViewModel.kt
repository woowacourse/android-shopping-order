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
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Orders
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.DefaultCouponRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.model.CouponUi
import woowacourse.shopping.ui.model.toUi
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CouponCheckListener {
    // TODO: 이거 왜 LiveData 로 안하면 안 되네
    private val _orders: MutableLiveData<Orders> = MutableLiveData()
    val orders: LiveData<Orders> get() = _orders

    private val _loadedCoupons = MutableSingleLiveData<List<CouponUi>>()
    val loadedCoupons: MutableSingleLiveData<List<CouponUi>> = _loadedCoupons

    private val _discountedPrice: MutableLiveData<Int> = MutableLiveData()
    val discountedPrice: LiveData<Int> get() = _discountedPrice

    private val _payEvent: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val payEvent: MutableSingleLiveData<Boolean> get() = _payEvent

    fun loadOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.loadAllOrders()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _orders.postValue(it)
                    }
                    yield()

                    couponRepository.availableCoupons(orders = orders.value ?: Orders.DEFAULT)
                        .onSuccess { coupons ->
                            withContext(Dispatchers.Main) {
                                _loadedCoupons.postValue(
                                    coupons.map(Coupon::toUi)
                                )
                            }
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
        viewModelScope.launch(Dispatchers.IO) {
            couponRepository.discountAmount(coupon.id, orders = orders.value ?: Orders.DEFAULT)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _discountedPrice.postValue(it)
                        _loadedCoupons.postValue(
                            value =
                            loadedCoupons.getValue()?.map { couponUi ->
                                if (couponUi.id == coupon.id) {
                                    couponUi.copy(isChecked = true)
                                } else {
                                    couponUi.copy(isChecked = false)
                                }
                            } ?: emptyList(),
                        )
                    }
                }
                .onFailure {
                    // TODO: Error handling
                    Log.e(TAG, "onCheck: failure $it")
                    throw it
                }
        }
    }

    fun pay() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.order()
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _payEvent.postValue(true)
                    }
                }
                .onFailure {
                    // TODO: Error handling
                    Log.e(TAG, "pay: failure $it")
                    throw it
                }
        }
    }

    companion object {
        private const val TAG = "PaymentViewModel"

        fun factory(): UniversalViewModelFactory =
            UniversalViewModelFactory {
                PaymentViewModel(
                    couponRepository =
                    DefaultCouponRepository(
                        ShoppingApp.couponSource,
                    ),
                    orderRepository =
                    DefaultOrderRepository(
                        ShoppingApp.orderSource,
                        ShoppingApp.cartSource,
                    ),
                )
            }
    }
}
