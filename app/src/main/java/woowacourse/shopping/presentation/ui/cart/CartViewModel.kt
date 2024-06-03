package woowacourse.shopping.presentation.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
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

    val updateUiModel: UpdateUiModel = UpdateUiModel()

    private var _isAllChecked = MutableLiveData<Boolean>(true)
    val isAllChecked: LiveData<Boolean> get() = _isAllChecked

    fun findCartByOffset() {
        repository.getCartItems(offSet, 1000) { result ->
            result.onSuccess {
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
            }.onFailure {
                _errorHandler.value = EventState(CART_LOAD_ERROR)
            }
        }
    }

    override fun onDelete(cartProductUiModel: CartProductUiModel) {
        thread {
            updateUiModel.add(
                cartProductUiModel.cartProduct.productId,
                cartProductUiModel.cartProduct.copy(quantity = 0)
            )
            repository.deleteCartItem(cartProductUiModel.cartProduct.cartId.toInt()).onSuccess {
                val updatedData = (_carts.value as UiState.Success).data.toMutableList()
                updatedData.remove(cartProductUiModel)
                _carts.postValue(
                    UiState.Success(updatedData.toList()),
                )
            }.onFailure {
                _errorHandler.postValue(EventState(CART_DELETE_ERROR))
            }
        }
    }

    fun postCheckedItems() {
        val checkedIds = getCheckedIds()

        thread {
            repository.postOrders(
                OrderRequest(
                    checkedIds,
                ),
            ).onSuccess {
                val currentCarts = (_carts.value as UiState.Success).data
                val filteredCarts =
                    currentCarts.filterNot { it.cartProduct.cartId in checkedIds.map { it.toLong() } }

                val removedCarts =
                    currentCarts.filter { it.cartProduct.cartId in checkedIds.map { it.toLong() } }
                removedCarts.forEach { cartProduct ->
                    updateUiModel.add(
                        cartProduct.cartProduct.productId,
                        cartProduct.cartProduct.copy(quantity = 0)
                    )
                }

                _carts.postValue(UiState.Success(filteredCarts))
                _eventHandler.postValue(EventState(CartEvent.Update))
            }.onFailure {
                _errorHandler.postValue(EventState("ERROR"))
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
        thread {
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
                }
                .onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        if (cartProduct.quantity == 1) return
        thread {
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
                }
                .onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
        }
    }

    companion object {
        const val CART_LOAD_ERROR = "LOAD ERROR"
        const val CART_DELETE_ERROR = "DELETE ERROR"
        const val PAGE_SIZE = 5
        const val PAGE_UPPER_BOUND = 4
    }
}