package woowacourse.shopping.view.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.data.shoppingCart.repository.DefaultShoppingCartRepository
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.product.ProductsEvent
import woowacourse.shopping.view.product.ProductsItem

class ProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.get(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _shoppingCartQuantity: MutableLiveData<Int> = MutableLiveData(0)
    val shoppingCartQuantity: LiveData<Int> get() = _shoppingCartQuantity

    private var loadable: Boolean = false
    private var page: Int = MINIMUM_PAGE

    private val _products: MutableLiveData<List<ProductsItem>> =
        MutableLiveData(emptyList())
    val products: LiveData<List<ProductsItem>> get() = _products

    private var shoppingCartDomain = emptyList<ShoppingCartProduct>()
    private var productsDomain = emptyList<Product>()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var isApiLoading: Boolean = false

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
            _isLoading.value = false
            isApiLoading = false
        }

    init {
        updateProducts()
        updateShoppingCartQuantity()
    }

    fun reload() {
        _isLoading.value = true
        _products.value = emptyList()
        shoppingCartDomain = emptyList()

        updateProducts(0, LOAD_PRODUCTS_SIZE * page)
        updateShoppingCartQuantity()
    }

    private fun updateProducts(
        offset: Int = page - 1,
        size: Int = LOAD_PRODUCTS_SIZE,
    ) {
        viewModelScope.launch(handler) {
            productsDomain = productsRepository.load(offset, size).getOrThrow()
            loadable = productsDomain.size == LOAD_PRODUCTS_SIZE
            updateProductsShoppingCartQuantity()
            _isLoading.value = false
        }
    }

    private fun updateShoppingCartQuantity() {
        viewModelScope.launch(handler) {
            _shoppingCartQuantity.value = shoppingCartRepository.fetchAllQuantity().getOrThrow()
        }
    }

    fun addProductToShoppingCart(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        if (isApiLoading) return
        isApiLoading = true
        viewModelScope.launch(handler) {
            updateProductQuantity(productItem, quantity + 1)
            _shoppingCartQuantity.value = _shoppingCartQuantity.value?.plus(1)
            isApiLoading = false
        }
    }

    fun minusProductToShoppingCart(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        if (isApiLoading) return
        isApiLoading = true
        viewModelScope.launch(handler) {
            updateProductQuantity(productItem, quantity - 1)
            _shoppingCartQuantity.value = _shoppingCartQuantity.value?.minus(1)
            isApiLoading = false
        }
    }

    fun updateMoreProducts() {
        page++
        updateProducts()
    }

    private suspend fun updateProductQuantity(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        val uploaded =
            if (productItem.shoppingCartId == null) {
                shoppingCartRepository.add(productItem.product, quantity).getOrThrow()
            } else {
                shoppingCartRepository.updateQuantity(
                    productItem.shoppingCartId,
                    quantity,
                ).getOrThrow()
            }

        _products.value =
            products.value
                ?.filterIsInstance<ProductsItem.ProductItem>()
                ?.map { item ->
                    if (item.product.id == productItem.product.id) {
                        item.copy(
                            selectedQuantity = uploaded?.quantity ?: 0,
                            shoppingCartId = uploaded?.id,
                        )
                    } else {
                        item
                    }
                }
    }

    private suspend fun updateProductsShoppingCartQuantity() {
        shoppingCartDomain = shoppingCartRepository.load().getOrThrow().shoppingCartItems
        val productUi =
            productsDomain.map { product ->
                val target = shoppingCartDomain.find { it.product.id == product.id }

                ProductsItem.ProductItem(
                    shoppingCartId = target?.id,
                    product = product,
                    selectedQuantity = target?.quantity ?: 0,
                )
            }

        _products.value =
            _products.value
                ?.filterIsInstance<ProductsItem.ProductItem>()
                ?.plus(productUi)
                ?.plus(ProductsItem.LoadItem)
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val LOAD_PRODUCTS_SIZE = 20
    }
}
