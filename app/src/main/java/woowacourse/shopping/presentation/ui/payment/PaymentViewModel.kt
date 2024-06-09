package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.CouponRepository
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.DiscountType
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.util.Event
import java.time.LocalDateTime

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(), PaymentHandler {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val carts = MutableLiveData<List<ProductListItem.ShoppingProductItem>>()

    private val selectedCoupon: LiveData<Coupon?> =
        coupons.map { coupons ->
            coupons.firstOrNull { it.isChecked }
        }

    val deliveryPrice =
        selectedCoupon.map {
            if (it == null || it.discountType != DiscountType.FreeShipping) {
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
        selectedCoupon: LiveData<Coupon?>,
    ): Long {
        return carts.value?.let {
            val selectedCoupon = selectedCoupon.value
            if (selectedCoupon == null || selectedCoupon.discountType == DiscountType.FreeShipping) {
                0L
            } else {
                selectedCoupon.calculateDiscount(it, LocalDateTime.now())
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

    fun fetchInitialData() {
        fetchCoupon()
        fetchCarts()
    }

    private fun fetchCoupon() {
        viewModelScope.launch {
            couponRepository.loadAll().onSuccess {
                _coupons.value = it
            }.onFailure {
                // TODO
            }
        }
    }

    private fun fetchCarts() {
        viewModelScope.launch {
            cartRepository.loadAll().onSuccess {
                carts.value = it.map { cart -> cart.toShoppingProduct() }
            }.onFailure {
                // TODO
            }
        }
    }

    override fun onCouponClicked(coupon: Coupon) {
        val coupons = coupons.value ?: return
        val updatedCoupon =
            coupons.map {
                if (it.id == coupon.id) {
                    it.updateCheck(!it.isChecked)
                } else {
                    it.updateCheck(false)
                }
            }
        _coupons.value = updatedCoupon
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
