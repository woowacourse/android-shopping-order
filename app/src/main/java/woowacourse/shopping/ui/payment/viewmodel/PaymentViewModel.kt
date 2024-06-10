package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.cart.CartWithProduct
import woowacourse.shopping.domain.model.coupon.Bogo
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.coupon.Freeshipping
import woowacourse.shopping.domain.model.coupon.MiracleSale
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.ui.base.BaseViewModel
import woowacourse.shopping.ui.payment.CouponClickListener
import woowacourse.shopping.ui.payment.CouponUiModel
import woowacourse.shopping.ui.payment.toUiModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class PaymentViewModel(
    private val orderedCartItemIds: List<Long>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
) : BaseViewModel(), CouponClickListener {
    private val orderedProducts: MutableLiveData<List<CartWithProduct>> =
        MutableLiveData(emptyList())
    val orderAmount: LiveData<Int> =
        orderedProducts.map { cartWithProducts ->
            cartWithProducts.sumOf { it.product.price * it.quantity.value }
        }

    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: MediatorLiveData<List<CouponUiModel>> =
        MediatorLiveData<List<CouponUiModel>>().apply {
            addSource(_coupons) { value = availableCoupons() }
            addSource(orderedProducts) { value = availableCoupons() }
        }

    val checkedCoupon: LiveData<CouponUiModel?> =
        _coupons.map { couponUiModels ->
            couponUiModels.firstOrNull { it.isChecked }
        }

    val totalAmount: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(orderAmount) { value = totalAmount() }
            addSource(checkedCoupon) { value = totalAmount() }
        }

    private val _paying: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val paying: SingleLiveData<Unit> = _paying

    init {
        loadOrderedCartItems()
        loadCoupons()
    }

    override fun checkCoupon(couponId: Long) {
        val checkedCoupon = findCheckedCoupon(couponId)
        val currentList = getCoupons()
        currentList[currentList.indexOf(checkedCoupon.copy(isChecked = false))] =
            checkedCoupon.copy(isChecked = !checkedCoupon.isChecked)
        _coupons.value = currentList
    }

    fun paying() {
        viewModelScope.launch(coroutineExceptionHandler) {
            orderedProducts.value?.let {
                cartRepository.order(
                    it.map { cartWithProduct ->
                        cartWithProduct.id
                    },
                )
            }
        }
        _paying.setValue(Unit)
    }

    fun isCouponsEmpty(): Boolean {
        return coupons.value?.isEmpty() ?: true
    }

    private fun loadOrderedCartItems() {
        val cartWithProducts = mutableListOf<CartWithProduct>()
        viewModelScope.launch(coroutineExceptionHandler) {
            orderedCartItemIds.forEach {
                cartRepository.getCartItemByCartId(it).onSuccess { cartWithProduct ->
                    cartWithProducts.add(cartWithProduct)
                    orderedProducts.value = cartWithProducts
                }
            }
        }
    }

    private fun getCoupons() = requireNotNull(_coupons.value?.map { it.copy(isChecked = false) }?.toMutableList())

    private fun findCheckedCoupon(couponId: Long): CouponUiModel {
        return requireNotNull(_coupons.value?.find { it.couponState.coupon.id == couponId })
    }

    private fun totalAmount(): Int {
        val orderAmount = orderAmount.value ?: 0
        val couponDiscount = checkedCoupon.value?.couponState?.discountAmount() ?: 0
        return orderAmount + DELIVERY_AMOUNT - couponDiscount
    }

    private fun availableCoupons(): List<CouponUiModel> {
        val orderAmount = orderedProducts.value?.sumOf { it.product.price * it.quantity.value } ?: 0
        val coupons =
            _coupons.value?.map { couponUiModel ->
                updateCouponUiModel(
                    couponUiModel.couponState,
                    orderAmount,
                ).copy(isChecked = couponUiModel.isChecked)
            } ?: emptyList()

        return coupons.filter { it.couponState.isValid() }
    }

    private fun updateCouponUiModel(
        couponState: CouponState,
        orderAmount: Int,
    ) = when (couponState) {
        is Fixed5000 -> couponState.copy(orderAmount = orderAmount).toUiModel()
        is Freeshipping -> couponState.copy(orderAmount = orderAmount).toUiModel()
        is MiracleSale -> couponState.copy(orderAmount = orderAmount).toUiModel()
        is Bogo ->
            couponState.copy(orderedProduct = orderedProducts.value ?: emptyList())
                .toUiModel()

        else -> throw IllegalStateException()
    }

    private fun loadCoupons() {
        viewModelScope.launch(coroutineExceptionHandler) {
            couponRepository.getCoupons().onSuccess { couponStates ->
                _coupons.value = couponStates.map { it.toUiModel() }
            }
        }
    }

    companion object {
        const val DELIVERY_AMOUNT = 3000
    }
}
