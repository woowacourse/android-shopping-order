package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.CouponRepository
import woowacourse.shopping.domain.DiscountType
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.ui.CouponModel
import woowacourse.shopping.presentation.ui.CouponModel.Companion.toUiModel
import woowacourse.shopping.presentation.util.Event
import java.time.LocalDateTime

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(), PaymentHandler {
    private val _error = MutableLiveData<Event<PaymentError>>()
    val error: LiveData<Event<PaymentError>> get() = _error

    private val _couponModels = MutableLiveData<List<CouponModel>>()
    val couponModels: LiveData<List<CouponModel>> get() = _couponModels

    private val carts = MutableLiveData<List<ProductListItem.ShoppingProductItem>>()

    private val selectedCoupon: LiveData<CouponModel?> =
        _couponModels.map { coupons ->
            coupons.firstOrNull { it.isChecked }
        }

    val deliveryPrice =
        selectedCoupon.map {
            if (it == null || it.coupon.discountType != DiscountType.FreeShipping) {
                DELIVERY_PRICE
            } else {
                0L
            }
        }

    val totalPrice: LiveData<Long> =
        carts.map { shoppingProducts ->
            shoppingProducts.sumOf { it.quantity * it.price }
        }

    val discount: MediatorLiveData<Long> = MediatorLiveData()

    val discountedPrice: MediatorLiveData<Long> = MediatorLiveData()

    private val _paymentEvent = MutableLiveData<Event<PaymentEvent>>()

    val paymentEvent: LiveData<Event<PaymentEvent>> = _paymentEvent

    init {
        initDiscountMediateLivedata()
        initDiscountedPriceMediateLivedata()
    }

    private fun initDiscountMediateLivedata() {
        discount.apply {
            addSource(carts) {
                value = checkDiscount(carts, selectedCoupon)
            }
            addSource(selectedCoupon) {
                value = checkDiscount(carts, selectedCoupon)
            }
        }
    }

    private fun initDiscountedPriceMediateLivedata() {
        discountedPrice.apply {
            addSource(totalPrice) {
                value = checkDiscountedPrice(totalPrice, discount, deliveryPrice)
            }
            addSource(discount) {
                value = checkDiscountedPrice(totalPrice, discount, deliveryPrice)
            }
            addSource(deliveryPrice) {
                value = checkDiscountedPrice(totalPrice, discount, deliveryPrice)
            }
        }
    }

    private fun checkDiscount(
        carts: LiveData<List<ProductListItem.ShoppingProductItem>>,
        selectedCoupon: LiveData<CouponModel?>,
    ): Long {
        return carts.value?.let {
            val selectedCoupon = selectedCoupon.value
            if (selectedCoupon == null || selectedCoupon.coupon.discountType == DiscountType.FreeShipping) {
                0L
            } else {
                selectedCoupon.coupon.calculateDiscount(it, LocalDateTime.now())
            }
        } ?: 0L
    }

    private fun checkDiscountedPrice(
        totalPrice: LiveData<Long>,
        discount: MediatorLiveData<Long>,
        deliveryPrice: LiveData<Long>,
    ): Long {
        return if (totalPrice.value != null && discount.value != null && deliveryPrice.value != null) {
            totalPrice.value!! + deliveryPrice.value!! - discount.value!!
        } else {
            0
        }
    }

    fun fetchInitialData(productIds: LongArray) {
        fetchCoupon()
        fetchCarts(productIds)
    }

    private fun fetchCoupon() {
        viewModelScope.launch {
            couponRepository.loadAll().onSuccess { coupons ->
                _couponModels.value = coupons.map { it.toUiModel() }
            }.onFailure {
                _error.value = Event(PaymentError.CouponNotFound)
            }
        }
    }

    private fun fetchCarts(productIds: LongArray) {
        viewModelScope.launch {
            cartRepository.loadAll().onSuccess { fetchedCarts ->
                carts.value =
                    fetchedCarts.filter { productIds.contains(it.product.id) }
                        .map { it.toShoppingProduct() }
            }.onFailure {
                _error.value = Event(PaymentError.CartItemsNotFound)
            }
        }
    }

    override fun onCouponClicked(couponModel: CouponModel) {
        val coupons = couponModels.value ?: return
        val updatedCoupon =
            coupons.map {
                if (it.coupon.id == couponModel.coupon.id) {
                    it.copy(isChecked = !it.isChecked)
                } else {
                    it.copy(isChecked = false)
                }
            }
        _couponModels.value = updatedCoupon
    }

    override fun onPaymentClicked() {
        val cartItem = carts.value ?: return
        val productItemIds = cartItem.map { it.id }
        viewModelScope.launch {
            orderRepository.completeOrder(productItemIds).onSuccess {
                _paymentEvent.value = Event(PaymentEvent.FinishOrder)
            }.onFailure {}
        }
    }

    companion object {
        private const val DELIVERY_PRICE = 3_000L
    }
}
