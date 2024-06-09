package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.list.CartItemClickListener
import woowacourse.shopping.view.cart.list.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.cart.recommend.RecommendProductEventListener
import woowacourse.shopping.view.home.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.view.state.CartListUiEvent
import woowacourse.shopping.view.state.CartListUiState
import woowacourse.shopping.view.state.CartUiState
import woowacourse.shopping.view.state.RecommendListUiEvent
import woowacourse.shopping.view.state.RecommendListUiState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    CartItemClickListener,
    QuantityEventListener,
    RecommendProductEventListener {
    private val _currentScreen: MutableLiveData<CurrentScreen> = MutableLiveData(CurrentScreen.CART)
    val currentScreen: LiveData<CurrentScreen>
        get() = _currentScreen

    private val _cartUiState: MutableLiveData<CartUiState> = MutableLiveData(CartUiState())
    val cartUiState: LiveData<CartUiState>
        get() = _cartUiState

    private val _navigateBackToHome: MutableLiveData<Event<Unit>> = MutableLiveData()
    val navigateBackToHome: LiveData<Event<Unit>>
        get() = _navigateBackToHome

    private val _cartListUiState: MutableLiveData<CartListUiState> =
        MutableLiveData(CartListUiState())
    val cartListUiState: LiveData<CartListUiState>
        get() = _cartListUiState

    private val _cartListUiEvent: MutableLiveData<Event<CartListUiEvent>> = MutableLiveData()
    val cartListUiEvent: LiveData<Event<CartListUiEvent>>
        get() = _cartListUiEvent

    private val _recommendedListUiState: MutableLiveData<RecommendListUiState> =
        MutableLiveData(RecommendListUiState())
    val recommendedListUiState: LiveData<RecommendListUiState>
        get() = _recommendedListUiState

    private val _recommendListUiEvent: MutableLiveData<Event<RecommendListUiEvent>> =
        MutableLiveData()
    val recommendListUiEvent: LiveData<Event<RecommendListUiEvent>>
        get() = _recommendListUiEvent

    var alteredProductIds: Array<Int> = arrayOf()
        private set

    init {
        loadCartItems()
    }

    fun loadRecommendedItems() {
        viewModelScope.launch {
            val recommendedProducts = productRepository.getRecommendedProducts().getOrNull()
            val uiState = recommendedListUiState.value
            if (recommendedProducts == null || uiState == null) {
                return@launch
            }
            _recommendedListUiState.value =
                uiState.copy(
                    isLoading = false,
                    recommendedProducts = recommendedProducts.map {
                        ProductViewItem(it)
                    }
                )
        }
    }

    fun navigate() {
        when (currentScreen.value ?: return) {
            CurrentScreen.CART -> {
                _cartListUiEvent.value = Event(CartListUiEvent.NavigateToRecommendList)
                _currentScreen.value = CurrentScreen.RECOMMEND
            }

            CurrentScreen.RECOMMEND -> {
                _recommendListUiEvent.value = Event(
                    RecommendListUiEvent.NavigateToOrder(
                        cartUiState.value?.selectedCartItems ?: return
                    )
                )
            }
        }
    }

    fun navigateBackToHome() {
        _navigateBackToHome.value = Event(Unit)
    }

    override fun onCartItemClick(productId: Int) {
        viewModelScope.launch {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
            _cartListUiEvent.value =
                Event(
                    CartListUiEvent.NavigateToProductDetail(
                        productId = productId,
                        lastlyViewed = productId == lastlyViewedProduct,
                    ),
                )
        }
    }

    override fun onDeleteButtonClick(itemId: Int) {
        viewModelScope.launch {
            val result = cartRepository.deleteCartItem(itemId).getOrNull()
            val uiState = cartUiState.value
            if (result == null || uiState == null) {
                return@launch
            }
            _cartListUiState.value = cartListUiState.value?.copy(
                cartViewItems = cartListUiState.value?.cartViewItems?.filter {
                    alteredProductIds += it.cartItem.product.id
                    it.cartItem.cartItemId != itemId
                } ?: return@launch
            )

            _cartUiState.value = uiState.copy(
                selectedCartItems = uiState.selectedCartItems.filter { it.cartItemId != itemId }
            )
        }
    }

    override fun onSelectChanged(
        itemId: Int,
        isSelected: Boolean,
    ) {
        val cartViewItems = cartListUiState.value?.cartViewItems?.map {
            if (it.cartItem.cartItemId == itemId)
                it.copy(isSelected = isSelected) else it
        } ?: return
        _cartListUiState.value =
            cartListUiState.value?.copy(cartViewItems = cartViewItems)
        val cartItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == itemId }
                ?: return
        if (isSelected) {
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItems = cartUiState.value?.selectedCartItems?.plus(
                    cartItem.cartItem
                ) ?: return
            )
        } else {
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItems = cartUiState.value?.selectedCartItems?.filter {
                    it.cartItemId != cartItem.cartItem.cartItemId
                } ?: return
            )
        }
        setTotalPrice()
    }

    override fun navigateToDetail(productId: Int) {
        viewModelScope.launch {
            val lastlyViewedProduct = recentProductRepository.findMostRecentProduct()?.productId
            _recommendListUiEvent.value =
                Event(
                    RecommendListUiEvent.NavigateToProductDetail(
                        productId,
                        productId == lastlyViewedProduct,
                    ),
                )
        }
    }

    override fun navigateToCart() {
        _recommendListUiEvent.value = Event(RecommendListUiEvent.NavigateBackToCartList)
    }

    override fun addToCart(product: ProductItemDomain) {
        viewModelScope.launch {
            alteredProductIds += product.id
            val result = cartRepository.addCartItem(product.id, 1).getOrNull()
            val entireCartItems = cartRepository.getEntireCartItems().getOrNull()
            val uiState = recommendedListUiState.value
            if (result == null || uiState == null || entireCartItems == null) {
                return@launch
            }
            val changedItem =
                entireCartItems.firstOrNull { it.product.id == product.id }
            if (changedItem == null) {
                return@launch
            }
            val updatedProducts = uiState.recommendedProducts.map {
                if (it.orderableProduct.productItemDomain.id == changedItem.product.id) it.copy(
                    orderableProduct = it.orderableProduct.copy(
                        cartData = CartData(changedItem.cartItemId, changedItem.product.id, quantity = changedItem.quantity)
                    )
                ) else it
            }
            _recommendedListUiState.value =
                recommendedListUiState.value?.copy(
                    recommendedProducts = updatedProducts,
                )
            _cartUiState.value = cartUiState.value?.copy(
                selectedCartItems = cartUiState.value?.selectedCartItems?.plus(
                    changedItem
                ) ?: return@launch
            )
        }
    }

    override fun addQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem = cartViewItem.copy(cartItem = cartViewItem.cartItem.plusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    override fun subtractQuantity(cartItemId: Int) {
        val cartViewItem =
            cartListUiState.value?.cartViewItems?.firstOrNull { it.cartItem.cartItemId == cartItemId }
                ?: return
        val changedItem =
            cartViewItem.copy(cartItem = cartViewItem.cartItem.minusQuantity())
        updateCartQuantity(cartItemId, changedItem)
    }

    fun updateEntireCheck(isSelected: Boolean) {
        val updatedCartViewItems = cartListUiState.value?.cartViewItems?.map {
            it.copy(isSelected = isSelected)
        } ?: return
        _cartListUiState.value =
            cartListUiState.value?.copy(cartViewItems = updatedCartViewItems)
        _cartUiState.value =
            cartUiState.value?.copy(
                isEntireCheckboxSelected = isSelected,
                totalPrice =
                updatedCartViewItems.sumOf { if (it.isSelected) it.cartItem.totalPrice() else 0 },
            )
    }

    private fun loadCartItems() {
        viewModelScope.launch() {
            _cartListUiState.value = cartListUiState.value?.copy(isLoading = true)
            val cartListUiState = cartListUiState.value
            val cartUiState = cartUiState.value
            if (cartUiState == null || cartListUiState == null) {
                return@launch
            }
            cartRepository.getEntireCartItemsForCart().onSuccess { cartItems ->
                _cartListUiState.value =
                    cartListUiState.copy(
                        isLoading = false,
                        cartViewItems =
                        cartItems.cartItems.map { cartItem ->
                            CartViewItem(
                                cartItem,
                                cartUiState.isEntireCheckboxSelected || cartItem.cartItemId in (cartUiState.selectedCartItems.map { it.cartItemId }),
                            )
                        },
                    )
            }.onFailure {

            }
        }
    }

    private fun updateCartQuantity(
        cartItemId: Int,
        changedItem: CartViewItem,
    ) {
        viewModelScope.launch {
            alteredProductIds += changedItem.cartItem.product.id
            cartRepository.updateCartItem(
                cartItemId = cartItemId,
                quantity = changedItem.cartItem.quantity
            ).onSuccess {
                val updatedViewItems =
                    cartListUiState.value?.cartViewItems?.map {
                        if (it.cartItem.cartItemId == cartItemId) changedItem else it
                    } ?: return@launch
                _cartListUiState.value =
                    cartListUiState.value?.copy(
                        cartViewItems = updatedViewItems,
                    )
                setTotalPrice()
            }
        }
    }

    private fun setTotalPrice() {
        val totalPrice = cartListUiState.value?.cartViewItems?.sumOf {
            if (it.isSelected) it.cartItem.totalPrice() else 0
        } ?: return
        val isEntirelySelected =
            cartListUiState.value?.cartViewItems?.all { it.isSelected } ?: return
        _cartUiState.value = cartUiState.value?.copy(
            isEntireCheckboxSelected = isEntirelySelected,
            totalPrice = totalPrice
        )
    }
}

