package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.shopping.NavigateUiState
import woowacourse.shopping.presentation.ui.shopping.ShoppingViewModel
import kotlin.concurrent.thread

class CartViewModel(private val repository: Repository) : ViewModel(), CartActionHandler {
    var maxOffset: Int = 0
        private set

    var offSet: Int = 0
        private set
        get() = field.coerceAtMost(maxOffset)
    private val _carts = MutableLiveData<UiState<List<CartProductUiModel>>>(UiState.Loading)

    val carts: LiveData<UiState<List<CartProductUiModel>>> get() = _carts

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler

    private val _eventHandler = MutableLiveData<EventState<CartEvent>>()
    val eventHandler: LiveData<EventState<CartEvent>> get() = _eventHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    val updateUiModel: UpdateUiModel = UpdateUiModel()

    private var _isAllChecked = MutableLiveData<Boolean>(true)
    val isAllChecked: LiveData<Boolean> get() = _isAllChecked

    private var _orderItems = listOf<CartProductUiModel>()
    val orderItems: List<CartProductUiModel> get() = _orderItems

    fun findCartByOffset() {
        viewModelScope.launch {
            repository.getCartItems(offSet, 1000)
                .onSuccess {
                    if (it == null) {
                        _errorHandler.postValue(EventState(ShoppingViewModel.LOAD_ERROR))
                    } else {
                        _carts.postValue(
                            UiState.Success(
                                it.map { cartProduct ->
                                    CartProductUiModel(
                                        cartProduct,
                                    )
                                },
                            ),
                        )
                    }
                }
                .onFailure {
                    _errorHandler.value = EventState(CART_LOAD_ERROR)
                }
        }
    }

    override fun onDelete(cartProductUiModel: CartProductUiModel) {
        viewModelScope.launch {
            updateUiModel.add(
                cartProductUiModel.cartProduct.productId,
                cartProductUiModel.cartProduct.copy(quantity = 0),
            )
            repository.deleteCartItem(cartProductUiModel.cartProduct.cartId.toInt())
                .onSuccess {
                    val updatedData = (_carts.value as UiState.Success).data.toMutableList()
                    updatedData.remove(cartProductUiModel)
                    _carts.postValue(
                        UiState.Success(updatedData.toList()),
                    )
                    updateRecentProduct(cartProductUiModel.cartProduct.productId, 0, 0)
                }
                .onFailure {
                    _errorHandler.postValue(EventState(CART_DELETE_ERROR))
                }
        }
    }

    private fun getCheckedIds(): List<Int> {
        return (_carts.value as UiState.Success).data.filter { it.isChecked }
            .map { it.cartProduct.cartId.toInt() }
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

    override fun onOrderClick(orderItems: List<CartProductUiModel>) {
        val checkedIds = getCheckedIds()
        val currentCarts = (_carts.value as UiState.Success).data
        val filteredCarts =
            currentCarts.filter { it.cartProduct.cartId in checkedIds.map { it.toLong() } }
        _orderItems = filteredCarts.map { it.copy() }
        _navigateHandler.value = EventState(NavigateUiState.ToOrder(checkedIds))
    }

    override fun onNext() {
        if (offSet == maxOffset) return
        offSet++
        findCartByOffset()
    }

    override fun onPrevious() {
        if (offSet == 0) return
        offSet--
        findCartByOffset()
    }

    override fun onPlus(cartProduct: CartProduct) {
        viewModelScope.launch {
            val cartProducts =
                (_carts.value as UiState.Success).data.map { it.copy(cartProduct = it.cartProduct.copy()) }
            val index =
                cartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            cartProducts[index].cartProduct.plusQuantity()

            updateUiModel.add(cartProduct.productId, cartProducts[index].cartProduct)

            repository.patchCartItem(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequest = QuantityRequest(quantity = cartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    _carts.postValue(UiState.Success(cartProducts))
                    updateRecentProduct(
                        cartProducts[index].cartProduct.productId,
                        cartProducts[index].cartProduct.quantity,
                        cartProducts[index].cartProduct.cartId,
                    )
                }
                .onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        if (cartProduct.quantity == 1) return
        viewModelScope.launch {
            val cartProducts =
                (_carts.value as UiState.Success).data.map { it.copy(cartProduct = it.cartProduct.copy()) }
            val index =
                cartProducts.indexOfFirst { it.cartProduct.productId == cartProduct.productId }
            cartProducts[index].cartProduct.minusQuantity()
            updateUiModel.add(cartProduct.productId, cartProducts[index].cartProduct)

            repository.patchCartItem(
                id = cartProducts[index].cartProduct.cartId.toInt(),
                quantityRequest = QuantityRequest(quantity = cartProducts[index].cartProduct.quantity),
            )
                .onSuccess {
                    _carts.postValue(UiState.Success(cartProducts))
                    updateRecentProduct(
                        cartProducts[index].cartProduct.productId,
                        cartProducts[index].cartProduct.quantity,
                        cartProducts[index].cartProduct.cartId,
                    )
                }
                .onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
        }
    }

    private fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecentProduct(productId, quantity, cartId)
        }
    }

    companion object {
        const val CART_LOAD_ERROR = "LOAD ERROR"
        const val CART_DELETE_ERROR = "DELETE ERROR"
    }
}
