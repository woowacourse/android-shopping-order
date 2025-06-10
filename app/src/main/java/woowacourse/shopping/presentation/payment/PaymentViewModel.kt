package woowacourse.shopping.presentation.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.RepositoryProvider.cartItemRepository
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.recommend.OrderEvent
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemFailure
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemSuccess
import woowacourse.shopping.presentation.util.SingleLiveEvent

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val initialCheckedItems: List<ProductUiModel>,
    private val applyCouponPolicyUseCase: ApplyCouponPolicyUseCase,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    private val _orderEvent = SingleLiveEvent<OrderEvent>()
    val orderEvent: LiveData<OrderEvent> = _orderEvent

    val initialOrderPrice = initialCheckedItems.sumOf { it.quantity * it.price }.toLong()

    private val _discountAmount = MutableLiveData(0L)
    val discountAmount: LiveData<Long> = _discountAmount

    private val _deliverCharge = MutableLiveData(INITIAL_DELIVERY_CHARGE)
    val deliverCharge: LiveData<Long> = _deliverCharge

    private val _totalAmount =
        MediatorLiveData<Long>().apply {
            fun update() {
                val discount = _discountAmount.value ?: 0L
                val delivery: Long = _deliverCharge.value ?: INITIAL_DELIVERY_CHARGE
                value = (initialOrderPrice - discount + delivery).coerceAtLeast(0)
            }

            addSource(_discountAmount) { update() }
            addSource(_deliverCharge) { update() }
        }
    val totalAmount: LiveData<Long> = _totalAmount

    private val _selectedCouponId = MutableLiveData<Long?>()
    val selectedCouponId: LiveData<Long?> = _selectedCouponId

    fun getCoupons() {
        val items = initialCheckedItems.map { it.toDomain() }
        viewModelScope.launch {
            val result = couponRepository.getCoupons(initialOrderPrice, items)

            result.onSuccess { coupons ->
                _coupons.postValue(coupons)
            }
        }
    }

    fun orderItems() {
        val items =
            initialCheckedItems.map {
                it.toDomain()
            }

        viewModelScope.launch {
            val cartIds = cartItemRepository.getCartIdsByProducts(items)
            val result = orderRepository.orderItems(cartIds)

            result
                .onSuccess {
                    _orderEvent.postValue(OrderItemSuccess)
                }.onFailure {
                    _orderEvent.postValue(OrderItemFailure)
                }
        }
    }

    fun onCheckCoupon(coupon: Coupon) {
        val isSelecting = (_selectedCouponId.value != coupon.id)

        if (isSelecting) {
            _selectedCouponId.postValue(coupon.id)
            updateOrderInfo(coupon)
        } else {
            _selectedCouponId.postValue(null)
            resetOrderInfo()
        }
    }

    fun resetOrderInfo() {
        _discountAmount.postValue(INITIAL_DISCOUNT_AMOUNT)
        _deliverCharge.postValue(INITIAL_DELIVERY_CHARGE)
    }

    fun updateOrderInfo(coupon: Coupon) {
        val adjustment =
            applyCouponPolicyUseCase.applyPolicy(coupon, initialOrderPrice, initialCheckedItems)

        _discountAmount.postValue(adjustment.discount)
        _deliverCharge.postValue(adjustment.deliveryCharge)
    }

    companion object {
        private const val INITIAL_DELIVERY_CHARGE = 3_000L
        private const val INITIAL_DISCOUNT_AMOUNT = 0L

        fun provideFactory(initialCheckedItems: List<ProductUiModel>): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PaymentViewModel(
                        couponRepository = RepositoryProvider.couponRepository,
                        orderRepository = RepositoryProvider.orderRepository,
                        initialCheckedItems = initialCheckedItems,
                        applyCouponPolicyUseCase = ApplyCouponPolicyUseCase(),
                    )
                }
            }
    }
}
