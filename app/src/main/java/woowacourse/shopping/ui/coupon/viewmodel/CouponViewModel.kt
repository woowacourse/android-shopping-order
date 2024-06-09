package woowacourse.shopping.ui.coupon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Response
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.coupon.toUiModel
import woowacourse.shopping.ui.coupon.uimodel.CouponError
import woowacourse.shopping.ui.coupon.uimodel.CouponUiModel
import woowacourse.shopping.ui.coupon.uimodel.PaymentInfoUiModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData
import woowacourse.shopping.ui.utils.viewModelAsync
import woowacourse.shopping.ui.utils.viewModelLaunch

class CouponViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> get() = _coupons

    private val _payment: MutableLiveData<PaymentInfoUiModel> = MutableLiveData()
    val payment: LiveData<PaymentInfoUiModel> get() = _payment

    val order = MutableLiveData<Order>()

    private val _isOrderSuccess: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val isOrderSuccess: SingleLiveData<Boolean> get() = _isOrderSuccess

    private val _error: MutableSingleLiveData<CouponError> = MutableSingleLiveData()
    val error: SingleLiveData<CouponError> get() = _error

    fun loadAvailableCoupons(carts: List<CartUiModel>) {
        viewModelLaunch(::couponExceptionHandler) {
            couponRepository.allCouponsResponse().onSuccess { coupons ->
                order.value = Order(coupons)
                _coupons.value =
                    order?.value?.canUseCoupons(getCartWithProduct(carts))?.map { it.toUiModel() }
            }.checkError { _error.setValue(it) }
        }
    }

    private fun getCartWithProduct(carts: List<CartUiModel>): List<CartWithProduct> =
        carts.map {
            CartWithProduct(
                it.id,
                Product(it.productId, it.imageUrl, it.name, it.price, ""),
                Quantity(it.quantity),
            )
        }

    fun loadInitialPaymentInfo(carts: List<CartUiModel>) {

        val carts = getCartWithProduct(carts)
        _payment.value =
            PaymentInfoUiModel(
                carts.sumOf { it.product.price * it.quantity.value },
                Order.INIT_DISCOUNT_PRICE,
                Order.SHIPPING_PRICE,
                carts.sumOf { it.product.price * it.quantity.value },
            )

    }

    fun updatePaymentInfo(
        cartsUiModel: List<CartUiModel>,
        selectedCouponId: Long,
    ) {
        viewModelScope.launch {
            val cartsJob = viewModelAsync { getCartWithProduct(cartsUiModel) }
            val orderJob = viewModelAsync { order?.value ?: Order(emptyList()) }

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
                viewModelLaunch {
                    loadInitialPaymentInfo(cartsUiModel)
                }
            }
        }
    }

    fun order(carts: List<CartUiModel>) {
        viewModelLaunch(::orderExceptionHandler) {
            orderRepository.order(carts.map { it.id }).onSuccess {
                _isOrderSuccess.setValue(true)
            }.checkError {
                _isOrderSuccess.setValue(false)
                _error.setValue(it)
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


    private fun couponExceptionHandler(throwable: Throwable) {
        _error.setValue(CouponError.LoadCoupon)
    }

    private fun orderExceptionHandler(throwable: Throwable) {
        _error.setValue(CouponError.Order)
    }

    private inline fun <reified T : Any?> Response<T>.checkError(execute: (CouponError) -> Unit) = apply {
        when (this) {
            is Response.Success -> {}
            is Fail.InvalidAuthorized -> execute(CouponError.InvalidAuthorized)
            is Fail.Network -> execute(CouponError.Network)
            is Fail.NotFound -> {
                when (T::class) {
                    Coupon::class -> execute(CouponError.LoadCoupon)
                    Order::class -> execute(CouponError.Order)
                    else -> execute(CouponError.UnKnown)
                }
            }

            is Response.Exception -> {
                Log.d(this.javaClass.simpleName, "${this.e}")
                execute(CouponError.UnKnown)
            }
        }
    }

}
