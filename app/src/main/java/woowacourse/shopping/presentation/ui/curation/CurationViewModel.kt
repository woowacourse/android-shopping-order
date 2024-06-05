package woowacourse.shopping.presentation.ui.curation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import kotlin.concurrent.thread

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

    private var _orderProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val orderProducts: LiveData<UiState<List<CartProduct>>> get() = _orderProducts

    init {
        thread {
            repository.getCuration { result ->
                if (result.isSuccess) {
                    _cartProducts.postValue(UiState.Success(result.getOrNull() ?: emptyList()))
                } else {
                    _errorHandler.postValue(EventState(LOAD_ERROR))
                }
            }
            repository.getCartItems(0, 1000) { result ->
                result.onSuccess {
                    if (it == null) {
                        _errorHandler.postValue(EventState(LOAD_ERROR))
                    } else {
                        val filteredCartItems =
                            it.filter { cartProduct -> ids.contains(cartProduct.cartId) }
                        _orderProducts.postValue(UiState.Success(filteredCartItems))
                    }
                }.onFailure {
                    _errorHandler.postValue(EventState(LOAD_ERROR))
                }
            }
        }
    }

    override fun order() {
        thread {
            var orderCartIds =
                (_cartProducts.value as UiState.Success).data.filter {
                    it.quantity > 0
                }.map {
                    it.cartId.toInt()
                }
            orderCartIds = orderCartIds + ids.map { it.toInt() }
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
            }
        }
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
        thread {
            val cartProducts = (_cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].plusQuantity()

            if (cartProducts[index].quantity == 1) {
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
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
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
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
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
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    _cartProducts.postValue(UiState.Success(cartProducts))
                }.onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
            }
        }
    }

    companion object {
        const val LOAD_ERROR = "큐레이션 로드 에러입니다"
    }
}
