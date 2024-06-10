package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.cart.model.CartProductUiModel
import woowacourse.shopping.presentation.ui.cart.model.CartNavigation
import woowacourse.shopping.presentation.ui.cart.model.CartUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel

class CartViewModel(
    private val cartItemRepository: CartItemRepository,
) : BaseViewModel(), CartActionHandler {

    private var _uiState = MutableLiveData<CartUiState>(CartUiState())
    val uiState: LiveData<CartUiState> get() = _uiState

    private val _navigateHandler = MutableLiveData<EventState<CartNavigation>>()
    val navigateHandler: LiveData<EventState<CartNavigation>> get() = _navigateHandler

    val updateUiModel: UpdateUiModel = UpdateUiModel()

    private var _isAllChecked = MutableLiveData<Boolean>(true)
    val isAllChecked: LiveData<Boolean> get() = _isAllChecked

    fun findCartByOffset() =
        viewModelScope.launch {
            delay(500L) // skeleton
            cartItemRepository.getAllByPaging(0, MAXIMUM_CART_SIZE).onSuccess {
                val currentState = _uiState.value ?: return@launch

                _uiState.postValue(
                    currentState.copy(
                        cartProductUiModels = it.map { cartProduct -> CartProductUiModel(cartProduct) },
                        isLoading = false
                    )
                )
            }.onFailure {
                showError(ErrorType.ERROR_CART_LOAD)
            }
        }

    override fun onDelete(cartProductUiModel: CartProductUiModel) =
        viewModelScope.launch {
            updateUiModel.add(
                cartProductUiModel.cartProduct.productId,
                cartProductUiModel.cartProduct.copy(quantity = 0),
            )
            cartItemRepository.delete(cartProductUiModel.cartProduct.cartId.toInt()).onSuccess {
                val currentCartProductUiModels = _uiState.value?.cartProductUiModels?.toMutableList() ?: return@launch
                currentCartProductUiModels.remove(cartProductUiModel)

                val currentState = _uiState.value ?: return@launch

                _uiState.postValue(
                    currentState.copy(
                        cartProductUiModels = currentCartProductUiModels.toList()
                    )
                )
            }.onFailure {
                showError(ErrorType.ERROR_CART_DELETE)
            }
        }

    fun postCheckedItems() {
        _navigateHandler.value = EventState(CartNavigation.ToPayment(getPaymentUiModel()))
    }

    private fun getPaymentUiModel(): PaymentUiModel {
        return PaymentUiModel(
            cartProducts = _uiState.value?.cartProductUiModels?.filter { it.isChecked }?.map { it.cartProduct } ?: emptyList(),
        )
    }

    override fun onCheck(
        cartProduct: CartProductUiModel,
        isChecked: Boolean,
    ) {
        var currentState = _uiState.value ?: return
        val updatedUiModel = currentState.cartProductUiModels.map {
            if (it.cartProduct.productId == cartProduct.cartProduct.productId) {
                cartProduct.copy(isChecked = isChecked)
            } else {
                it
            }
        }
        _uiState.value = currentState.copy(
            cartProductUiModels = updatedUiModel
        )
        currentState = _uiState.value ?: return

        if (!isChecked) _isAllChecked.value = false
        if (currentState.cartProductUiModels.all { it.isChecked }) _isAllChecked.value = true
    }

    override fun onCheckAll() {
        val selectAll = isAllChecked.value ?: false

        val currentState = _uiState.value ?: return

        _uiState.value = currentState.copy(
            cartProductUiModels = currentState.cartProductUiModels.map {
                it.copy(isChecked = !selectAll)
            }
        )
        _isAllChecked.value = !selectAll
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentCartProducts = _uiState.value?.cartProductUiModels?.map { it.copy(cartProduct = it.cartProduct.copy()) } ?: return@launch
            val index =
                currentCartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            currentCartProducts[index].cartProduct.plusQuantity()

            updateUiModel.add(cartProduct.productId, currentCartProducts[index].cartProduct)

            cartItemRepository.patch(
                id = currentCartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    val currentState = _uiState.value ?: return@launch
                    _uiState.postValue(
                        currentState.copy(
                            cartProductUiModels = currentCartProducts
                        )
                    )
                }
                .onFailure {
                    showError(ErrorType.ERROR_PRODUCT_PLUS)
                }
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            if (cartProduct.quantity == 1) return@launch
            val currentCartProducts = _uiState.value?.cartProductUiModels?.map { it.copy(cartProduct = it.cartProduct.copy()) } ?: return@launch
            val index =
                currentCartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            currentCartProducts[index].cartProduct.minusQuantity()
            updateUiModel.add(cartProduct.productId, currentCartProducts[index].cartProduct)

            cartItemRepository.patch(
                id = currentCartProducts[index].cartProduct.cartId.toInt(),
                quantityRequestDto = QuantityRequest(quantity = currentCartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    val currentState = _uiState.value ?: return@launch
                    _uiState.postValue(
                        currentState.copy(
                            cartProductUiModels = currentCartProducts
                        )
                    )
                }
                .onFailure {
                    showError(ErrorType.ERROR_PRODUCT_PLUS)
                }
        }

    companion object {
        const val MAXIMUM_CART_SIZE = 1000
    }
}
