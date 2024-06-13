package woowacourse.shopping.ui.coupon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError
import woowacourse.shopping.domain.result.onError
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.coupon.toUiModel
import woowacourse.shopping.ui.coupon.uimodel.CouponError
import woowacourse.shopping.ui.coupon.uimodel.CouponUiModel
import woowacourse.shopping.ui.coupon.uimodel.PaymentInfoUiModel
import woowacourse.shopping.ui.utils.BaseViewModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CouponViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> get() = _coupons

    private val _payment: MutableLiveData<PaymentInfoUiModel> = MutableLiveData()
    val payment: LiveData<PaymentInfoUiModel> get() = _payment

    val order = MutableLiveData<Order>()

    private val _isOrderSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val isOrderSuccess: SingleLiveData<Boolean> get() = _isOrderSuccess

    private val _error: MutableSingleLiveData<CouponError> = MutableSingleLiveData()
    val error: SingleLiveData<CouponError> get() = _error

    private val _errorScope: MutableSingleLiveData<CouponError> = MutableSingleLiveData()
    val errorScope: SingleLiveData<CouponError> get() = _errorScope

    fun loadAvailableCoupons(carts: List<CartUiModel>) {
        viewModelLaunch {
            couponRepository.getAllCoupons().onSuccess { coupons ->
                order.value = Order(coupons)
                _coupons.value =
                    order?.value?.canUseCoupons(carts.map { it.toCartWithProduct() })
                        ?.map { it.toUiModel() }
            }.onError { setError(it, CouponError.LoadCoupon) }
        }
    }

    fun loadInitialPaymentInfo(carts: List<CartUiModel>) {
        val carts = carts.map { it.toCartWithProduct() }
        _payment.value =
            PaymentInfoUiModel(
                carts.sumOf { it.product.price * it.quantity.value },
                Order.INIT_DISCOUNT_PRICE,
                Order.SHIPPING_PRICE,
                carts.sumOf { it.product.price * it.quantity.value } + Order.SHIPPING_PRICE,
            )
    }

    fun updatePaymentInfo(
        cartsUiModel: List<CartUiModel>,
        selectedCouponId: Long,
    ) {
        viewModelScope.launch {
            val cartsJob = async { cartsUiModel.map { it.toCartWithProduct() } }
            val orderJob = async { order?.value ?: Order(emptyList()) }

            _coupons.value = _coupons.value?.reverseCheck(couponId = selectedCouponId)

            if (_coupons.value.isChecked(selectedCouponId)) {
                val carts = cartsJob.await()
                val order = orderJob.await()
                _payment.value =
                    PaymentInfoUiModel(
                        carts.sumOf { it.product.price * it.quantity.value },
                        order.discountPrice(carts, selectedCouponId),
                        order.shippingPrice(carts, selectedCouponId),
                        order.paymentPrice(carts, selectedCouponId),
                    )
            } else {
                launch {
                    loadInitialPaymentInfo(cartsUiModel)
                }
            }
        }
    }

    fun order(carts: List<CartUiModel>) {
        viewModelLaunch {
            orderRepository.order(carts.map { it.id }).onSuccess {
                _isOrderSuccess.setValue(true)
            }.onError {
                setError(it, CouponError.Order)
            }
        }
    }

    private fun List<CouponUiModel>.reverseCheck(couponId: Long): List<CouponUiModel>? {
        val coupon = _coupons.value?.firstOrNull { it.id == couponId } ?: return emptyList()
        return if (coupon.isChecked) {
            _coupons.value?.map { if (it.id == couponId) it.copy(isChecked = false) else it }
        } else {
            _coupons.value?.map {
                if (it.id == couponId) {
                    it.copy(isChecked = true)
                } else {
                    it.copy(
                        isChecked = false,
                    )
                }
            }
        }
    }

    private fun List<CouponUiModel>?.isChecked(couponId: Long): Boolean =
        this?.firstOrNull { it.id == couponId }?.isChecked ?: false

    private fun setError(
        dataError: DataError,
        errorScope: CouponError,
    ) {
        if (dataError is ShowError) {
            mutableDataError.setValue(dataError)
        } else {
            _errorScope.setValue(errorScope)
        }
    }
}
