package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel
import woowacourse.shopping.presentation.ui.payment.model.PaymentNavigation
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState
import woowacourse.shopping.presentation.ui.payment.model.toUiModel

class PaymentViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val recentProductRepository: RecentProductRepository,
) : BaseViewModel(), PaymentActionHandler {
    private val _uiState = MutableLiveData<PaymentUiState>()
    val uiState: LiveData<PaymentUiState> get() = _uiState

    private val _navigateHandler = MutableLiveData<EventState<PaymentNavigation>>()
    val navigateHandler: LiveData<EventState<PaymentNavigation>> get() = _navigateHandler

    private val updateUiModel: UpdateUiModel = UpdateUiModel()

    fun setPaymentUiModel(paymentUiModel: PaymentUiState) {
        _uiState.value = paymentUiModel
    }

    override fun pay() =
        viewModelScope.launch {
            val currentState = _uiState.value ?: return@launch

            currentState.cartProducts.forEach {
                updateRecentProduct(it)
                updateUiModel.add(it.productId, it.copy(quantity = 0))
            }

            orderRepository.post(
                OrderRequest(
                    currentState.cartProductIds,
                ),
            ).onSuccess {
                _navigateHandler.postValue(EventState(PaymentNavigation.ToShopping(updateUiModel)))
            }.onFailure {
                showError(ErrorType.ERROR_ORDER)
            }
        }

    private fun updateRecentProduct(cartProduct: CartProduct) =
        viewModelScope.launch {
            recentProductRepository.save(cartProduct.toRecentProduct().copy(quantity = 0)).onFailure {
                showError(ErrorType.ERROR_RECENT_INSERT)
            }
        }

    override fun checkCoupon(couponUiModel: CouponUiModel) {
        val currentState = _uiState.value ?: return

        currentState.couponUiModels.map {
            if (it.coupon.id == couponUiModel.coupon.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it.copy(isChecked = false)
            }
        }.also {
            _uiState.value =
                currentState.copy(
                    couponUiModels = it,
                )
        }
    }

    fun loadCoupons() =
        viewModelScope.launch {
            couponRepository.getAll()
                .onSuccess {
                    val currentState = _uiState.value ?: return@launch
                    _uiState.postValue(
                        currentState.copy(
                            couponUiModels =
                                it.filter {
                                    it.isValid(cartProducts = _uiState.value!!.cartProducts)
                                }.map { it.toUiModel() },
                        ),
                    )
                }
                .onFailure {
                    showError(ErrorType.ERROR_COUPON_LOAD)
                }
        }
}
