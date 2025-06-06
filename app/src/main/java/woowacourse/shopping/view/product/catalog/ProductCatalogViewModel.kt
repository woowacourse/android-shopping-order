package woowacourse.shopping.view.product.catalog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.product.catalog.adapter.ProductCatalogItem
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class ProductCatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    ProductCatalogEventHandler {
    private var page = FIRST_PAGE
    private var hasNext = false
    private val cartProducts = mutableSetOf<CartProduct>()

    private val recentProducts = MutableLiveData<List<RecentProduct>>(emptyList())
    private val productItems = MutableLiveData<List<ProductCatalogItem.ProductItem>>(emptyList())

    private val _productCatalogItems =
        MediatorLiveData<List<ProductCatalogItem>>().apply {
            addSource(recentProducts) { value = buildCatalogItems() }
            addSource(productItems) { value = buildCatalogItems() }
        }
    val productCatalogItems: LiveData<List<ProductCatalogItem>> get() = _productCatalogItems

    private val _totalQuantity = MutableLiveData(MINIMUM_QUANTITY)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    fun loadCatalog() {
        viewModelScope.launch {
            _isLoading.value = true
            loadRecentProducts()
            loadCartProducts()

            cartProductRepository
                .getTotalQuantity()
                .onSuccess {
                    _totalQuantity.postValue(it)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onPlusClick(item: Product) {
        viewModelScope.launch {
            cartProductRepository
                .insert(item.id, QUANTITY_TO_ADD)
                .onSuccess { cartProductId ->
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_TO_ADD))
                    updateProductQuantity(item, QUANTITY_TO_ADD)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        updateCartProduct(item, QUANTITY_TO_ADD)
    }

    override fun onQuantityDecreaseClick(item: Product) {
        updateCartProduct(item, -QUANTITY_TO_ADD)
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onRecentProductClick(item: RecentProduct) {
        _selectedProduct.setValue(item.product)
    }

    override fun onLoadMoreClick() {
        page++
        loadMoreProducts()
    }

    private fun loadRecentProducts() {
        recentProductRepository.getPagedProducts(RECENT_PRODUCT_SIZE_LIMIT) { result ->
            result
                .onSuccess {
                    recentProducts.postValue(it)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun loadCartProducts() {
        viewModelScope.launch {
            cartProductRepository
                .getPagedProducts()
                .onSuccess {
                    cartProducts.clear()
                    cartProducts.addAll(it.items)
                    loadProducts()
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun loadProducts() {
        productRepository.getPagedProducts(FIRST_PAGE, PRODUCT_SIZE_LIMIT * (page + 1)) { result ->
            result
                .onSuccess { pagedResult ->
                    applyLoadedProducts(pagedResult, true)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun loadMoreProducts() {
        productRepository.getPagedProducts(page, PRODUCT_SIZE_LIMIT) { result ->
            result
                .onSuccess { pagedResult ->
                    applyLoadedProducts(pagedResult, false)
                }.onFailure { Log.e("error", it.message.toString()) }
        }
    }

    private fun applyLoadedProducts(
        pagedResult: PagedResult<Product>,
        isInitial: Boolean,
    ) {
        val currentItems = if (isInitial) emptyList() else productItems.value.orEmpty()
        val newItems =
            pagedResult.items.map { product ->
                val existing = cartProducts.firstOrNull { it.product.id == product.id }
                ProductCatalogItem.ProductItem(product, existing?.quantity ?: MINIMUM_QUANTITY)
            }

        hasNext = pagedResult.hasNext
        productItems.postValue(currentItems + newItems)
        _isLoading.value = false
    }

    private fun updateCartProduct(
        item: Product,
        quantityDelta: Int,
    ) {
        viewModelScope.launch {
            val existing = cartProducts.firstOrNull { it.product.id == item.id } ?: return@launch
            val newQuantity = existing.quantity + quantityDelta

            cartProductRepository
                .updateQuantity(existing, quantityDelta)
                .onSuccess {
                    cartProducts.removeIf { it.product.id == item.id }
                    if (newQuantity > MINIMUM_QUANTITY) {
                        cartProducts.add(existing.copy(quantity = newQuantity))
                    }
                    updateProductQuantity(item, quantityDelta)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun updateProductQuantity(
        item: Product,
        quantityToAdd: Int,
    ) {
        val updatedItems =
            productItems.value.orEmpty().map {
                if (it.product.id == item.id) it.copy(quantity = it.quantity + quantityToAdd) else it
            }
        productItems.postValue(updatedItems)
        _totalQuantity.postValue((totalQuantity.value ?: MINIMUM_QUANTITY) + quantityToAdd)
    }

    private fun buildCatalogItems(): List<ProductCatalogItem> {
        val recent = recentProducts.value.orEmpty()
        val products = productItems.value.orEmpty()
        return buildList {
            if (recent.isNotEmpty()) {
                add(ProductCatalogItem.RecentProductsItem(recent))
            }
            addAll(products)
            if (hasNext) add(ProductCatalogItem.LoadMoreItem)
        }
    }

    companion object {
        private const val FIRST_PAGE = 0
        private const val PRODUCT_SIZE_LIMIT = 20
        private const val RECENT_PRODUCT_SIZE_LIMIT = 10
        private const val QUANTITY_TO_ADD = 1
        private const val MINIMUM_QUANTITY = 0
    }
}
