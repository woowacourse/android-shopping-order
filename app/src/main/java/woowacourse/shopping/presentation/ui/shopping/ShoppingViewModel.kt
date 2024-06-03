package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import kotlin.concurrent.thread

class ShoppingViewModel(private val repository: Repository) :
    ViewModel(), ShoppingActionHandler {

    private val _cartCount = MutableLiveData<Int>(0)
    val cartCount: LiveData<Int> get() = _cartCount

    private val _recentProducts = MutableLiveData<UiState<List<RecentProduct>>>(UiState.Loading)
    val recentProducts: LiveData<UiState<List<RecentProduct>>> get() = _recentProducts

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    private val _products = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val products: LiveData<UiState<List<CartProduct>>> get() = _products

    private val _carts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val carts: LiveData<UiState<List<CartProduct>>> get() = _carts

    val cartProducts = MediatorLiveData<UiState<List<CartProduct>>>(UiState.Loading)

    init {
        cartProducts.addSource(_products) {
            combineCartProducts()
        }
        cartProducts.addSource(_carts) {
            combineCartProducts()
        }
        getCartItemCounts()
    }

    private fun combineCartProducts() {
        if (_products.value is UiState.Success && _carts.value is UiState.Success) {
            val products = (_products.value as UiState.Success).data
            val carts = (_carts.value as UiState.Success).data

            // cartProducts 리스트 생성
            val cartProducts =
                products.map { product ->
                    val cartItem = carts.find { it.productId == product.productId }
                    if (cartItem != null) {
                        product.copy(quantity = cartItem.quantity, cartId = cartItem.cartId)
                    } else {
                        product
                    }
                }
            this.cartProducts.value = UiState.Success(cartProducts)
        }
    }

    fun loadProductByOffset() {
        thread {
            repository.getProductsByPaging().onSuccess {
                if (it == null) {
                    _errorHandler.postValue(EventState(LOAD_ERROR))
                } else {
                    if (_products.value is UiState.Loading) {
                        _products.postValue(UiState.Success(it))
                    } else {
                        _products.postValue(
                            UiState.Success((_products.value as UiState.Success).data + it),
                        )
                    }
                }
            }.onFailure {
                _errorHandler.postValue(EventState(LOAD_ERROR))
            }
        }
    }

    fun loadCartByOffset() {
        thread {
            repository.getCartItems(0, 2000).onSuccess {
                if (it == null) {
                    _errorHandler.postValue(EventState(LOAD_ERROR))
                } else {
                    _carts.postValue(UiState.Success(it))
                }
            }.onFailure {
                _errorHandler.value = EventState(CartViewModel.CART_LOAD_ERROR)
            }
        }
    }

    fun getCartItemCounts() {
        thread {
            repository.getCartItemsCounts().onSuccess { maxCount ->
                _cartCount.postValue(maxCount)
            }.onFailure {
                _errorHandler.postValue(EventState(LOAD_ERROR))
            }
        }
    }

    companion object {
        const val LOAD_ERROR = "아이템을 끝까지 불러왔습니다"
    }

    override fun onProductClick(cartProduct: CartProduct) {
        _navigateHandler.value = EventState(NavigateUiState.ToDetail(cartProduct))
    }

    override fun onRecentProductClick(recentProduct: RecentProduct) {
        _navigateHandler.value =
            EventState(
                NavigateUiState.ToDetail(
                    recentProduct.toCartProduct(),
                ),
            )
    }

    override fun onCartClick() {
        _navigateHandler.value = EventState(NavigateUiState.ToCart)
    }

    override fun loadMore() {
        loadProductByOffset()
    }

    override fun onPlus(cartProduct: CartProduct) {
        thread {
            val cartProducts = (this.cartProducts.value as UiState.Success).data.map { it.copy() }
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
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        _cartCount.postValue(_cartCount.value?.plus(1))
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
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        _cartCount.postValue(_cartCount.value?.plus(1))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        thread {
            val cartProducts = (this.cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                repository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        _cartCount.postValue(_cartCount.value?.minus(1))
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    this.cartProducts.postValue(UiState.Success(cartProducts))
                    _cartCount.postValue(_cartCount.value?.minus(1))
                }.onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
            }
        }
    }

    fun syncProduct() {
        viewModelScope.launch {
            val loadCartJob = launch { loadCartByOffset() }
            loadCartJob.job
        }
    }

    fun findAllRecent() {
        thread {
            repository.findByLimit(10).onSuccess {
                _recentProducts.postValue(UiState.Success(it))
            }.onFailure {
                _errorHandler.postValue(EventState("최근 아이템 로드 에러"))
            }
        }
    }
}
