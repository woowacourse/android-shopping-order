package woowacourse.shopping.view.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
    private val productsRepository: ProductsRepository = DefaultProductsRepository.Companion.get(),
    private val shoppingCartRepository: ShoppingCartRepository = DefaultShoppingCartRepository.Companion.get(),
) : ViewModel() {
    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val _shoppingCartQuantity: MutableLiveData<Int> = MutableLiveData(0)
    val shoppingCartQuantity: LiveData<Int> get() = _shoppingCartQuantity

    private var loadable: Boolean = false
    private var page: Int = MINIMUM_PAGE

    private val _productsUi: MutableLiveData<List<ProductsItem.ProductItem>> =
        MutableLiveData(emptyList())
    val productsUi: LiveData<List<ProductsItem.ProductItem>> get() = _productsUi

    private var shoppingCartDomain = emptyList<ShoppingCartProduct>()
    private var productsDomain = emptyList<Product>()

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.postValue(ProductsEvent.UPDATE_PRODUCT_FAILURE)
            _isLoading.value = false
        }

    init {
        updateProducts()
        updateShoppingCartQuantity()
    }

    fun reload() {
        _isLoading.value = true
        _productsUi.value = emptyList()
        shoppingCartDomain = emptyList()

        updateProducts(0, LOAD_PRODUCTS_SIZE * page)
        updateShoppingCartQuantity()
    }

    fun updateProducts(
        offset: Int = page - 1,
        size: Int = LOAD_PRODUCTS_SIZE,
    ) {
        viewModelScope.launch {
            productsDomain =
                async(handler) {
                    productsRepository.load(offset, size)
                }.await()

            loadable = productsDomain.size == LOAD_PRODUCTS_SIZE
            updateProductsShoppingCartQuantity(this)
            _isLoading.value = false
        }
    }

    fun updateShoppingCartQuantity() {
        viewModelScope.launch {
            _shoppingCartQuantity.value = shoppingCartRepository.fetchAllQuantity()
        }
    }

    fun addProductToShoppingCart(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        updateProductQuantity(productItem, quantity + 1)
        _shoppingCartQuantity.value = _shoppingCartQuantity.value?.plus(1)
    }

    fun minusProductToShoppingCart(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        updateProductQuantity(productItem, quantity - 1)
        _shoppingCartQuantity.value = _shoppingCartQuantity.value?.minus(1)
    }

    fun updateMoreProducts() {
        page++
        updateProducts()
    }

    private fun updateProductQuantity(
        productItem: ProductsItem.ProductItem,
        quantity: Int,
    ) {
        viewModelScope.launch(handler) {
            val uploaded =
                if (productItem.shoppingCartId == null) {
                    shoppingCartRepository.add(productItem.product, quantity)
                } else {
                    shoppingCartRepository.updateQuantity(
                        productItem.shoppingCartId,
                        quantity,
                    )
                }

            _productsUi.value =
                productsUi.value?.map { item ->
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
    }

    private fun updateProductsShoppingCartQuantity(scope: CoroutineScope) {
        scope.launch(handler) {
            shoppingCartDomain = shoppingCartRepository.load().shoppingCartItems
            val productUi =
                productsDomain.map { product ->
                    val target = shoppingCartDomain.find { it.product.id == product.id }

                    ProductsItem.ProductItem(
                        shoppingCartId = target?.id,
                        product = product,
                        selectedQuantity = target?.quantity ?: 0,
                    )
                }

            _productsUi.value = _productsUi.value?.plus(productUi)
        }
    }

    companion object {
        private const val MINIMUM_PAGE = 1
        private const val LOAD_PRODUCTS_SIZE = 20
    }
}
