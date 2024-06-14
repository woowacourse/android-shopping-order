package woowacourse.shopping.presentation.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.domain.datasource.map
import com.example.domain.datasource.onSuccess
import com.example.domain.model.CartItem
import com.example.domain.model.coupon.Coupon
import com.example.domain.repository.CartRepository
import com.example.domain.repository.CouponRepository
import com.example.domain.repository.OrderRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class PurchaseViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel(), PurchaseActionHandler {
    private val cartItems: MutableLiveData<List<CartItem>> = MutableLiveData()
    val totalPrice: LiveData<Int> get() = cartItems.map { it.sumOf { it.quantity.count * it.product.price } }

    private val _couponUiModels: MutableLiveData<CouponUiModels> = MutableLiveData()
    val couponUiModels: LiveData<CouponUiModels> get() = _couponUiModels

    val selectedCoupon: LiveData<Coupon?>
        get() =
            couponUiModels.map { couponUiModels ->
                couponUiModels.couponUiModelList.firstOrNull {
                    it.checked
                }?.coupon
            }

    val discountPrice: LiveData<Int>
        get() =
            selectedCoupon.map {
                when (it) {
                    is Coupon.FixedCoupon -> -it.discount
                    is Coupon.BuyXGetYCoupon -> -getBogoDiscount(it)
                    is Coupon.PercentageCoupon -> -getMiracleDiscount(it)
                    else -> 0
                }
            }

    val deliveryPrice: LiveData<Int>
        get() =
            selectedCoupon.map {
                if (it is Coupon.FreeShippingCoupon) 0 else 3000
            }

    val finalPrice: LiveData<Int>
        get() =
            totalPrice.map {
                deliveryPrice.value?.plus(it) ?: 0
            }.map { discountPrice.value?.plus(it) ?: 0 }

    init {
        savedStateHandle.get<IntArray>(PurchaseActivity.CART_ITEMS_ID)?.toList()
            ?.let(::getCartItems)
    }

    private fun getBogoDiscount(coupon: Coupon.BuyXGetYCoupon): Int {
        val mostExpensiveBogo =
            cartItems.value?.filter {
                it.quantity.count < coupon.buyQuantity
            }?.maxBy {
                it.product.price
            }
        return (mostExpensiveBogo?.product?.price?.times(coupon.getQuantity)) ?: 0
    }

    private fun getMiracleDiscount(coupon: Coupon.PercentageCoupon): Int = totalPrice.value?.let { (it * coupon.discount / 100) } ?: 0

    private fun getCartItems(cartItemIds: List<Int>) {
        viewModelScope.launch {
            val result =
                cartRepository.findAll().map { allCartItems ->
                    allCartItems.filter { cartItem ->
                        cartItemIds.contains(cartItem.id)
                    }
                }
            result.onSuccess {
                cartItems.value = it
            }
        }
    }

    fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess { coupons ->
                _couponUiModels.value = coupons.toCouponUiModels()
            }
        }
    }

    override fun selectCoupon(
        couponId: Int,
        isChecked: Boolean,
    ) {
        val unselected =
            _couponUiModels.value?.couponUiModelList?.map { it.copy(checked = false) } ?: listOf()
        val oneSelected =
            unselected.map {
                if (it.coupon.id == couponId) {
                    it.copy(checked = true)
                } else {
                    it
                }
            }
        _couponUiModels.value = CouponUiModels(oneSelected)
    }

    fun List<Coupon>.toCouponUiModels(): CouponUiModels = CouponUiModels(this.map { it.toCouponUiModel() })

    fun Coupon.toCouponUiModel(): CouponUiModel {
        val available = this.isAvailable()
        return CouponUiModel(this, false, available)
    }

    private fun Coupon.isAvailable() =
        if (this.expirationDate < LocalDate.now()) {
            false
        } else {
            when (this) {
                is Coupon.FixedCoupon -> (totalPrice.value ?: 0) > this.minimumAmount
                is Coupon.FreeShippingCoupon -> (totalPrice.value ?: 0) > this.minimumAmount
                is Coupon.PercentageCoupon -> LocalTime.now() in this.availableTime
                else -> true
            }
        }

    fun createOrder() {
        /*
        val cartUiModels = cartUiState.value?.cartUiModels ?: return
        val cartItemIds = cartUiModels.filter { it.isSelected }.map { it.cartItemId }
        viewModelScope.launch {
            orderRepository.createOrder(cartItemIds)
        }
        orderRepository.createOrder(
            cartItemIds,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    _isSuccessCreateOrder.value = Event(true)
                    deleteCartItemIds(cartItemIds)
                }

                override fun onFailure(t: Throwable) {
                    _isSuccessCreateOrder.value = Event(false)
                }
            },
        )
         */
    }
}
