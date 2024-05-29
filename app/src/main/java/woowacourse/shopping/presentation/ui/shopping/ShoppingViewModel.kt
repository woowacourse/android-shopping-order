package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.cart.CartViewModel
import kotlin.concurrent.thread

class ShoppingViewModel(private val repository: Repository) :
    ViewModel(), ShoppingActionHandler {
    private var offSet: Int = 0

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
    }

    fun combineCartProducts() {
        // _products와 _carts가 성공 상태인지 확인
        if (_products.value is UiState.Success && _carts.value is UiState.Success) {
            // 성공 상태인 경우 데이터 가져오기
            val products = (_products.value as UiState.Success).data
            val carts = (_carts.value as UiState.Success).data

            // cartProducts 리스트 생성
            val cartProducts = products.map { product ->
                // carts에서 productId가 같은 항목을 찾음
                val cartItem = carts.find { it.productId == product.productId }
                if (cartItem != null) {
                    // product의 quantity를 cartItem의 quantity로 업데이트
                    product.copy(quantity = cartItem.quantity, cartId = cartItem.cartId)
                } else {
                    product
                }
            }

            // 업데이트된 cartProducts를 반영 (예: _combinedProducts MutableLiveData에 저장)
            this.cartProducts.value = UiState.Success(cartProducts)
        }
    }


    fun loadProductByOffset() {
        thread {
            repository.getProducts(offSet, PAGE_SIZE).onSuccess {
                if(it == null) {
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
                offSet++
            }.onFailure {
                _errorHandler.postValue(EventState(LOAD_ERROR))
            }

        }
    }

    fun loadCartByOffset() {
        thread {
            repository.getCartItems(offSet, 2000).onSuccess {
                if(it == null) {
                    _errorHandler.postValue(EventState(ShoppingViewModel.LOAD_ERROR))
                } else {
                    _carts.postValue(UiState.Success(it))
                }
            }.onFailure {
                _errorHandler.value = EventState(CartViewModel.CART_LOAD_ERROR)
            }
        }
    }

    fun getItemCount() {
        thread {
            repository.getMaxCartCount().onSuccess { maxCount ->
                _cartCount.postValue(maxCount)
            }
        }
    }

    companion object {
        const val LOAD_ERROR = "아이템을 끝까지 불러왔습니다"
        const val PAGE_SIZE = 20
    }

    override fun onProductClick(cartProduct: CartProduct) {
        _navigateHandler.value = EventState(NavigateUiState.ToDetail(cartProduct))
    }

    override fun onRecentProductClick(recentProduct: RecentProduct) {

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

            Log.d("CartId","${ cartProducts[index].cartId.toInt()}" )

            cartProducts[index].plusQuantity()

            if(cartProducts[index].quantity == 1) {
                repository.postCartItem(
                    CartItemRequest(
                        productId = cartProducts[index].productId.toInt(),
                        quantity = cartProducts[index].quantity
                    )
                )
                    .onSuccess {
                        cartProducts[index].cartId = it.toLong()
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        getItemCount()
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            } else {
                Log.d("CartId","${ cartProducts[index].cartId.toInt()}" )
                repository.patchCartItem(
                    id = cartProducts[index].cartId.toInt(),
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity)
                )
                    .onSuccess {
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        getItemCount()
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
                    quantityRequest = QuantityRequest(quantity = cartProducts[index].quantity)
                )
                    .onSuccess {
                        this.cartProducts.postValue(UiState.Success(cartProducts))
                        getItemCount()
                    }
                    .onFailure {
                        _errorHandler.postValue(EventState("아이템 증가 오류"))
                    }
            } else {
                repository.deleteCartItem(cartProduct.cartId.toInt()).onSuccess {
                    this.cartProducts.postValue(UiState.Success(cartProducts))
                    getItemCount()
                }.onFailure {
                    _errorHandler.postValue(EventState("아이템 증가 오류"))
                }
            }
        }
    }

    fun updateCartProducts(updateUiModel: UpdateUiModel) {
        val cartProducts = (this.cartProducts.value as UiState.Success).data.map { it.copy() }
        updateUiModel.updatedItems.forEach { updatedItem ->
            // 해당 productId를 갖는 제품을 찾아서 수량을 업데이트
            val cartProductToUpdate = cartProducts.find { it.productId == updatedItem.key }
            cartProductToUpdate?.quantity = updatedItem.value.quantity
        }
        this.cartProducts.value = UiState.Success(cartProducts)
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
