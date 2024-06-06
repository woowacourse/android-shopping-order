package woowacourse.shopping.presentation.ui.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.CartViewModel

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

    fun loadProductByOffset() = viewModelScope.launch {
        repository.getProductsByPaging().onSuccess { it ->
            if (it == null) {
                _errorHandler.value = EventState(LOAD_ERROR)
            } else {
                if (_products.value is UiState.Loading) {
                    _products.value = UiState.Success(it)
                } else {
                    _products.value =
                        UiState.Success((_products.value as UiState.Success).data + it)
                }

            }
        }.onFailure { _errorHandler.value = EventState(LOAD_ERROR) }
    }

    fun loadCartByOffset() = viewModelScope.launch {
        repository.getCartItems(0, 2000)
            .onSuccess {
                if (it == null) {
                    _errorHandler.value = EventState(LOAD_ERROR)
                } else {
                    _carts.value = UiState.Success(it)
                }
            }
            .onFailure { _errorHandler.value = EventState(CartViewModel.CART_LOAD_ERROR) }
    }

    fun getCartItemCounts() = viewModelScope.launch {
        repository.getCartItemsCounts()
            .onSuccess { _cartCount.value = it }
            .onFailure { _errorHandler.value = EventState(LOAD_ERROR) }
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
        viewModelScope.launch {
            loadProductByOffset()
        }
    }

    override fun onPlus(cartProduct: CartProduct) {
        viewModelScope.launch {
            val cartProducts = (cartProducts.value as UiState.Success).data.map { it.copy() }
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
                        this@ShoppingViewModel.cartProducts.value = (UiState.Success(cartProducts))
                        _cartCount.value = _cartCount.value?.plus(1)
                    }
                    .onFailure {
                        _errorHandler.value = EventState("아이템 증가 오류")
                    }
            } else {
                repository.updateCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        this@ShoppingViewModel.cartProducts.value = (UiState.Success(cartProducts))
                        _cartCount.value = _cartCount.value?.plus(1)
                    }
                    .onFailure {
                        _errorHandler.value = EventState("아이템 증가 오류")
                    }
            }
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        viewModelScope.launch {
            val cartProducts = (cartProducts.value as UiState.Success).data.map { it.copy() }
            val index = cartProducts.indexOfFirst { it.productId == cartProduct.productId }
            cartProducts[index].minusQuantity()

            if (cartProducts[index].quantity > 0) {
                repository.updateCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity),
                )
                    .onSuccess {
                        this@ShoppingViewModel.cartProducts.value = (UiState.Success(cartProducts))
                        _cartCount.value = _cartCount.value?.minus(1)
                    }
                    .onFailure {
                        _errorHandler.value = EventState("아이템 증가 오류")
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    this@ShoppingViewModel.cartProducts.value = (UiState.Success(cartProducts))
                    _cartCount.value = _cartCount.value?.minus(1)
                }.onFailure {
                    _errorHandler.value = EventState("아이템 증가 오류")
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

    fun findAllRecent() = viewModelScope.launch(Dispatchers.IO) {
        repository.findByLimit(10).onSuccess {
            withContext(Dispatchers.Main) {
                _recentProducts.value = UiState.Success(it)
            }
        }.onFailure {
            withContext(Dispatchers.Main) {
                _errorHandler.value = (EventState("최근 아이템 로드 에러"))
            }
        }
    }

    companion object {
        const val LOAD_ERROR = "아이템을 끝까지 불러왔습니다"
    }
}
