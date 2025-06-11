package woowacourse.shopping.view.product.catalog

import android.util.Log
import androidx.lifecycle.LiveData
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
import woowacourse.shopping.view.util.Error
import woowacourse.shopping.view.util.MutableSingleLiveData
import woowacourse.shopping.view.util.SingleLiveData

class ProductCatalogViewModel(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(),
    ProductCatalogEventHandler {
    private val productItems = mutableListOf<ProductCatalogItem.ProductItem>()
    private var recentProducts = emptyList<RecentProduct>()
    private val cartProducts = mutableSetOf<CartProduct>()
    private var page = FIRST_PAGE
    private var hasNext = false

    private val _productCatalogItems = MutableLiveData<List<ProductCatalogItem>>()
    val productCatalogItems: LiveData<List<ProductCatalogItem>> get() = _productCatalogItems

    private val _selectedProduct = MutableSingleLiveData<Product>()
    val selectedProduct: SingleLiveData<Product> get() = _selectedProduct

    private val _totalQuantity = MutableLiveData(MINIMUM_QUANTITY)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    private val _isFinishedLoading = MutableLiveData(false)
    val isFinishedLoading: LiveData<Boolean> get() = _isFinishedLoading

    private val _errorEvent = MutableSingleLiveData<Error>()
    val errorEvent: SingleLiveData<Error> get() = _errorEvent

    override fun onRecentProductClick(item: RecentProduct) {
        _selectedProduct.setValue(item.product)
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onAddClick(item: Product) {
        viewModelScope.launch {
            val result = cartProductRepository.insert(item.id, QUANTITY_STEP)

            result
                .onSuccess { cartProductId ->
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_STEP))
                    updateQuantity(item, QUANTITY_STEP)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToCart)
                }
        }
    }

    override fun onQuantityIncreaseClick(id: Int) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == id } ?: return
        val product = productItems.firstOrNull { it.product.id == id }?.product ?: return
        viewModelScope.launch {
            val result =
                cartProductRepository.updateQuantity(
                    cartProduct,
                    cartProduct.quantity + QUANTITY_STEP,
                )

            result
                .onSuccess {
                    cartProducts.removeIf { it.product.id == id }
                    cartProducts.add(cartProduct.copy(quantity = cartProduct.quantity + QUANTITY_STEP))
                    updateQuantity(product, QUANTITY_STEP)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToIncrease)
                }
        }
    }

    override fun onQuantityDecreaseClick(id: Int) {
        val cartProduct = cartProducts.firstOrNull { it.product.id == id } ?: return
        val product = productItems.firstOrNull { it.product.id == id }?.product ?: return
        viewModelScope.launch {
            val result =
                cartProductRepository.updateQuantity(
                    cartProduct,
                    cartProduct.quantity - QUANTITY_STEP,
                )

            result
                .onSuccess {
                    cartProducts.removeIf { it.product.id == id }
                    val newQuantity = cartProduct.quantity - QUANTITY_STEP
                    if (newQuantity > MINIMUM_QUANTITY) {
                        cartProducts.add(
                            cartProduct.copy(
                                quantity = newQuantity,
                            ),
                        )
                    }
                    updateQuantity(product, -QUANTITY_STEP)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToDecrease)
                }
        }
    }

    override fun onMoreClick() {
        page++
        loadMoreProducts()
    }

    fun loadCatalog() {
        _isFinishedLoading.value = false

        viewModelScope.launch {
            loadRecentProducts()
            loadCartProducts()

            val result = cartProductRepository.getTotalQuantity()

            result
                .onSuccess {
                    _totalQuantity.value = it
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private suspend fun loadCartProducts() {
        val result = cartProductRepository.getPagedProducts()

        result
            .onSuccess {
                cartProducts.clear()
                cartProducts.addAll(it.items)
                loadProducts()
            }.onFailure {
                Log.e("error", it.message.toString())
                _errorEvent.setValue(Error.FailToLoadProduct)
            }
    }

    private suspend fun loadRecentProducts() {
        val result = recentProductRepository.getPagedProducts(RECENT_PRODUCT_SIZE_LIMIT)

        result.onSuccess {
            recentProducts = it
            _productCatalogItems.value = buildCatalogItems()
        }.onFailure {
            Log.e("error", it.message.toString())
            _errorEvent.setValue(Error.FailToLoadProduct)
        }
    }

    private suspend fun loadProducts() {
        val result = productRepository.getPagedProducts(FIRST_PAGE, PRODUCT_SIZE_LIMIT * (page + 1))

        result
            .onSuccess { pagedResult ->
                productItems.clear()
                applyLoadedProducts(pagedResult)
            }.onFailure {
                Log.e("error", it.message.toString())
                _errorEvent.setValue(Error.FailToLoadProduct)
            }
    }

    private fun loadMoreProducts() {
        viewModelScope.launch {
            val result = productRepository.getPagedProducts(page, PRODUCT_SIZE_LIMIT)

            result
                .onSuccess { pagedResult ->
                    applyLoadedProducts(pagedResult)
                }.onFailure {
                    Log.e("error", it.message.toString())
                    _errorEvent.setValue(Error.FailToLoadProduct)
                }
        }
    }

    private fun applyLoadedProducts(pagedResult: PagedResult<Product>) {
        productItems.addAll(
            pagedResult.items.map { product ->
                val cartProduct = cartProducts.firstOrNull { it.product.id == product.id }
                ProductCatalogItem.ProductItem(
                    product,
                    cartProduct?.quantity ?: MINIMUM_QUANTITY,
                )
            },
        )
        hasNext = pagedResult.hasNext
        _isFinishedLoading.value = true
        _productCatalogItems.value = buildCatalogItems()
    }

    private fun updateQuantity(
        item: Product,
        quantityToAdd: Int,
    ) {
        val index = productItems.indexOfFirst { it.product.id == item.id }
        if (index != -1) {
            val currentItem = productItems[index]
            val updatedItem = currentItem.copy(quantity = currentItem.quantity + quantityToAdd)
            productItems[index] = updatedItem
        }
        _totalQuantity.value = (totalQuantity.value ?: MINIMUM_QUANTITY) + quantityToAdd
        _productCatalogItems.value = buildCatalogItems()
    }

    private fun buildCatalogItems(): List<ProductCatalogItem> =
        buildList {
            if (recentProducts.isNotEmpty()) {
                add(ProductCatalogItem.RecentProductsItem(recentProducts))
            }
            addAll(productItems)
            if (hasNext) {
                add(ProductCatalogItem.LoadMoreItem)
            }
        }

    companion object {
        private const val FIRST_PAGE = 0
        private const val PRODUCT_SIZE_LIMIT = 20
        private const val RECENT_PRODUCT_SIZE_LIMIT = 10
        private const val QUANTITY_STEP = 1
        private const val MINIMUM_QUANTITY = 0
    }
}
