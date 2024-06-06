package woowacourse.shopping.ui.order.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.event.Event
import woowacourse.shopping.ui.home.adapter.product.HomeViewItem.ProductViewItem
import woowacourse.shopping.ui.home.viewmodel.HomeViewModel.Companion.ASCENDING_SORT_ORDER
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.action.CartNavigationActions
import woowacourse.shopping.ui.order.cart.action.CartNotifyingActions
import woowacourse.shopping.ui.order.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.ui.order.cart.listener.CartClickListener
import woowacourse.shopping.ui.order.listener.OrderClickListener
import woowacourse.shopping.ui.order.recommend.action.RecommendNavigationActions
import woowacourse.shopping.ui.order.recommend.listener.RecommendClickListener
import woowacourse.shopping.ui.state.OrderState
import woowacourse.shopping.ui.state.UiState
import kotlin.math.min

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(),
    CartClickListener,
    OrderClickListener,
    RecommendClickListener {


    // 공용


    val totalPrice: LiveData<Int>
        get() =
            selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.totalPrice
                }
            }

    val selectedCartViewItemSize: LiveData<Int>
        get() =
            selectedCartViewItems.map { selectedCartViewItemsValue ->
                selectedCartViewItemsValue.sumOf { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.quantity
                }
            }

    val allCheckBoxChecked: LiveData<Boolean>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                if (cartUiState.value is UiState.Success && cartViewItemsValue.isNotEmpty()) {
                    cartViewItemsValue.all { selectedCartViewItem -> selectedCartViewItem.isChecked }
                } else {
                    false
                }
            }

    private val _orderNavigationActions = MutableLiveData<Event<OrderNavigationActions>>()
    val orderNavigationActions: LiveData<Event<OrderNavigationActions>>
        get() = _orderNavigationActions

    private val _cartNavigationActions = MutableLiveData<Event<CartNavigationActions>>()
    val cartNavigationActions: LiveData<Event<CartNavigationActions>>
        get() = _cartNavigationActions

    private val _recommendNavigationActions = MutableLiveData<Event<RecommendNavigationActions>>()
    val recommendNavigationActions: LiveData<Event<RecommendNavigationActions>>
        get() = _recommendNavigationActions

    private val _orderNotifyingActions = MutableLiveData<Event<OrderNotifyingActions>>()
    val orderNotifyingActions: LiveData<Event<OrderNotifyingActions>>
        get() = _orderNotifyingActions

    private val _cartNotifyingActions = MutableLiveData<Event<CartNotifyingActions>>()
    val cartNotifyingActions: LiveData<Event<CartNotifyingActions>>
        get() = _cartNotifyingActions

    // Cart
    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

    private val cartViewItems = MutableLiveData<List<CartViewItem>>()
    private val selectedCartViewItems = MutableLiveData<List<CartViewItem>>(mutableListOf())

    val isCartEmpty: LiveData<Boolean>
        get() =
            cartViewItems.map { cartViewItemsValue ->
                cartViewItemsValue.isEmpty()
            }

    // Recommend
    private val _recommendUiState =
        MutableLiveData<UiState<List<ProductViewItem>>>(UiState.Loading)
    val recommendUiState: LiveData<UiState<List<ProductViewItem>>>
        get() = _recommendUiState

    private val _orderState = MutableLiveData<OrderState>(OrderState.Cart)
    val orderState: LiveData<OrderState>
        get() = _orderState

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadCartViewItems()
        }, 1000)
    }

    fun generateRecommendProductViewItems() {
        runCatching {
            val mostRecentProduct = recentProductRepository.findMostRecentProduct()
            productRepository.getProducts(
                mostRecentProduct?.category,
                0,
                Int.MAX_VALUE,
                ASCENDING_SORT_ORDER,
            )
        }.onSuccess { productResponse ->
            val selectedProducts =
                selectedCartViewItems.value?.map { selectedCartViewItem -> selectedCartViewItem.cartItem.product }
                    ?: return@onSuccess
            var sameCategoryProducts = productResponse.getOrNull()?.products
                ?: return@onSuccess
            sameCategoryProducts =
                sameCategoryProducts.filter { sameCategoryProduct ->
                    !selectedProducts.contains(sameCategoryProduct)
                }.shuffled()

            val numberOfRecommend = min(DEFAULT_NUMBER_OF_RECOMMEND, sameCategoryProducts.size)
            val recommendProductViewItems =
                sameCategoryProducts.subList(0, numberOfRecommend).map(::ProductViewItem)
            _recommendUiState.value = UiState.Success(recommendProductViewItems)
        }
    }

    private fun loadCartViewItems() {
        runCatching {
            val cartTotalQuantity = cartRepository.getCartTotalQuantity().getOrNull() ?: 0
            cartRepository.getCartItems(0, cartTotalQuantity, DESCENDING_SORT_ORDER)
        }.onSuccess {
            cartViewItems.value =
                it.getOrNull()?.map(::CartViewItem)
            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
        }.onFailure {
            _cartUiState.value = UiState.Error(it)
        }
    }

    private fun getCartViewItemByCartItemId(cartItemId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    private fun getCartViewItemByProductId(productId: Int): CartViewItem? {
        return cartViewItems.value?.firstOrNull { cartViewItem ->
            cartViewItem.cartItem.product.productId == productId
        }
    }

    private fun getCartViewItemPosition(cartItemId: Int): Int? {
        return cartViewItems.value?.indexOfFirst { cartViewItem -> cartViewItem.cartItem.cartItemId == cartItemId }
    }

    override fun onCheckBoxClick(cartItemId: Int) {
        var updatedCartItem = getCartViewItemByCartItemId(cartItemId) ?: return
        updatedCartItem = updatedCartItem.toggleCheck()

        if (updatedCartItem.isChecked) {
            selectedCartViewItems.value = selectedCartViewItems.value?.plus(updatedCartItem)
        } else {
            selectedCartViewItems.value =
                selectedCartViewItems.value?.filter { it.cartItem.cartItemId != cartItemId }
        }

        val position = getCartViewItemPosition(cartItemId) ?: return
        val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
        newCartViewItems[position] = updatedCartItem
        cartViewItems.value = newCartViewItems
        selectedCartViewItems.value = selectedCartViewItems.value
        _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
    }

    override fun onCartItemClick(productId: Int) {
        _cartNavigationActions.value = Event(CartNavigationActions.NavigateToDetail(productId))
    }

    override fun onDeleteButtonClick(cartItemId: Int) {
        runCatching {
            cartRepository.deleteCartItem(cartItemId)
        }.onSuccess {
            val deletedCartViewItem = getCartViewItemByCartItemId(cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems.remove(deletedCartViewItem)
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem -> selectedCartViewItem.cartItem.cartItemId == cartItemId }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems.removeAt(selectedPosition)
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
            _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCartItemDeleted)
        }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.plusQuantity())

        runCatching {
            cartRepository.updateCartItem(
                updatedCartItem.cartItem.cartItemId,
                updatedCartItem.cartItem.quantity,
            )
        }.onSuccess {
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                } ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())

            val recommendState = recommendUiState.value
            if (recommendState is UiState.Success) {
                val recommendProductViewItems = recommendState.data
                val recommendPosition =
                    recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                        recommendProductViewItem.product.productId == updatedCartItem.cartItem.product.productId
                    }
                val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                newRecommendProductViewItems[recommendPosition] =
                    ProductViewItem(
                        updatedCartItem.cartItem.product,
                        updatedCartItem.cartItem.quantity,
                    )
                _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
            }
        }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var updatedCartItem = getCartViewItemByProductId(productId) ?: return
        updatedCartItem = updatedCartItem.copy(cartItem = updatedCartItem.cartItem.minusQuantity())

        runCatching {
            if (updatedCartItem.cartItem.quantity == 0) {
                cartRepository.deleteCartItem(updatedCartItem.cartItem.cartItemId)
            } else {
                cartRepository.updateCartItem(
                    updatedCartItem.cartItem.cartItemId,
                    updatedCartItem.cartItem.quantity,
                )
            }
        }.onSuccess {
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            if (updatedCartItem.cartItem.quantity == 0) {
                newCartViewItems.removeAt(position)
                _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCartItemDeleted)
            } else {
                newCartViewItems[position] = updatedCartItem
            }
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                }
                    ?: return
            if (selectedPosition != -1) {
                val newSelectedCatViewItems =
                    selectedCartViewItems.value?.toMutableList() ?: return
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
                selectedCartViewItems.value = newSelectedCatViewItems
            }

            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())

            val recommendState = recommendUiState.value
            if (recommendState is UiState.Success) {
                val recommendProductViewItems = recommendState.data
                val recommendPosition =
                    recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                        recommendProductViewItem.product.productId == updatedCartItem.cartItem.product.productId
                    }
                val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                newRecommendProductViewItems[recommendPosition] =
                    ProductViewItem(
                        updatedCartItem.cartItem.product,
                        updatedCartItem.cartItem.quantity,
                    )
                _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
            }
        }
    }

    override fun onBackButtonClick() {
        _orderNavigationActions.value = Event(OrderNavigationActions.NavigateToBack)
    }

    override fun onAllCheckBoxClick() {
        val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
        if (allCheckBoxChecked.value == false) {
            cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.check()
            }
            selectedCartViewItems.value = cartViewItems.value
        } else {
            cartViewItems.value?.forEachIndexed { index, cartViewItem ->
                newCartViewItems[index] = cartViewItem.unCheck()
            }
            selectedCartViewItems.value = emptyList()
        }
        cartViewItems.value = newCartViewItems
        _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())
    }

    override fun onOrderButtonClick() {
        if (selectedCartViewItemSize.value == 0) {
            _cartNotifyingActions.value = Event(CartNotifyingActions.NotifyCanNotPutCart)
        } else {
            if (orderState.value is OrderState.Cart) {
                _orderState.value = OrderState.Recommend
                _orderNavigationActions.value = Event(OrderNavigationActions.NavigateToRecommend)
            } else {
                runCatching {
                    val selectedCartItemIds =
                        selectedCartViewItems.value?.map { selectedCartViewItem ->
                            selectedCartViewItem.cartItem.cartItemId
                        } ?: return
                    orderRepository.postOrder(selectedCartItemIds)
                }.onSuccess {
                    _orderNotifyingActions.value = Event(OrderNotifyingActions.NotifyCartCompleted)
                }
            }
        }
    }

    override fun onProductClick(productId: Int) {
        _cartNavigationActions.value = Event(CartNavigationActions.NavigateToDetail(productId))
        _recommendNavigationActions.value =
            Event(RecommendNavigationActions.NavigateToDetail(productId))
    }

    override fun onPlusButtonClick(product: Product) {
        runCatching {
            val cartTotalQuantity =
                cartRepository.getCartTotalQuantity().getOrNull()?.plus(1) ?: 0
            cartRepository.addCartItem(product.productId, 1)
            cartRepository.getCartItems(0, cartTotalQuantity, DESCENDING_SORT_ORDER)
        }.onSuccess {
            cartViewItems.value = it.getOrNull()?.map(::CartViewItem)
            val updatedCartItem = getCartViewItemByProductId(product.productId) ?: return
            val position = getCartViewItemPosition(updatedCartItem.cartItem.cartItemId) ?: return
            val newCartViewItems = cartViewItems.value?.toMutableList() ?: return
            newCartViewItems[position] = updatedCartItem
            cartViewItems.value = newCartViewItems

            val selectedPosition =
                selectedCartViewItems.value?.indexOfFirst { selectedCartViewItem ->
                    selectedCartViewItem.cartItem.cartItemId == updatedCartItem.cartItem.cartItemId
                } ?: return
            val newSelectedCatViewItems =
                selectedCartViewItems.value?.toMutableList() ?: return
            if (selectedPosition != -1) {
                newSelectedCatViewItems[selectedPosition] = updatedCartItem
            } else {
                newSelectedCatViewItems.add(updatedCartItem)
            }
            selectedCartViewItems.value = newSelectedCatViewItems
            _cartUiState.value = UiState.Success(cartViewItems.value ?: emptyList())

            val recommendState = recommendUiState.value
            if (recommendState is UiState.Success) {
                val recommendProductViewItems = recommendState.data
                val recommendPosition =
                    recommendProductViewItems.indexOfFirst { recommendProductViewItem ->
                        recommendProductViewItem.product.productId == product.productId
                    }
                val newRecommendProductViewItems = recommendProductViewItems.toMutableList()
                newRecommendProductViewItems[recommendPosition] = ProductViewItem(product, 1)
                _recommendUiState.value = UiState.Success(newRecommendProductViewItems)
            }
        }
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
        const val DEFAULT_NUMBER_OF_RECOMMEND = 10
    }
}
