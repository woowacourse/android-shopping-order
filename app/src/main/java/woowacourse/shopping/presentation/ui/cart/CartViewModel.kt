package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.common.UiState
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.cart.model.CartEvent
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel
import woowacourse.shopping.presentation.ui.cart.model.NavigateUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

class CartViewModel(
    private val cartItemRepository: CartItemRepository,
) : ViewModel(), CartActionHandler {
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
            cartItemRepository.getAllByPaging(0, 1000).onSuccess {
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
            cartItemRepository.delete(cartProductUiModel.cartProduct.cartId.toInt()).onSuccess {
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
    }

    private fun getPaymentUiModel(): PaymentUiModel {
        return PaymentUiModel(
            cartProducts = (_carts.value as UiState.Success).data.filter { it.isChecked }.map { it.cartProduct },
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

            cartItemRepository.patch(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequest(quantity = cartProducts[index].cartProduct.quantity),
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

            cartItemRepository.patch(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequest(quantity = cartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    _carts.postValue(UiState.Success(cartProducts))
                }
                .onFailure {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                }
        }
}
