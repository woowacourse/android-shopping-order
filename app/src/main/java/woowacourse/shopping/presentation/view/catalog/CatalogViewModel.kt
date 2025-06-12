package woowacourse.shopping.presentation.view.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toProduct
import woowacourse.shopping.presentation.model.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.catalog.adapter.CatalogItem

class CatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel(),
    ItemCounterListener {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _items = MutableLiveData<List<CatalogItem>>()
    val items: LiveData<List<CatalogItem>> = _items

    private val _itemUpdateEvent = MutableLiveData<ProductUiModel>()
    val itemUpdateEvent: LiveData<ProductUiModel> = _itemUpdateEvent

    private val _deletedItemId = MutableLiveData<Long>()
    val deletedItemId: LiveData<Long> = _deletedItemId

    private val _totalCartCount = MutableLiveData<Int>()
    val totalCartCount: LiveData<Int> = _totalCartCount

    private val _toastEvent = MutableSingleLiveData<CatalogEvent>()
    val toastEvent: SingleLiveData<CatalogEvent> = _toastEvent

    private val loadSize = 20
    private var currentPage = 0

    init {
        fetchProducts()
    }

    override fun increase(product: ProductUiModel) {
        updateCartItemQuantity(product, isIncrement = true)
    }

    override fun decrease(product: ProductUiModel) {
        updateCartItemQuantity(product, isIncrement = false)
    }

    fun fetchProducts() {
        _isLoading.value = true

        viewModelScope.launch {
            collectCartState { cartMap ->
                productRepository
                    .loadRecentProducts(10)
                    .onSuccess { recentProducts ->
                        val recentItem =
                            CatalogItem.RecentProductItem(
                                recentProducts.map { it.toUiModel() },
                            )

                        productRepository
                            .loadProducts(currentPage, loadSize)
                            .onSuccess { (products, hasMore) ->
                                updateCatalogItems(products, cartMap, recentItem, hasMore)
                                currentPage++
                            }
                    }.onFailure {
                        _toastEvent.setValue(CatalogEvent.LOAD_PRODUCT_FAILURE)
                    }
            }
            _isLoading.value = false
        }
    }

    private suspend fun collectCartState(onCollected: suspend (Map<Long, ProductUiModel>) -> Unit) {
        cartRepository
            .getAllCartItems()
            .onSuccess { cartItems ->
                _totalCartCount.value = cartItems.sumOf { it.amount }
                val map = cartItems.associate { it.product.id to it.toUiModel() }
                onCollected(map)
            }.onFailure {
                _toastEvent.setValue(CatalogEvent.CART_LOAD_FAILURE)
            }
    }

    private fun updateCatalogItems(
        products: List<Product>,
        cartUiMap: Map<Long, ProductUiModel>,
        recentItem: CatalogItem.RecentProductItem,
        hasMore: Boolean,
    ) {
        val fetchedUiModels = products.map { cartUiMap[it.id] ?: it.toUiModel() }
        val existingItems =
            _items.value
                .orEmpty()
                .filterIsInstance<CatalogItem.ProductItem>()
                .map { it.product }

        val combined = (existingItems + fetchedUiModels).distinctBy { it.id }

        val updatedItems =
            buildList {
                if (recentItem.products.isNotEmpty()) add(recentItem)
                addAll(combined.map { CatalogItem.ProductItem(it) })
                if (hasMore && none { it is CatalogItem.LoadMoreItem }) add(CatalogItem.LoadMoreItem)
            }

        _items.value = updatedItems
    }

    fun refreshCartState() {
        if (_items.value.isNullOrEmpty()) return

        viewModelScope.launch {
            collectCartState { cartMap ->
                val updated =
                    _items.value?.map {
                        if (it is CatalogItem.ProductItem) {
                            CatalogItem.ProductItem(
                                cartMap[it.product.id] ?: it.product.copy(amount = 0),
                            )
                        } else {
                            it
                        }
                    }
                _items.value = updated.orEmpty()
            }
        }
    }

    fun addRecentProduct(product: ProductUiModel) {
        viewModelScope.launch {
            productRepository.addRecentProduct(product.toProduct())
            updateRecentProducts()
        }
    }

    private fun updateRecentProducts() {
        viewModelScope.launch {
            productRepository.loadRecentProducts(10).onSuccess { recentProducts ->
                val recentItem =
                    CatalogItem.RecentProductItem(recentProducts.map { it.toUiModel() })
                val newItems =
                    _items.value.orEmpty().filterNot { it is CatalogItem.RecentProductItem }
                _items.value =
                    buildList {
                        if (recentItem.products.isNotEmpty()) add(recentItem)
                        addAll(newItems)
                    }
            }
        }
    }

    private fun updateCartItemQuantity(
        product: ProductUiModel,
        isIncrement: Boolean,
    ) {
        viewModelScope.launch {
            val cartItems = cartRepository.getAllCartItems().getOrNull()
            val existing = cartItems?.find { it.product.id == product.id }

            when {
                existing != null && isIncrement -> {
                    cartRepository
                        .increaseCartItem(existing)
                        .onSuccess {
                            postUpdatedCart(it)
                        }
                }

                existing != null -> {
                    if (existing.amount <= 1) {
                        cartRepository
                            .deleteCartItem(existing.cartId)
                            .onSuccess {
                                _itemUpdateEvent.postValue(product.copy(amount = 0))
                                updateCartCount()
                            }
                    } else {
                        cartRepository
                            .decreaseCartItem(existing)
                            .onSuccess {
                                postUpdatedCart(it)
                            }
                    }
                }

                isIncrement -> {
                    val newItem = product.copy(amount = 1)
                    cartRepository
                        .addCartItem(newItem.toCartItem())
                        .onSuccess {
                            _itemUpdateEvent.postValue(newItem)
                            refreshCartState()
                        }
                }
            }
        }
    }

    private fun postUpdatedCart(cartId: Long) {
        viewModelScope.launch {
            val item = getCartItemByCartId(cartId)
            item?.let { _itemUpdateEvent.postValue(it.toUiModel()) }
            updateCartCount()
        }
    }

    private fun updateCartCount() {
        viewModelScope.launch {
            val count = cartRepository.getAllCartItemsCount().getOrNull()
            _totalCartCount.postValue(count?.quantity)
        }
    }

    private suspend fun getCartItemByCartId(id: Long): CartItem? = cartRepository.getAllCartItems().getOrNull()?.find { it.cartId == id }

    companion object {
        fun factory(
            productRepository: ProductRepository,
            cartRepository: CartRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T = CatalogViewModel(productRepository, cartRepository) as T
            }
    }
}
