package woowacourse.shopping.presentation.ui.curation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState

class CurationViewModel(
    private val repository: Repository,
    private val ids: List<Long>,
) : ViewModel(), CurationActionHandler {
    private val _cartProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val cartProducts: LiveData<UiState<List<CartProduct>>> get() = _cartProducts

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler

    private val _eventHandler = MutableLiveData<EventState<CurationEvent>>()
    val eventHandler: LiveData<EventState<CurationEvent>> get() = _eventHandler

    private val _orderProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val orderProducts: LiveData<UiState<List<CartProduct>>> get() = _orderProducts

    init {
        viewModelScope.launch {
            repository.getCuration()
                .onSuccess {
                    _cartProducts.value = UiState.Success(it ?: emptyList())
                    orderProducts()
                }
                .onFailure {
                    _errorHandler.value = EventState(LOAD_ERROR)
                }
        }
    }

    private suspend fun orderProducts() {
        repository.getCartItems(0, 1000)
            .onSuccess {
                if (it == null) {
                    _errorHandler.value = EventState(LOAD_ERROR)
                } else {
                    val filteredCartItems =
                        it.filter { cartProduct -> ids.contains(cartProduct.cartId) }
                    _orderProducts.value = UiState.Success(filteredCartItems)
                }
            }
            .onFailure {
                _errorHandler.value = EventState(LOAD_ERROR)
            }
    }

    override fun onOrderClick() {
        _eventHandler.value = (EventState(CurationEvent.SuccessOrder))
    }

    override fun onProductClick(cartProduct: CartProduct) {
        // 인터페이스 분리 원칙 필요
    }

    override fun onRecentProductClick(recentProduct: RecentProduct) {
        // 인터페이스 분리 원칙 필요
    }

    override fun onCartClick() {
        // 인터페이스 분리 원칙 필요
    }

    override fun loadMore() {
        // 인터페이스 분리 원칙 필요
    }

    override fun onPlus(cartProduct: CartProduct) {
        viewModelScope.launch {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].plusQuantity()

            if (cartProducts[index].quantity == 1) {
                repository.postCartItem(
                    CartItemRequest(
                        productId = cartProducts[index].productId.toInt(),
                        quantity = cartProducts[index].quantity,
                    ),
                ).onSuccess {
                    cartProducts[index].cartId = it.toLong()
                    _cartProducts.value = UiState.Success(cartProducts)
                    _orderProducts.value =
                        UiState.Success((_orderProducts.value as UiState.Success).data + cartProducts[index])
                }.onFailure {
                    _errorHandler.value = EventState("아이템 증가 오류")
                }
            } else {
                repository.updateCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                ).onSuccess {
                    _cartProducts.value = UiState.Success(cartProducts)
                    _orderProducts.value = UiState.Success(
                        (_orderProducts.value as UiState.Success).data.map {
                            if (it.productId == cartProduct.productId) {
                                it.copy(quantity = cartProducts[index].quantity)
                            } else {
                                it
                            }
                        }
                    )
                }.onFailure {
                    _errorHandler.value = EventState("아이템 증가 오류")
                }
            }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        viewModelScope.launch {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                repository.updateCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        _cartProducts.value = UiState.Success(cartProducts)
                        _orderProducts.value = UiState.Success(
                            (_orderProducts.value as UiState.Success).data.map {
                                if (it.productId == cartProduct.productId) {
                                    it.copy(quantity = cartProducts[index].quantity)
                                } else {
                                    it
                                }
                            }
                        )
                    }
                    .onFailure {
                        _errorHandler.value = EventState("아이템 감소 오류")
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    _cartProducts.value = UiState.Success(cartProducts)
                    _orderProducts.value =
                        UiState.Success((_orderProducts.value as UiState.Success).data - cartProduct)
                }.onFailure {
                    _errorHandler.value = EventState("아이템 감소 오류")
                }
            }
        }
    }

    companion object {
        const val LOAD_ERROR = "큐레이션 로드 에러입니다"
    }
}
