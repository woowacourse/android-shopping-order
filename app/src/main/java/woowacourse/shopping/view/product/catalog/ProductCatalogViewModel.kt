package woowacourse.shopping.view.product.catalog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val _onFinishLoading = MutableLiveData(false)
    val onFinishLoading: LiveData<Boolean> get() = _onFinishLoading

    override fun onRecentProductClick(item: RecentProduct) {
        _selectedProduct.setValue(item.product)
    }

    override fun onProductClick(item: Product) {
        _selectedProduct.setValue(item)
    }

    override fun onAddClick(item: Product) {
        cartProductRepository.insert(item.id, QUANTITY_TO_ADD) { result ->
            result
                .onSuccess { cartProductId ->
                    cartProducts.add(CartProduct(cartProductId, item, QUANTITY_TO_ADD))
                    updateQuantity(item, QUANTITY_TO_ADD)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    override fun onQuantityIncreaseClick(item: Product) {
        val cartProduct = cartProducts.first { it.product.id == item.id }
        cartProductRepository.updateQuantity(cartProduct, QUANTITY_TO_ADD) {
            cartProducts.removeIf { it.product.id == item.id }
            cartProducts.add(cartProduct.copy(quantity = cartProduct.quantity + QUANTITY_TO_ADD))
            updateQuantity(item, QUANTITY_TO_ADD)
        }
    }

    override fun onQuantityDecreaseClick(item: Product) {
        val cartProduct = cartProducts.first { it.product.id == item.id }
        cartProductRepository.updateQuantity(cartProduct, -QUANTITY_TO_ADD) {
            cartProducts.removeIf { it.product.id == item.id }
            val newQuantity = cartProduct.quantity - QUANTITY_TO_ADD
            if (newQuantity > MINIMUM_QUANTITY) cartProducts.add(cartProduct.copy(quantity = newQuantity))
            updateQuantity(item, -QUANTITY_TO_ADD)
        }
    }

    override fun onMoreClick() {
        page++
        loadProducts()
    }

    fun loadCatalog() {
        _onFinishLoading.value = false
        loadRecentProducts()
        loadCartProducts()
        cartProductRepository.getTotalQuantity { result ->
            result
                .onSuccess {
                    _totalQuantity.postValue(it)
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun loadCartProducts() {
        cartProductRepository.getPagedProducts { result ->
            result
                .onSuccess {
                    cartProducts.clear()
                    cartProducts.addAll(it.items)
                    loadProducts()
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
    }

    private fun loadRecentProducts() {
        recentProductRepository.getPagedProducts(RECENT_PRODUCT_SIZE_LIMIT) { result ->
            result.onSuccess {
                recentProducts = it
                _productCatalogItems.postValue(buildCatalogItems())
            }
        }
    }

    private fun loadProducts() {
        productRepository.getPagedProducts(page, PRODUCT_SIZE_LIMIT) { result ->
            result
                .onSuccess { pagedResult ->
                    pagedResult.items.forEach { product ->
                        val cartProduct = cartProducts.firstOrNull { it.product.id == product.id }
                        productItems.add(
                            ProductCatalogItem.ProductItem(
                                product,
                                cartProduct?.quantity ?: MINIMUM_QUANTITY,
                            ),
                        )
                    }
                    hasNext = pagedResult.hasNext
                    _onFinishLoading.postValue(true)
                    _productCatalogItems.postValue(buildCatalogItems())
                }.onFailure {
                    Log.e("error", it.message.toString())
                }
        }
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
        _totalQuantity.postValue((totalQuantity.value ?: MINIMUM_QUANTITY) + quantityToAdd)
        _productCatalogItems.postValue(buildCatalogItems())
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
        private const val QUANTITY_TO_ADD = 1
        private const val MINIMUM_QUANTITY = 0
    }
}
