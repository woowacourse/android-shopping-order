package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.data.remote.dto.request.QuantityRequestDto
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.cart.model.CartEvent
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel
import woowacourse.shopping.presentation.ui.cart.model.NavigateUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

class CartViewModel(private val repository: Repository) : ViewModel(), CartActionHandler {
    private val _carts = MutableLiveData<UiState<List<CartProductUiModel>>>(UiState.Loading)

    val carts: LiveData<UiState<List<CartProductUiModel>>> get() = _carts

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _eventHandler = MutableLiveData<EventState<CartEvent>>()
    val eventHandler: LiveData<EventState<CartEvent>> get() = _eventHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    val updateUiModel: UpdateUiModel = UpdateUiModel()

    private var _isAllChecked = MutableLiveData<Boolean>(true)
    val isAllChecked: LiveData<Boolean> get() = _isAllChecked

    fun findCartByOffset() =
        viewModelScope.launch {
            repository.getCartItems(0, 1000).onSuccess {
                _carts.postValue(
                    UiState.Success(
                        it.map { cartProduct ->
                            CartProductUiModel(
                                cartProduct,
                            )
                        },
                    ),
                )
            }.onFailure {
                _errorHandler.value = EventState(ErrorType.ERROR_CART_LOAD)
            }
        }

    override fun onDelete(cartProductUiModel: CartProductUiModel) =
        viewModelScope.launch {
            updateUiModel.add(
                cartProductUiModel.cartProduct.productId,
                cartProductUiModel.cartProduct.copy(quantity = 0),
            )
            repository.deleteCartItem(cartProductUiModel.cartProduct.cartId.toInt()).onSuccess {
                val updatedData = (_carts.value as UiState.Success).data.toMutableList()
                updatedData.remove(cartProductUiModel)
                _carts.postValue(
                    UiState.Success(updatedData.toList()),
                )
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_CART_DELETE))
            }
        }

    fun postCheckedItems() {

        _navigateHandler.value = EventState(NavigateUiState.ToPayment(getPaymentUiModel()))

//        viewModelScope.launch {
//            val checkedIds = getCheckedIds()
//
//            repository.postOrders(
//                OrderRequestDto(
//                    checkedIds,
//                ),
//            ).onSuccess {
//                val currentCarts = (_carts.value as UiState.Success).data
//                val filteredCarts =
//                    currentCarts.filterNot { it.cartProduct.cartId in checkedIds.map { it.toLong() } }
//
//                val removedCarts =
//                    currentCarts.filter { it.cartProduct.cartId in checkedIds.map { it.toLong() } }
//                removedCarts.forEach { cartProduct ->
//                    updateUiModel.add(
//                        cartProduct.cartProduct.productId,
//                        cartProduct.cartProduct.copy(quantity = 0),
//                    )
//                }
//
//                _carts.postValue(UiState.Success(filteredCarts))
//                _eventHandler.postValue(EventState(CartEvent.Update))
//            }.onFailure {
//                _errorHandler.postValue(EventState(ErrorType.ERROR_ORDER))
//            }
//        }
    }

    private fun getPaymentUiModel(): PaymentUiModel {
        return PaymentUiModel(
            cartProducts = (_carts.value as UiState.Success).data.filter { it.isChecked }.map { it.cartProduct }
        )
    }


    private fun getCheckedIds(): List<Int> {
        return (_carts.value as UiState.Success).data.filter { it.isChecked }
            .map { it.cartProduct.cartId.toInt() }
    }

    private fun updateCarts(productId: Int) {
        val currentCartItems = (_carts.value as? UiState.Success)?.data
        val updatedCartItems =
            currentCartItems?.filterNot { it.cartProduct.productId.toInt() == productId }
        _carts.postValue(UiState.Success(updatedCartItems ?: emptyList()))
    }

    override fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    ) {
        _carts.value =
            UiState.Success(
                (_carts.value as UiState.Success).data.map {
                    if (it.cartProduct.productId == cartProduct.cartProduct.productId) {
                        cartProduct.copy(isChecked = isChecked)
                    } else {
                        it
                    }
                },
            )

        if (!isChecked) _isAllChecked.value = false
        if ((_carts.value as UiState.Success).data.all { it.isChecked }) _isAllChecked.value = true
    }

    override fun onCheckAll() {
        val selectAll = isAllChecked.value ?: false
        _carts.value =
            UiState.Success(
                (_carts.value as UiState.Success).data.map {
                    it.copy(isChecked = !selectAll)
                },
            )
        _isAllChecked.value = !selectAll
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val cartProducts =
                (_carts.value as UiState.Success).data.map { it.copy(cartProduct = it.cartProduct.copy()) }
            val index =
                cartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            cartProducts[index].cartProduct.plusQuantity()

            updateUiModel.add(cartProduct.productId, cartProducts[index].cartProduct)

            repository.patchCartItem(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequestDto(quantity = cartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    _carts.postValue(UiState.Success(cartProducts))
                }
                .onFailure {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            if (cartProduct.quantity == 1) return@launch

            val cartProducts =
                (_carts.value as UiState.Success).data.map { it.copy(cartProduct = it.cartProduct.copy()) }
            val index =
                cartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            cartProducts[index].cartProduct.minusQuantity()
            updateUiModel.add(cartProduct.productId, cartProducts[index].cartProduct)

            repository.patchCartItem(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequestDto(quantity = cartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    _carts.postValue(UiState.Success(cartProducts))
                }
                .onFailure {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                }
        }
}
