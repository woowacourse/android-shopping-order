package woowacourse.shopping.presentation.ui.cart.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class RecommendViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
    private val orderDatabase: OrderDatabase,
) : ViewModel(), CounterHandler {
    private val _order: MutableLiveData<Order> = MutableLiveData()
    val order: LiveData<Order>
        get() = _order

    private val _shoppingProducts = MutableLiveData<List<ShoppingProduct>>()
    val shoppingProducts: LiveData<List<ShoppingProduct>>
        get() = _shoppingProducts

    val recommendItemsState: LiveData<UIState<List<ShoppingProduct>>> =
        shoppingProducts.switchMap { products ->
            if (products.isEmpty()) {
                MutableLiveData(UIState.Empty)
            } else {
                MutableLiveData(UIState.Success(products))
            }
        }

    private val _totalOrderPrice: MutableLiveData<Long> = MutableLiveData(DEFAULT_TOTAL_PRICE)
    val totalOrderPrice: LiveData<Long>
        get() = _totalOrderPrice

    private val _totalOrderQuantity: MutableLiveData<Int> = MutableLiveData(DEFAULT_TOTAL_QUANTITY)
    val totalOrderQuantity: LiveData<Int>
        get() = _totalOrderQuantity

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        fetchOrder()
        setUpUIState()
    }

    private fun fetchOrder() {
        _order.value = OrderDatabase.getOrder()
        updatePriceAndQuantity()
    }

    private fun updatePriceAndQuantity() {
        _totalOrderPrice.value = order.value?.getTotalPrice() ?: 0L
        _totalOrderQuantity.value = order.value?.getTotalQuantity() ?: 0
    }

    private fun setUpUIState() {
        loadRecommendationProducts()
    }

    private fun loadRecommendationProducts() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.loadLatest()
            val cartItems = asyncLoadCartItems()

            recentProduct.onSuccess {
                if (it != null) {
                    val recommendCandidates =
                        shoppingRepository.recommendProducts(
                            it.category,
                            DEFAULT_RECOMMEND_ITEM_COUNTS,
                            cartItems.map { it.productId },
                        )

                    recommendCandidates.onSuccess {
                        _shoppingProducts.value = it.mapperToShoppingProductList()
                    }
                }
            }
            onLoaded()
        }
    }

    private suspend fun asyncLoadCartItems(): List<CartItem> {
        var cartItems: List<CartItem> = listOf()
        val transaction =
            viewModelScope.async {
                cartRepository.findAll()
            }.await()
        transaction.onSuccess {
            cartItems = it.items
        }
        return cartItems
    }

    fun onLoading() {
        _isLoading.postValue(true)
    }

    fun onLoaded() {
        _isLoading.postValue(false)
    }

    private fun List<Product>.mapperToShoppingProductList(): List<ShoppingProduct> {
        return this.map { ShoppingProduct(it) }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            val deleteTransaction = cartRepository.delete(itemId)
            deleteTransaction.onSuccess {
                loadRecommendationProducts()
            }
        }
    }

    override fun increaseCount(productId: Long) {
        val shoppingProducts = _shoppingProducts.value?.map { it.copy() } ?: return
        val shoppingProduct = shoppingProducts.find { it.product.id == productId } ?: return
        val index = shoppingProducts.indexOf(shoppingProduct) ?: return
        shoppingProduct.increase()

        viewModelScope.launch {
            val insertion =
                cartRepository.insert(shoppingProduct.product, shoppingProduct.quantity())
            insertion.onSuccess {
                _shoppingProducts.value =
                    shoppingProducts.toMutableList().apply {
                        set(index, shoppingProduct)
                    }

                val find = cartRepository.findOrNullWithProductId(productId)
                find.onSuccess {
                    val cartItem = it ?: return@onSuccess
                    val currentOrder = _order.value ?: return@onSuccess

                    currentOrder.addCount(cartItem)
                    _order.value = currentOrder

                    updatePriceAndQuantity()
                }
            }
        }
    }

    // TODO : decreaseCount 함수 구현
    override fun decreaseCount(productId: Long) {
        val shoppingProducts = _shoppingProducts.value?.map { it.copy() } ?: return
        val shoppingProduct = shoppingProducts.find { it.product.id == productId } ?: return
        val index = shoppingProducts.indexOf(shoppingProduct) ?: return
        shoppingProduct.decrease()

        val quantity = shoppingProduct.quantity() ?: 0

        viewModelScope.launch {
            val transaction: Result<Unit> =
                if (quantity > 0) {
                    cartRepository.updateQuantity(productId, quantity)
                } else {
                    cartRepository.deleteWithProductId(productId)
                }
            transaction.onSuccess {
                _shoppingProducts.value =
                    shoppingProducts.toMutableList().apply {
                        set(index, shoppingProduct)
                    }

                val currentOrder = _order.value ?: return@onSuccess
                val cartItem = currentOrder.list.find { it.productId == productId } ?: return@onSuccess

                currentOrder.subCount(cartItem)
                _order.value = currentOrder

                updatePriceAndQuantity()
            }
        }
    }

    fun postOrder() {
        orderDatabase.postOrder(order = order.value ?: return)
    }

    companion object {
        private const val DEFAULT_TOTAL_PRICE = 0L
        private const val DEFAULT_TOTAL_QUANTITY = 0
        private const val DEFAULT_RECOMMEND_ITEM_COUNTS = 10
    }
}
