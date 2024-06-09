package woowacourse.shopping.presentation.ui.curation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.curation.model.NavigateUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

class CurationViewModel(
    private val repository: Repository,
) : ViewModel(), CurationActionHandler {
    private val _cartProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val cartProducts: LiveData<UiState<List<CartProduct>>> get() = _cartProducts

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _eventHandler = MutableLiveData<EventState<CurationEvent>>()
    val eventHandler: LiveData<EventState<CurationEvent>> get() = _eventHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateUiState: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    init {
        viewModelScope.launch {
            repository.getCuration().onSuccess {
                _cartProducts.postValue(UiState.Success(it))
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_CURATION_LOAD))
            }
        }
    }

    override fun order() {
        _navigateHandler.value = EventState(NavigateUiState.ToPayment(getPaymentUiModel()))
    }


    override fun onProductClick(cartProduct: CartProduct) {
    }


    private fun getPaymentUiModel(): PaymentUiModel {
        return PaymentUiModel(
            cartProducts = (_cartProducts.value as UiState.Success).data.filter { it.quantity > 0 }
        )
    }
    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].plusQuantity()

            if (cartProducts[index].quantity == FIRST_UPDATE) {
                repository.postCartItem(
                    CartItemRequest(
                        productId = cartProducts[index].productId.toInt(),
                        quantity = cartProducts[index].quantity,
                    ),
                )
                    .onSuccess {
                        cartProducts[index].cartId = it.toLong()
                        _cartProducts.postValue(UiState.Success(cartProducts))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            } else {
                repository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        _cartProducts.postValue(UiState.Success(cartProducts))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                repository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequestDto = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        _cartProducts.postValue(UiState.Success(cartProducts))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    _cartProducts.postValue(UiState.Success(cartProducts))
                }.onFailure {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                }
            }
        }

    companion object {
        const val FIRST_UPDATE = 1
    }
}
