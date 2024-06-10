package woowacourse.shopping.presentation.ui.curation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.usecase.CurationUseCase
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.ui.curation.model.CurationNavigation
import woowacourse.shopping.presentation.ui.curation.model.CurationUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiState

class CurationViewModel(
    private val cartItemRepository: CartItemRepository,
    private val curationUseCase: CurationUseCase,
) : BaseViewModel(), CurationActionHandler {

    private val _uiState = MutableLiveData<CurationUiState>(CurationUiState())
    val uiState: LiveData<CurationUiState> get() = _uiState

    private val _navigateHandler = MutableLiveData<EventState<CurationNavigation>>()
    val navigateHandler: LiveData<EventState<CurationNavigation>> get() = _navigateHandler

    init {
        viewModelScope.launch {
            curationUseCase(10).onSuccess {
                val currentState = _uiState.value ?: return@launch
                _uiState.postValue(
                    currentState.copy(
                        cartProducts = it,
                        isLoading = false
                    ),
                )
            }.onFailure {
                showError(ErrorType.ERROR_CURATION_LOAD)
            }
        }
    }

    override fun order() {
        _navigateHandler.value = EventState(CurationNavigation.ToPayment(getPaymentUiModel()))
    }

    override fun onProductClick(cartProduct: CartProduct) {
        // 상세 상품으로 갈 수 없음
    }

    private fun getPaymentUiModel(): PaymentUiState {
        return PaymentUiState(
            cartProducts = _uiState.value?.cartProducts?.filter { it.quantity > 0 } ?: emptyList(),
        )
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentCartProducts = _uiState.value?.cartProducts?.map { it.copy() } ?: return@launch
            val index = currentCartProducts.indexOfFirst { it.productId == cartProduct.productId }
            currentCartProducts[index].plusQuantity()

            if (currentCartProducts[index].quantity == FIRST_UPDATE) {
                cartItemRepository.post(
                    CartItemRequest(
                        productId = currentCartProducts[index].productId.toInt(),
                        quantity = currentCartProducts[index].quantity,
                    ),
                )
                    .onSuccess {
                        currentCartProducts[index].cartId = it.toLong()
                        val currentState = _uiState.value ?: return@launch

                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts
                            )
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS)
                    }
            } else {
                cartItemRepository.patch(
                    id = currentCartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].quantity),
                )
                    .onSuccess {

                        val currentState = _uiState.value ?: return@launch
                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts
                            )
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS)
                    }
            }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentCartProducts = _uiState.value?.cartProducts?.map { it.copy() } ?: return@launch

            val index = currentCartProducts.indexOfFirst { it.productId == cartProduct.productId }
            currentCartProducts[index].minusQuantity()

            if (currentCartProducts[index].quantity > 0) {
                cartItemRepository.patch(
                    id = currentCartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].quantity),
                )
                    .onSuccess {
                        val currentState = _uiState.value ?: return@launch
                        _uiState.postValue(
                            currentState.copy(
                                cartProducts = currentCartProducts
                            )
                        )
                    }
                    .onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS)
                    }
            } else {
                cartItemRepository.delete(cartProduct.cartId.toInt()).onSuccess {
                    val currentState = _uiState.value ?: return@launch
                    _uiState.postValue(
                        currentState.copy(
                            cartProducts = currentCartProducts
                        )
                    )
                }.onFailure {
                    showError(ErrorType.ERROR_PRODUCT_PLUS)
                }
            }
        }

    companion object {
        const val FIRST_UPDATE = 1
    }
}