/**
 * 예외 처리
 * val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
 *     // setting(uiState...)
 * }
 * private val scope = (viewModelScope + coroutineExceptionHandler)
 * scope.launch { /* ... */ }
 * 차이? : 미리 만들어두고 사용 -> 매개변수에 넣어줄 필요 사라짐
 * viewModelScope.launch(coroutineExceptionHandler) {
 *
 * }
 */

/**
 * fun getAllCartProducts() {
 *  viewModel.launch(coroutineExceptionHandler) {
 *      launch {
 *          throw Exception()
 *      }
 *      에러를 잡을 수 있음. => structured concurrency의 이유.
 *      자식에서 예외가 발생했을 때, 부모에게까지 영향을 준다.(예외가 잡히면 하는 행동을 한 후, 부모도 종료됨)
 *  }
 * }
 */

/**
 * fun getAllCartProducts() {
 *  try {
 *    launch { throw RuntimeException("") } // 에러를 잡을 수 없음.
 *  } catch (e: Exception) {
 *
 *  }
 * }
 */

/**
 * fun getAllCartProducts() {
 *  viewModelScope.launch(coroutineExceptionHandler) {
 *      launch(coroutineExceptionHandler) { // ***이래도 전파를 못 막는다.***
 *          throw Exception()
 *      }
 *      자식에서 예외가 발생했을 때, 부모에게까지 영향을 준다.(예외가 잡히면 하는 행동을 한 후, 부모도 종료됨)
 *  }
 * }
 */

