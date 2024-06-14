package woowacourse.shopping.presentation.ui.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.PageInfo
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.ShoppingProduct
import woowacourse.shopping.domain.model.UpdatedQuantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.presentation.event.Event
import woowacourse.shopping.presentation.event.SingleLiveEvent

class ShoppingViewModel(
    val shoppingItemsRepository: ShoppingItemsRepository,
    val cartItemsRepository: CartRepository,
    val recentProductRepository: RecentProductRepository,
) : ViewModel(), ShoppingEventHandler, ShoppingItemCountHandler {
    private val _shoppingProducts: MutableLiveData<List<ShoppingProduct>> = MutableLiveData()
    val shoppingProducts: LiveData<List<ShoppingProduct>>
        get() = _shoppingProducts

    private val _pageInfo: MutableLiveData<PageInfo> = MutableLiveData(PageInfo(false, 0, 0))
    val pageInfo: LiveData<PageInfo>
        get() = _pageInfo

    private var nextPage = DEFAULT_PAGE

    private val _recentProducts: MutableLiveData<List<RecentProduct>> = MutableLiveData()
    val recentProducts: LiveData<List<RecentProduct>>
        get() = _recentProducts

    private val _changedIds: SingleLiveEvent<Set<Long>> = SingleLiveEvent()
    val changedIds: LiveData<Set<Long>>
        get() = _changedIds

    private val _totalCartItemsQuantity = MutableLiveData(DEFAULT_TOTAL_CART_ITEMS_QUANTITY)
    val totalCartItemsQuantity: LiveData<Int>
        get() = _totalCartItemsQuantity

    private val _navigateToDetail = MutableLiveData<Event<Long>>()
    val navigateToDetail: LiveData<Event<Long>>
        get() = _navigateToDetail

    private val _navigateToCart = MutableLiveData<Event<Boolean>>()
    val navigateToCart: LiveData<Event<Boolean>>
        get() = _navigateToCart

    private val _isLoading = MutableLiveData(Event(true))

    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    fun loadProducts() {
        setLoadingStart()
        viewModelScope.launch {
            val totalProductsDeferred =
                async { asyncGetProductListWithSettingPageInfo() }
            val cartItemsDeferred =
                async { asyncLoadCartItems() }
            loadRecentProducts()
            updateTotalCartItemsQuantity()
            combineProductsWithCartItems(totalProductsDeferred.await(), cartItemsDeferred.await())
        }
    }

    private suspend fun asyncGetProductListWithSettingPageInfo(): List<Product> {
        var products: List<Product> = shoppingProducts.value?.map { it.product }.orEmpty()
        val result = shoppingItemsRepository.fetchProductsWithPage(nextPage, PAGE_SIZE)
        result.onSuccess { productListInfo ->
            products = products + productListInfo.products
            _pageInfo.value = productListInfo.pageInfo
            nextPage = productListInfo.pageInfo.currentPage + 1
        }.onFailure {
            setLoadingEnd()
            Log.d(this::class.java.simpleName, "$it")
        }
        return products
    }

    private suspend fun asyncLoadCartItems(): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        val result = cartItemsRepository.fetchCartItemsInfo()
        result.onSuccess { items ->
            cartItems = items
        }.onFailure {
            setLoadingEnd()
            Log.d(this::class.java.simpleName, "$it")
        }
        return cartItems
    }

    private fun combineProductsWithCartItems(
        products: List<Product>?,
        cartItems: List<CartItem>?,
    ) {
        if (products != null && cartItems != null) {
            _shoppingProducts.value = convertToShoppingProductList(products, cartItems)
            _changedIds.value = products.map { it.id }.toSet()
            setLoadingEnd()
        }
    }

    private fun convertToShoppingProductList(
        products: List<Product>,
        cartItems: List<CartItem>,
    ): List<ShoppingProduct> {
        return products.map { product ->
            val result = cartItems.find { it.productId == product.id }
            if (result != null) {
                ShoppingProduct(product, result.quantity)
            } else {
                ShoppingProduct(product)
            }
        }
    }

    private fun updateTotalCartItemsQuantity() {
        viewModelScope.launch {
            val result = cartItemsRepository.fetchTotalQuantity()
            result.onSuccess { totalQuantity ->
                _totalCartItemsQuantity.value = totalQuantity
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun loadRecentProducts() {
        viewModelScope.launch {
            _recentProducts.value = recentProductRepository.loadLatestList()
        }
    }

    fun setLoadingStart() {
        _isLoading.value = Event(true)
    }

    fun setLoadingEnd() {
        _isLoading.value = Event(false)
    }

    override fun onProductClick(productId: Long) {
        _navigateToDetail.value = Event(productId)
        viewModelScope.launch {
            val result = shoppingItemsRepository.findProductItem(productId)
            result.onSuccess { product ->
                updateRecentProducts(product)
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun updateRecentProducts(product: Product) {
        viewModelScope.launch {
            recentProductRepository.save(product)
            loadRecentProducts()
        }
    }

    override fun onLoadMoreButtonClick() {
        loadMoreProducts()
    }

    private fun loadMoreProducts() {
        loadProducts()
    }

    override fun onShoppingCartButtonClick() {
        _navigateToCart.value = Event(true)
    }

    override fun increaseCount(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            val result = cartItemsRepository.addCartItem(productId, quantity.inc())
            result.onSuccess {
                _shoppingProducts.value = getQuantityChangedList(productId, quantity.inc())
                _totalCartItemsQuantity.value = totalCartItemsQuantity.value?.inc()
                _changedIds.value = setOf(productId)
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    override fun decreaseCount(
        productId: Long,
        quantity: Int,
    ) {
        if (quantity > 1) {
            viewModelScope.launch {
                val result =
                    cartItemsRepository.updateCartItemQuantityWithProductId(
                        productId,
                        quantity.dec(),
                    )
                result.onSuccess {
                    _shoppingProducts.value = getQuantityChangedList(productId, quantity.dec())
                    _totalCartItemsQuantity.value = totalCartItemsQuantity.value?.dec()
                    _changedIds.value = setOf(productId)
                }.onFailure {
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        } else {
            viewModelScope.launch {
                val result = cartItemsRepository.deleteCartItemWithProductId(productId)
                result.onSuccess {
                    _shoppingProducts.value = getQuantityChangedList(productId, 0)
                    _totalCartItemsQuantity.value = totalCartItemsQuantity.value?.dec()
                    _changedIds.value = setOf(productId)
                }.onFailure {
                    Log.d(this::class.java.simpleName, "$it")
                }
            }
        }
    }

    private fun getQuantityChangedList(
        productId: Long,
        newQuantity: Int,
    ): List<ShoppingProduct> {
        return shoppingProducts.value?.map {
            if (it.product.id == productId) {
                it.copy(quantity = newQuantity)
            } else {
                it
            }
        }.orEmpty()
    }

    fun acceptChangedItems(changedItems: Set<Long>) {
        viewModelScope.launch {
            val result = cartItemsRepository.getCartItemsQuantities(changedItems)
            result.onSuccess { updatedQuantities ->
                _shoppingProducts.value = applyUpdatedQuantities(updatedQuantities)
                _changedIds.value = changedIds.value.orEmpty() + changedItems
                updateTotalCartItemsQuantity()
            }.onFailure {
                Log.d(this::class.java.simpleName, "$it")
            }
        }
    }

    private fun applyUpdatedQuantities(updatedQuantities: List<UpdatedQuantity>): List<ShoppingProduct> {
        val updatedQuantitiesMap = updatedQuantities.associateBy { it.productId }
        return shoppingProducts.value?.map { shoppingProduct ->
            val updatedQuantity = updatedQuantitiesMap[shoppingProduct.product.id]
            if (updatedQuantity != null) {
                shoppingProduct.copy(quantity = updatedQuantity.quantity)
            } else {
                shoppingProduct
            }
        }.orEmpty()
    }

    companion object {
        private const val DEFAULT_TOTAL_CART_ITEMS_QUANTITY = 0
        private const val DEFAULT_PAGE = 0
        private const val PAGE_SIZE = 10
    }
}
