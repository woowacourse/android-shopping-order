package woowacourse.shopping.view.product.catalog

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

    private val _totalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = _totalQuantity

    override fun onRecentProductClick(item: RecentProduct) {
        _selectedProduct.setValue(item.product)
    }

    override fun onProductClick(item: ProductCatalogItem.ProductItem) {
        _selectedProduct.setValue(item.product)
    }

    override fun onAddClick(item: ProductCatalogItem.ProductItem) {
        cartProductRepository.insert(item.product.id, 1) { cartProductId ->
            cartProducts.add(CartProduct(cartProductId, item.product, 1))
            updateQuantity(item, 1)
        }
    }

    override fun onQuantityIncreaseClick(item: ProductCatalogItem.ProductItem) {
        val cartProduct = cartProducts.first { it.product.id == item.product.id }
        val newQuantity = cartProduct.quantity + 1
        cartProductRepository.updateQuantity(cartProduct.product.id, 1) {
            cartProducts.removeIf { it.product.id == item.product.id }
            cartProducts.add(cartProduct.copy(quantity = newQuantity))
            updateQuantity(item, newQuantity)
        }
    }

    override fun onQuantityDecreaseClick(item: ProductCatalogItem.ProductItem) {
        val cartProduct = cartProducts.first { it.product.id == item.product.id }
        val newQuantity = cartProduct.quantity - 1
        cartProductRepository.updateQuantity(cartProduct.product.id, -1) {
            cartProducts.removeIf { it.product.id == item.product.id }
            if (newQuantity > 0) cartProducts.add(cartProduct.copy(quantity = newQuantity))
            updateQuantity(item, newQuantity)
        }
    }

    override fun onMoreClick() {
        loadProducts()
    }

    fun loadCatalog() {
        loadRecentProducts()
        loadCartProducts()
        cartProductRepository.getTotalQuantity {
            _totalQuantity.postValue(it)
        }
    }

    private fun loadCartProducts() {
        cartProductRepository.getPagedProducts {
            cartProducts.clear()
            cartProducts.addAll(it.items)
            loadProducts()
        }
    }

    private fun loadRecentProducts() {
        recentProductRepository.getPagedProducts(RECENT_PRODUCT_SIZE_LIMIT) {
            recentProducts = it
            _productCatalogItems.postValue(buildCatalogItems())
        }
    }

    private fun loadProducts() {
        productRepository.getPagedProducts(page, PRODUCT_SIZE_LIMIT) { result ->
            result.items.forEach { product ->
                val cartProduct = cartProducts.firstOrNull { it.product.id == product.id }
                productItems.add(
                    ProductCatalogItem.ProductItem(product, cartProduct?.quantity ?: 0),
                )
            }
            hasNext = result.hasNext
            _productCatalogItems.postValue(buildCatalogItems())

            if (result.items.isNotEmpty()) page++
        }
    }

    private fun updateQuantity(
        item: ProductCatalogItem.ProductItem,
        newQuantity: Int,
    ) {
        val index = productItems.indexOfFirst { it.product.id == item.product.id }
        if (index != -1) {
            productItems[index] = productItems[index].copy(quantity = newQuantity)
        }
        _totalQuantity.postValue((totalQuantity.value ?: 0) + (newQuantity - item.quantity))
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
    }
}
