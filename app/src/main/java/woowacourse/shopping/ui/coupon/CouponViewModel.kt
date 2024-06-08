package woowacourse.shopping.ui.coupon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.cart.cartitem.CartUiModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

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

    fun loadAvailableCoupons(carts: List<CartUiModel>) {
        viewModelScope.launch {
            runCatching {
                couponRepository.getCoupons()
            }.onSuccess { coupons ->
                order.value = Order(coupons)
                _coupons.value =
                    order?.value?.canUseCoupons(getCartWithProduct(carts))?.map { it.toUiModel() }
            }
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
        viewModelScope.launch {
            val carts = getCartWithProduct(carts)
            _payment.value =
                PaymentInfoUiModel(
                    carts.sumOf { it.product.price * it.quantity.value },
                    Order.INIT_DISCOUNT_PRICE,
                    Order.SHIPPING_PRICE,
                    carts.sumOf { it.product.price * it.quantity.value },
                )
        }
    }

    fun updatePaymentInfo(
        cartsUiModel: List<CartUiModel>,
        selectedCouponId: Long,
    ) {
        viewModelScope.launch {
            val carts = getCartWithProduct(cartsUiModel)
            val order = order?.value ?: Order(emptyList())

            _coupons.value = _coupons.value?.reverseCheck(couponId = selectedCouponId)

            if (_coupons.value.isChecked(selectedCouponId)) {
                _payment.value =
                    PaymentInfoUiModel(
                        carts.sumOf { it.product.price * it.quantity.value },
                        order.discountPrice(carts, selectedCouponId),
                        order.shippingPrice(carts, selectedCouponId),
                        order.paymentPrice(carts, selectedCouponId),
                    )
            } else {
                loadInitialPaymentInfo(cartsUiModel)
            }
        }
    }

    fun order(carts: List<CartUiModel>) {
        viewModelScope.launch {
            runCatching {
                orderRepository.order(carts.map { it.id })
            }.onSuccess {
                _isOrderSuccess.setValue(true)
            }.onFailure {
                _isOrderSuccess.setValue(false)
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
}
