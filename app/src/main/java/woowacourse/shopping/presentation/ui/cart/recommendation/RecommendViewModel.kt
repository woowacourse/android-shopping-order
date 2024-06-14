package woowacourse.shopping.presentation.ui.cart.recommendation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.order.OrderDatabase
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.event.SingleLiveEvent
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.SharedChangedIdsDB

class RecommendViewModel(
    private val cartRepository: CartRepository,
    private val shoppingRepository: ShoppingItemsRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), RecommendItemCountHandler {
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

    private val _isEmpty = MediatorLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private val _deleteCartItem = MutableLiveData<Event<Long>>()
    val deleteCartItem: LiveData<Event<Long>>
        get() = _deleteCartItem

    private val _isLoading = MutableLiveData(Event(true))
    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    private val _changedIds: SingleLiveEvent<Set<Long>> = SingleLiveEvent()
    val changedIds: LiveData<Set<Long>>
        get() = _changedIds

    init {
        with(_isEmpty) {
            addSource(recommendItemsState) {
                value = (it as UIState.Success<List<ShoppingProduct>>).data.isEmpty()
            }
        }
    }

    fun setLoadingState(value: Boolean) {
        _isLoading.value = Event(value)
    }

    fun setRecommendation() {
        fetchOrder()
        loadRecommendationProducts()
    }

    private fun fetchOrder() {
        _order.value = OrderDatabase.getOrder()
        updatePriceAndQuantity()
    }

    private fun updatePriceAndQuantity() {
        _totalOrderPrice.value = _order.value?.getTotalPrice() ?: 0L
        _totalOrderQuantity.value = _order.value?.getTotalQuantity() ?: 0
    }

    private fun loadRecommendationProducts() {
        viewModelScope.launch {
            val recentProduct = recentProductRepository.loadLatest()
            if (recentProduct == null) {
                _recommendItemsState.value = UIState.Empty
                setLoadingState(false)
                return@launch
            }
            val result = cartRepository.fetchCartItemsInfo()
            result.onSuccess { items ->
                val cartItemIds = items.map { it.productId }
                val recommendItems =
                    shoppingRepository.recommendProducts(
                        recentProduct.category,
                        DEFAULT_RECOMMEND_ITEM_COUNTS,
                        cartItemIds,
                    ).mapperToShoppingProductList()
                setUpUIState(recommendItems)
            }.onFailure {
                setLoadingState(false)
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun setUpUIState(recommendItems: List<ShoppingProduct>) {
        if (recommendItems.isEmpty()) {
            _recommendItemsState.value = (UIState.Empty)
        } else {
            _recommendItemsState.value = (UIState.Success(recommendItems))
        }
        setLoadingState(false)
    }

    private fun List<Product>.mapperToShoppingProductList(): List<ShoppingProduct> {
        return this.map { ShoppingProduct(it) }
    }

    fun completeOrder() {
        val currentOrder = _order.value ?: return
        OrderDatabase.postOrder(currentOrder)
    }

    override fun increaseCount(
        productId: Long,
        quantity: Int,
    ) {
        addOrder(productId, quantity)
        SharedChangedIdsDB.addChangedProductsId(setOf(productId))
        SharedChangedIdsDB.addRecommendProductsIds(setOf(productId))
    }

    private fun addOrder(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val result =
                cartRepository.updateCartItemQuantityWithProductId(productId, quantity.inc())
            result.onSuccess {
                val cartItem =
                    cartRepository.findCartItemWithProductId(productId)
                        ?: return@launch
                val prevOrder = order.value ?: Order()
                prevOrder.addCartItem(cartItem)
                _order.value = prevOrder
                changeShoppingProductQuantity(productId, quantity.inc())
                _changedIds.value = setOf(productId)
                updatePriceAndQuantity()
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun decreaseCount(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity == 1) {
            removeOrder(productId)
        } else {
            decreaseOrderQuantity(productId, quantity)
        }
        SharedChangedIdsDB.addChangedProductsId(setOf(productId))
        SharedChangedIdsDB.addRecommendProductsIds(setOf(productId))
    }

    private fun removeOrder(productId: Long) {
        viewModelScope.launch {
            val cartItem = cartRepository.findCartItemWithProductId(productId) ?: return@launch
            val result = cartRepository.deleteCartItemWithProductId(productId)
            result.onSuccess {
                val prevOrder = order.value ?: Order()
                prevOrder.removeCartItem(cartItem.id)
                _order.value = prevOrder
                changeShoppingProductQuantity(productId, 0)
                _changedIds.value = setOf(productId)
                updatePriceAndQuantity()
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun decreaseOrderQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val result =
                cartRepository.updateCartItemQuantityWithProductId(
                    productId,
                    quantity.dec(),
                )
            result.onSuccess {
                val cartItem =
                    cartRepository.findCartItemWithProductId(productId)
                        ?: return@launch
                val prevOrder = order.value ?: Order()
                prevOrder.addCartItem(cartItem)
                _order.value = prevOrder
                changeShoppingProductQuantity(productId, quantity.dec())
                _changedIds.value = setOf(productId)
                updatePriceAndQuantity()
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun changeShoppingProductQuantity(
        productId: Long,
        newQuantity: Int,
    ) {
        val changedShoppingProducts =
            (recommendItemsState.value as UIState.Success<List<ShoppingProduct>>).data.map {
                if (it.product.id == productId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
        _recommendItemsState.value = UIState.Success(changedShoppingProducts)
    }

    companion object {
        private const val DEFAULT_TOTAL_PRICE = 0L
        private const val DEFAULT_TOTAL_QUANTITY = 0
        private const val DEFAULT_RECOMMEND_ITEM_COUNTS = 10
    }
}
