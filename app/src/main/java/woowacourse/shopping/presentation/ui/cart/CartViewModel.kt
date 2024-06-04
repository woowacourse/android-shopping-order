package woowacourse.shopping.presentation.ui.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.local.LocalCartDataSourceImpl
import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.data.local.AppDatabase
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.order.RemoteOrderDataSource
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recent.RecentProductRepositoryImpl
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.CartModel
import woowacourse.shopping.presentation.ui.model.ProductModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val orderRepository: OrderRepository,
    initialTotalCartItemCount: Int,
) : ViewModel(), CartHandler {
    private val _error = MutableLiveData<Event<CartError>>()
    val error: LiveData<Event<CartError>> = _error

    private val _orderState = MutableLiveData<OrderState>(OrderState.CartList)
    val orderState: LiveData<OrderState> = _orderState

    private val _orderEvent = MutableLiveData<Event<OrderEvent>>()
    val orderEvent: LiveData<Event<OrderEvent>> = _orderEvent

    private val _recommendedProduct = MutableLiveData<Map<Long, ProductModel>>()
    val recommendedProduct: LiveData<Map<Long, ProductModel>> get() = _recommendedProduct

    private val _cartItems: MutableLiveData<UiState<List<CartModel>>> = MutableLiveData(UiState.Loading)
    val cartItems: LiveData<UiState<List<CartModel>>> = _cartItems

    private val _changedCartProducts: MutableMap<Long, Int> = mutableMapOf()
    val changedCartProducts: Map<Long, Int> get() = _changedCartProducts.toMap()

    private val cartItemsData
        get() = (cartItems.value as? UiState.Success)?.data ?: emptyList()

    val isAllCartItemsSelected: LiveData<Boolean> =
        cartItems.switchMap {
            MutableLiveData(
                if (cartItemsData.isNotEmpty()) {
                    cartItemsData.all { it.isChecked }
                } else {
                    false
                },
            )
        }

    private val selectedCartItems: LiveData<List<CartModel>> =
        cartItems.switchMap {
            MutableLiveData(cartItemsData.filter { it.isChecked })
        }

    val totalPrice: LiveData<Long> =
        selectedCartItems.switchMap {
            MutableLiveData(
                it.sumOf { it.price * it.quantity },
            )
        }

    val totalCount: LiveData<Int> =
        selectedCartItems.switchMap {
            MutableLiveData(
                it.sumOf { it.quantity },
            )
        }

    init {
        loadAllCartItems(initialTotalCartItemCount)
    }

    private fun loadAllCartItems(pageSize: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            cartRepository.load(
                0,
                pageSize,
                onSuccess = { loadedCarts, _ ->
                    val currentCartItems = cartItemsData.associateBy { it.cartId }
                    val newCartModels = loadedCarts.map { it.toUiModel() }
                    val newCartItems =
                        newCartModels.map { cartModel ->
                            val find = currentCartItems[cartModel.cartId]
                            if (find == null) {
                                cartModel.copy(isChecked = true)
                            } else {
                                cartModel.copy(isChecked = find.isChecked)
                            }
                        }
                    _cartItems.value = UiState.Success(newCartItems)
                },
                onFailure = { _error.value = Event(CartError.CartItemsNotFound) },
            )
        }, 500)
    }

    override fun onDeleteClick(cartId: Long) {
        cartRepository.deleteCartItem(
            cartId,
            onSuccess = { deletedCartId, _ ->
                val deletedItem = cartItemsData.find { it.cartId == deletedCartId } ?: return@deleteCartItem
                _changedCartProducts[deletedItem.productId] = 0

                updateDeletedCartItem(deletedCartId)
            },
            onFailure = {},
        )
    }

    override fun onCheckBoxClicked(cartId: Long) {
        val checkedCartItems =
            cartItemsData.map { cartItem ->
                if (cartItem.cartId == cartId) {
                    cartItem.copy(isChecked = !cartItem.isChecked)
                } else {
                    cartItem
                }
            }
        _cartItems.value = UiState.Success(checkedCartItems)
    }

    override fun onTotalCheckBoxClicked(isChecked: Boolean) {
        val updatedItems = cartItemsData.map { it.copy(isChecked = isChecked) }
        _cartItems.value = UiState.Success(updatedItems)
    }

    override fun onOrderClicked() {
        when (orderState.value ?: return) {
            OrderState.CartList -> {
                if (totalCount.value == 0) return
                _orderEvent.value = Event(OrderEvent.MoveToRecommend)
            }

            OrderState.Recommend -> {
                val cartItemIds = selectedCartItems.value?.map { it.cartId } ?: emptyList()
                if (cartItemIds.isNotEmpty()) {
                    orderRepository.completeOrder(cartItemIds, onSuccess = {
                        selectedCartItems.value?.forEach { cartModel -> _changedCartProducts[cartModel.productId] = 0 }

                        _orderEvent.value = Event(OrderEvent.CompleteOrder)
                    }, onFailure = {})
                }
            }
        }
    }

    override fun onDecreaseQuantity(productId: Long) {
        val selectedItem = cartItemsData.find { it.productId == productId } ?: return
        val updatedQuantity = selectedItem.quantity - 1
        if (orderState.value is OrderState.CartList && updatedQuantity < 1) return
        cartRepository.updateCartItemQuantity(
            selectedItem.cartId,
            updatedQuantity,
            onSuccess = { cartId, resultQuantity ->
                if (resultQuantity == 0) {
                    updateDeletedCartItem(cartId)
                } else {
                    updateCartItems(cartId, updatedCartItem = selectedItem.copy(quantity = resultQuantity))
                }

                _changedCartProducts[productId] = resultQuantity

                updateRecommendedProducts(productId, resultQuantity)
            },
            onFailure = {},
        )
    }

    override fun onIncreaseQuantity(productId: Long) {
        val selectedItem = cartItemsData.find { it.productId == productId } ?: return
        val updatedQuantity = selectedItem.quantity + 1
        cartRepository.updateCartItemQuantity(
            selectedItem.cartId,
            updatedQuantity,
            onSuccess = { cartId, resultQuantity ->
                updateCartItems(cartId, updatedCartItem = selectedItem.copy(quantity = resultQuantity))

                _changedCartProducts[productId] = resultQuantity

                updateRecommendedProducts(productId, resultQuantity)
            },
            onFailure = {},
        )
    }

    private fun updateDeletedCartItem(deletedCartId: Long) {
        val updated = cartItemsData.filter { it.cartId != deletedCartId }
        _cartItems.value = UiState.Success(updated)
    }

    private fun updateCartItems(
        cartId: Long,
        updatedCartItem: CartModel,
    ) {
        val result =
            cartItemsData.map { cartModel ->
                if (cartModel.cartId == cartId) {
                    updatedCartItem
                } else {
                    cartModel
                }
            }
        _cartItems.value = UiState.Success(result)
    }

    private fun updateRecommendedProducts(
        productId: Long,
        resultQuantity: Int,
    ) {
        if (recommendedProduct.value?.get(productId) == null) return
        _recommendedProduct.value =
            recommendedProduct.value?.toMutableMap()?.apply {
                this[productId]?.let {
                    this[productId] = it.copy(quantity = resultQuantity)
                }
            }
    }

    fun buildRecommendProducts() {
        recentRepository.loadMostRecent().onSuccess {
            val recentViewedCategory = it?.category ?: return
            productRepository.loadWithCategory(
                recentViewedCategory,
                0,
                20,
                onSuccess = { products ->
                    val existingCartItem = cartItemsData.groupBy { it.productId }
                    _recommendedProduct.value =
                        products.asSequence().filter { existingCartItem[it.id] == null }
                            .map { it.toUiModel() }.associateBy { it.id }
                },
                onFailure = {},
            )
        }
    }

    override fun onPlusButtonClick(productId: Long) {
        cartRepository.saveNewCartItem(
            productId,
            1,
            onSuccess = { _, savedQuantity ->
                updateRecommendedProducts(productId, savedQuantity)

                loadAllCartItems(cartItemsData.sumOf { it.quantity } + 1)

                _changedCartProducts[productId] = savedQuantity
            },
            onFailure = {},
        )
    }

    fun setOrderState(state: OrderState) {
        _orderState.value = state
    }

    companion object {
        class Factory(private val initialTotalCartItemCount: Int) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val recentDao = AppDatabase.instanceOrNull.recentProductDao()
                val cartDao = AppDatabase.instanceOrNull.cartDao()
                return CartViewModel(
                    cartRepository =
                        CartRepositoryImpl(
                            localCartDataSource = LocalCartDataSourceImpl(cartDao),
                            remoteCartDataSource = RemoteCartDataSource(),
                        ),
                    productRepository = ProductRepositoryImpl(),
                    recentRepository = RecentProductRepositoryImpl(recentDao),
                    orderRepository = OrderRepositoryImpl(RemoteOrderDataSource()),
                    initialTotalCartItemCount,
                ) as T
            }
        }
    }
}
