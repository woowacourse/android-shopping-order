package woowacourse.shopping.presentation.ui.cart.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.counter.CounterHandler

class RecommendViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), CounterHandler {
    private val _order: MutableLiveData<Order> = MutableLiveData()
    val order: LiveData<Order>
        get() = _order

    private val _recommendItemsState: MutableLiveData<UIState<List<ShoppingProduct>>> =
        MutableLiveData()
    val recommendItemsState: LiveData<UIState<List<ShoppingProduct>>>
        get() = _recommendItemsState

    private val _totalOrderPrice: MutableLiveData<Long> = MutableLiveData(DEFAULT_TOTAL_PRICE)
    val totalOrderPrice: LiveData<Long>
        get() = _totalOrderPrice

    private val _totalOrderQuantity: MutableLiveData<Int> = MutableLiveData(DEFAULT_TOTAL_QUANTITY)
    val totalOrderQuantity: LiveData<Int>
        get() = _totalOrderQuantity

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    /*private val _navigateToOrder = MutableLiveData<Event<Boolean>>()

    val navigateToOrder: LiveData<Event<Boolean>>
        get() = _navigateToOrder*/

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val _deleteCartItem = MutableLiveData<Event<Long>>()
    val deleteCartItem: LiveData<Event<Long>>
        get() = _deleteCartItem

    init {
        fetchOrder()
        setUpUIState()
        viewModelScope.launch {
            shoppingRepository.initializeProducts()
        }
    }

    private fun fetchOrder() {
        _order.value = OrderDatabase.getOrder()
        updatePriceAndQuantity()
    }

    private fun setUpUIState() {
        loadRecommendationProducts()
        /*_recommendItemsState.value =
            try {
                loadRecommendationProducts()
            } catch (e: Exception) {
                UIState.Error(e)
                loadRecommendationProducts()
            }*/
    }

    private fun updatePriceAndQuantity() {
        _totalOrderPrice.value = _order.value?.getTotalPrice() ?: 0L
        _totalOrderQuantity.value = _order.value?.getTotalQuantity() ?: 0
    }

    private fun loadRecommendationProducts() {
        var recentProduct: RecentProduct? = null
        var items: List<ShoppingProduct> = listOf()

        viewModelScope.launch {
            recentProduct = recentProductRepository.loadLatest().getOrNull()

            if (recentProduct == null) {
                _recommendItemsState.value = UIState.Empty
                return@launch
            }

            val cartItems = cartRepository.findAll()
            var cartItemIds: List<Long> = listOf()
            cartItems.onSuccess { shoppingCart ->
                cartItemIds = shoppingCart.items.map { it.productId }
            }
            // val cartItemIds = cartRepository.findAll().items.map { it.productId }

            /*items =
                shoppingRepository.recommendProducts(
                    recentProduct!!.category,
                    DEFAULT_RECOMMEND_ITEM_COUNTS,
                    cartItemIds,
                ).mapperToShoppingProductList()*/

            val recommendCandidates =
                shoppingRepository.recommendProducts(
                    recentProduct!!.category,
                    DEFAULT_RECOMMEND_ITEM_COUNTS,
                    cartItemIds,
                )

            recommendCandidates.onSuccess {
                items = it.mapperToShoppingProductList()
            }

            _recommendItemsState.value =
                if (items.isEmpty()) {
                    UIState.Empty
                } else {
                    UIState.Success(items)
                }
        }
    }

    /*private fun loadRecommendationProducts(): UIState<List<ShoppingProduct>> {
        var recentProduct: Result<RecentProduct?>
        viewModelScope.launch {
            recentProduct = recentProductRepository.loadLatest()
            recentProduct.onSuccess {
                if (it == null) return UIState.Empty
            }
        }
        cartRepository.updateCartItems()
        val cartItemIds = cartRepository.findAll().items.map { it.productId }
        val items =
            shoppingRepository.recommendProducts(
                recentProduct.category,
                DEFAULT_RECOMMEND_ITEM_COUNTS,
                cartItemIds,
            ).mapperToShoppingProductList()
        return if (items.isEmpty()) {
            UIState.Empty
        } else {
            UIState.Success(items)
        }
    }*/

    fun onLoading() {
        _loading.postValue(true)
    }

    fun onLoaded() {
        _loading.postValue(false)
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
        val shoppingProduct =
            (recommendItemsState.value as UIState.Success).data.find { it.product.id == productId }
        shoppingProduct?.increase()
        viewModelScope.launch {
            val product = shoppingRepository.findProductItem(productId)
            product.onSuccess {
                if (it == null) return@onSuccess
                cartRepository.insert(it, shoppingProduct?.quantity() ?: 1)
            }
        }
        loadRecommendationProducts()
        /*val product = shoppingRepository.findProductItem(productId) ?: return
        cartRepository.insert(product, shoppingProduct?.quantity() ?: 1)
        loadRecommendationProducts()*/
    }

    override fun decreaseCount(productId: Long) {
        val shoppingProduct =
            (recommendItemsState.value as UIState.Success).data.find { it.product.id == productId }
        shoppingProduct?.decrease()
        val quantity = shoppingProduct?.quantity() ?: 0

        viewModelScope.launch {
            var transaction: Result<Unit>
            if (quantity > 0) {
                transaction =
                    cartRepository.updateQuantity(productId, shoppingProduct?.quantity() ?: 1)
            } else {
                transaction = cartRepository.deleteWithProductId(productId)
            }
            transaction.onSuccess {
                loadRecommendationProducts()
            }
        }
    }

    companion object {
        private const val DEFAULT_TOTAL_PRICE = 0L
        private const val DEFAULT_TOTAL_QUANTITY = 0
        private const val DEFAULT_RECOMMEND_ITEM_COUNTS = 10
    }
}
