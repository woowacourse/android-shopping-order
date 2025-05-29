package woowacourse.shopping.view.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.product.ProductsItem.LoadItem
import woowacourse.shopping.view.product.ProductsItem.ProductItem
import woowacourse.shopping.view.product.ProductsItem.RecentWatchingItem

class ProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
) : ViewModel() {
    private val _products: MutableLiveData<List<ProductsItem>> = MutableLiveData(emptyList())
    val products: LiveData<List<ProductsItem>> get() = _products

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _shoppingCartQuantity: MutableLiveData<Int> = MutableLiveData(0)
    val shoppingCartQuantity: LiveData<Int> get() = _shoppingCartQuantity

    private var loadable: Boolean = false
    private var page: Int = MINIMUM_PAGE

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        updateProducts()
        updateShoppingCartQuantity()
    }

    fun updateProducts() {
        val offset = page - 1
        val limit = LOAD_PRODUCTS_SIZE + 1
        val currentProducts: List<ProductsItem> = _products.value ?: emptyList()

        productsRepository.load(offset, limit) { result ->
            result
                .onSuccess { newProducts ->
                    loadable = newProducts.size == LOAD_PRODUCTS_SIZE + 1
                    val productsToShow = newProducts.take(LOAD_PRODUCTS_SIZE)

                    updateProductsShoppingCartQuantity(productsToShow, currentProducts)
                    _isLoading.value = false
                }.onFailure {
                    _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
                    _isLoading.value = false
                }
        }
    }

    private fun updateProductsShoppingCartQuantity(
        productsToShow: List<Product>,
        currentProducts: List<ProductsItem>,
    ) {
        var shoppingCartProducts = emptyList<ShoppingCartProduct>()
        shoppingCartRepository.load(0, 20) { result ->
            result
                .onSuccess {
                    shoppingCartProducts = it
                    handleShoppingCartQuantitySuccess(
                        currentProducts,
                        productsToShow,
                        shoppingCartProducts,
                    )
                }.onFailure {
                    handleShoppingCartQuantitySuccess(
                        currentProducts,
                        productsToShow,
                        shoppingCartProducts,
                    )
                }
        }
    }

    private fun handleShoppingCartQuantitySuccess(
        currentProducts: List<ProductsItem>,
        productsToShow: List<Product>,
        shoppingCartProducts: List<ShoppingCartProduct>,
    ) {
        val productsWithoutLoadItem =
            if (currentProducts.lastOrNull() is LoadItem) {
                currentProducts.dropLast(1)
            } else {
                currentProducts
            }

        val updatedProductItems =
            productsToShow.map { product ->
                val matchingCartProduct = shoppingCartProducts.find { it.product == product }

                ProductItem(
                    shoppingCartId = matchingCartProduct?.id,
                    product = product,
                    selectedQuantity = matchingCartProduct?.quantity ?: 0,
                )
            }

        val hasRecentWatching = currentProducts.any { it is RecentWatchingItem }

        updateRecentProducts(
            productsWithoutLoadItem,
            updatedProductItems,
            hasRecentWatching,
        )
    }

    private fun updateRecentProducts(
        productsWithoutLoadItem: List<ProductsItem>,
        updatedProductItems: List<ProductItem>,
        hasRecentWatching: Boolean,
    ) {
        productsRepository.getRecentWatchingProducts(10) { result ->
            result
                .onSuccess { recentWatchingProducts ->
                    var updatedProducts = emptyList<ProductsItem>()
                    if (hasRecentWatching) {
                        updatedProducts = productsWithoutLoadItem.drop(1)
                    }
                    handleRecentProductsSuccess(
                        recentWatchingProducts = recentWatchingProducts,
                        productsWithoutLoadItem = updatedProducts,
                        updatedProductItems = updatedProductItems,
                    )
                }.onFailure {
                    handleRecentProductsSuccess(
                        emptyList(),
                        productsWithoutLoadItem,
                        updatedProductItems,
                    )
                    _event.postValue(ProductsEvent.UPDATE_RECENT_WATCHING_PRODUCTS_FAILURE)
                }
        }
    }

    private fun handleRecentProductsSuccess(
        recentWatchingProducts: List<Product>,
        productsWithoutLoadItem: List<ProductsItem>,
        updatedProductItems: List<ProductItem>,
    ) {
        val recentWatchingItem: RecentWatchingItem? =
            if (recentWatchingProducts.isEmpty()) {
                null
            } else {
                val items =
                    recentWatchingProducts.map { product ->
                        updatedProductItems.find { it.product.id == product.id }
                            ?: ProductItem(product = product)
                    }
                RecentWatchingItem(items)
            }
        updateNewProducts(
            productsWithoutLoadItem,
            updatedProductItems,
            recentWatchingItem,
        )
    }

    private fun updateNewProducts(
        productsWithoutLoadItem: List<ProductsItem>,
        updatedProductItems: List<ProductItem>,
        recentWatchingItem: ProductsItem?,
    ) {
        //
        if (productsWithoutLoadItem.isEmpty() || page != 1) {
            _products.postValue(
                buildList {
                    recentWatchingItem?.let { add(it) }
                    addAll(productsWithoutLoadItem)
                    addAll(updatedProductItems)
                    if (loadable) add(LoadItem)
                },
            )
            return
        }

        // 이게 업데이트 로직
        val mergedProducts =
            productsWithoutLoadItem.map { item: ProductsItem ->
                if (item is ProductItem) {
                    val updated = updatedProductItems.find { it.product.id == item.product.id }
                    updated ?: item
                } else {
                    item
                }
            }

        _products.postValue(
            buildList {
                recentWatchingItem?.let { add(it) }
                addAll(mergedProducts)
                if (loadable) add(LoadItem)
            },
        )
    }

    fun updateShoppingCartQuantity() {
        shoppingCartRepository.fetchAllQuantity { result ->
            result.onSuccess { quantity: Int ->
                _shoppingCartQuantity.postValue(quantity)
            }
        }
    }

    fun updateRecentWatching() {
        productsRepository.getRecentWatchingProducts(10) { result ->
            result
                .onSuccess { recentWatchingProducts: List<Product> ->
                    val recentWatchingItem =
                        if (recentWatchingProducts.isEmpty()) {
                            null
                        } else {
                            val items =
                                recentWatchingProducts.map { ProductItem(product = it) }
                            RecentWatchingItem(items)
                        }

                    val currentProducts = _products.value ?: return@onSuccess
                    val withoutOldRecentWatching =
                        currentProducts.filterNot { it is RecentWatchingItem }

                    val updatedProducts =
                        buildList {
                            recentWatchingItem?.let { add(it) }
                            addAll(withoutOldRecentWatching)
                        }

                    _products.postValue(updatedProducts)
                }.onFailure {
                    _event.postValue(ProductsEvent.UPDATE_RECENT_WATCHING_PRODUCTS_FAILURE)
                }
        }
    }

    fun addProductToShoppingCart(
        productItem: ProductItem,
        quantity: Int,
    ) {
        if (productItem.shoppingCartId == null) {
            shoppingCartRepository.add(productItem.product, quantity + 1) { result ->
                result
                    .onSuccess {
                        val currentProducts = products.value?.toMutableList() ?: return@add
                        val index: Int =
                            currentProducts.indexOfFirst { it is ProductItem && it.product == productItem.product }

                        if (index != -1) {
                            shoppingCartRepository.load(page - 1, LOAD_PRODUCTS_SIZE) { result ->
                                result.onSuccess { shoppingCartProducts ->
                                    val newShoppingCartId =
                                        shoppingCartProducts
                                            .find {
                                                it.product.id == productItem.product.id
                                            }?.id

                                    val productItem = currentProducts[index] as ProductItem
                                    val updatedItem =
                                        productItem.copy(
                                            selectedQuantity = productItem.selectedQuantity + 1,
                                            shoppingCartId = newShoppingCartId,
                                        )

                                    currentProducts[index] = updatedItem
                                    _products.value = currentProducts
                                }
                            }
                        }
                        _shoppingCartQuantity.value = shoppingCartQuantity.value?.plus(1)
                    }.onFailure {
                        _event.postValue(ProductsEvent.NOT_ADD_TO_SHOPPING_CART)
                    }
            }
            return
        }
        shoppingCartRepository.increaseQuantity(
            productItem.shoppingCartId,
            quantity + 1,
        ) { result ->
            result
                .onSuccess {
                    val currentProducts = products.value?.toMutableList() ?: return@onSuccess
                    val index: Int =
                        currentProducts.indexOfFirst { it is ProductItem && it.product == productItem.product }

                    if (index != -1) {
                        val productItem = currentProducts[index] as ProductItem
                        val updatedItem =
                            productItem.copy(selectedQuantity = productItem.selectedQuantity + 1)
                        currentProducts[index] = updatedItem
                        _products.value = currentProducts
                    }
                    _shoppingCartQuantity.value = shoppingCartQuantity.value?.plus(1)
                }.onFailure {
                    _event.postValue(ProductsEvent.NOT_ADD_TO_SHOPPING_CART)
                }
        }
    }

    fun minusProductToShoppingCart(
        productItem: ProductItem,
        quantity: Int,
    ) {
        shoppingCartRepository.decreaseQuantity(
            productItem.shoppingCartId ?: return,
            quantity - 1,
        ) { result ->
            result
                .onSuccess {
                    val currentProducts = products.value?.toMutableList() ?: return@decreaseQuantity
                    val index: Int =
                        currentProducts.indexOfFirst { it is ProductItem && it.product == productItem.product }

                    if (index != -1) {
                        val productItem = currentProducts[index] as ProductItem
                        val updatedItem =
                            productItem.copy(selectedQuantity = productItem.selectedQuantity - 1)
                        currentProducts[index] = updatedItem
                        _products.postValue(currentProducts)
                    }
                    _shoppingCartQuantity.postValue(shoppingCartQuantity.value?.minus(1))
                }.onFailure {
                    _event.postValue(ProductsEvent.NOT_MINUS_TO_SHOPPING_CART)
                }
        }
    }

    fun updateMoreProducts() {
        page++
        updateProducts()
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val LOAD_PRODUCTS_SIZE = 20
    }
}
