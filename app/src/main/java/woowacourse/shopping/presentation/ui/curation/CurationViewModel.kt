package woowacourse.shopping.presentation.ui.curation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import kotlin.concurrent.thread

class CurationViewModel(
    private val repository: Repository,
) : ViewModel(), CurationActionHandler {
    private val _cartProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val cartProducts: LiveData<UiState<List<CartProduct>>> get() = _cartProducts

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _eventHandler = MutableLiveData<EventState<CurationEvent>>()
    val eventHandler: LiveData<EventState<CurationEvent>> get() = _eventHandler

    init {
        thread {
            repository.getCuration().onSuccess {
                _cartProducts.postValue(UiState.Success(it))
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_CURATION_LOAD))
            }
        }
    }

    override fun order() {
        thread {
            val orderCartIds = getOrderCartIds()
            repository.postOrders(
                OrderRequest(
                    orderCartIds,
                ),
            ).onSuccess {
                val cartProducts =
                    (this.cartProducts.value as UiState.Success).data.map { it.copy() }

                orderCartIds.forEach { id ->
                    cartProducts.find { it.cartId == id.toLong() }?.quantity = 0
                }
                _eventHandler.postValue(EventState(CurationEvent.SuccessOrder))
                _cartProducts.postValue(UiState.Success(cartProducts))
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_ORDER))
            }
        }
    }

    override fun onProductClick(cartProduct: CartProduct) {
    }

    private fun getOrderCartIds() =
        (_cartProducts.value as UiState.Success).data.filter {
            it.quantity > 0
        }.map {
            it.cartId.toInt()
        }

    override fun onPlus(cartProduct: CartProduct) {
        thread {
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
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        _cartProducts.postValue(UiState.Success(cartProducts))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                    }
            }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        thread {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                repository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
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
    }

    companion object {
        const val FIRST_UPDATE = 1
    }
}
