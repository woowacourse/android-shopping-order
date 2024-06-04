package woowacourse.shopping.presentation.ui.cart.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
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

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val _deleteCartItem = MutableLiveData<Event<Long>>()
    val deleteCartItem: LiveData<Event<Long>>
        get() = _deleteCartItem

    init {
        fetchOrder()
        setUpUIState()
    }

    private fun fetchOrder() {
        _order.value = OrderDatabase.getOrder()
        updatePriceAndQuantity()
    }

    private fun setUpUIState() {
        _recommendItemsState.value =
            try {
                loadRecommendationProducts()
            } catch (e: Exception) {
                UIState.Error(e)
                loadRecommendationProducts()
            }
    }

    private fun updatePriceAndQuantity() {
        _totalOrderPrice.value = _order.value?.getTotalPrice() ?: 0L
        _totalOrderQuantity.value = _order.value?.getTotalQuantity() ?: 0
    }

    private fun loadRecommendationProducts(): UIState<List<ShoppingProduct>> {
        val recentProduct = recentProductRepository.loadLatest() ?: return UIState.Empty
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
    }

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
        cartRepository.delete(itemId)
        loadRecommendationProducts()
    }

    fun completeOrder() {
        val currentOrder = _order.value ?: return
        cartRepository.makeOrder(currentOrder)
    }

    override fun increaseCount(productId: Long) {
        val shoppingProduct =
            (recommendItemsState.value as UIState.Success).data.find { it.product.id == productId }
        shoppingProduct?.increase()
        val product = shoppingRepository.findProductItem(productId) ?: return
        cartRepository.insert(product, shoppingProduct?.quantity() ?: 1)
        loadRecommendationProducts()
    }

    override fun decreaseCount(productId: Long) {
        val shoppingProduct =
            (recommendItemsState.value as UIState.Success).data.find { it.product.id == productId }
        shoppingProduct?.decrease()
        val quantity = shoppingProduct?.quantity() ?: 0

        if (quantity > 0) {
            cartRepository.updateQuantity(productId, shoppingProduct?.quantity() ?: 1)
        } else {
            cartRepository.deleteWithProductId(productId)
        }
        loadRecommendationProducts()
    }

    companion object {
        private const val DEFAULT_TOTAL_PRICE = 0L
        private const val DEFAULT_TOTAL_QUANTITY = 0
        private const val DEFAULT_RECOMMEND_ITEM_COUNTS = 10
    }
}