/**
 * fun getAllCartProducts() {
 *  viewModelScope.launch(coroutineExceptionHandler) {
 *      launch(coroutineExceptionHandler2) { // ***이래도 전파가 되는가?!***
 *          throw Exception()
 *      }
 *      자식에서 예외가 발생했을 때, 부모에게까지 영향을 준다.(예외가 잡히면 하는 행동을 한 후, 부모도 종료됨)
 *  }
 * }
 */

/** 전파를 막는 방법 : supervisorScope : 자식의 에러가 부모 코루틴으로 전파되지 않는다!
 * fun getAllCartProducts() {
 *  viewModel.launch(coroutineExceptionHandler) {
 *      supervisorScope {
 *          // ...
 *      }
 *  }
 * }
 */

/** retrofit을 활용한 예외 핸들링 방법
 * suspend fun getAllCArtProducts(): Result<> {
 *  val response = service.getCartItems()
 *  return if (response.isSuccessful) {
 *      Result.success(response.body() ?: emptyList())
 *  } else {
 *      Result.failure(RuntimeException("responseCode : ${response.code}"))
 *      // 예외를 터뜨리지 않고 result 객체로 감싸면, 동작 중 예외가 발생할 위험을 막을 수 있음.
 *  }
 * }
 */

/** retrofit을 활용한 예외 핸들링 방법
 * sealed interface Result<T> {
 *      data class Success<T>(data: T)
 *      data object NotFount
 *      data object UnAuthorized
 * }
 * suspend fun getAllCArtProducts(): Result<T> {
 *  val response = service.getCartItems()
 *  return if (response.isSuccessful) {
 *      Result.Success(response.body() ?: emptyList())
 *  } else if (response.code() == 404) {
 *      Result.Notfound
 *  } else if (response.code() == 401) {
 *      Result.UnAuthorized
 *  }
 * }
 */

/**
 * suspend fun getAllCart
 */

//suspend fun main() {
//    var a = 1
//    val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
//        println("t! : $t, a : $a")
//        a = 2
//    }
//    val coroutineExceptionHandler2 = CoroutineExceptionHandler { _, t ->
//        println("t2! : $t, a : $a")
//        a = 3
//    }
//    coroutineScope {
//        launch(coroutineExceptionHandler) {
//            launch(coroutineExceptionHandler) {
//                throw Exception()
//            }
//        }
//    }
//}
