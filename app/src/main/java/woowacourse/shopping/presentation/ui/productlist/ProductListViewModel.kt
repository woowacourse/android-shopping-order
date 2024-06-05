package woowacourse.shopping.presentation.ui.productlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import woowacourse.shopping.domain.model.CartItemId
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.LoadingProvider
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.model.ProductItemId
import woowacourse.shopping.presentation.ui.productlist.adapter.ProductListPagingSource

class ProductListViewModel(
    productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val productHistoryRepository: ProductHistoryRepository,
) : BaseViewModel(), ProductListActionHandler, ProductCountHandler {
    private val _uiState: MutableLiveData<ProductListUiState> =
        MutableLiveData(ProductListUiState())
    val uiState: LiveData<ProductListUiState> get() = _uiState

    private val _navigateAction: MutableLiveData<Event<ProductListNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<ProductListNavigateAction>> get() = _navigateAction

    private val shoppingCart: MutableMap<ProductItemId, CartItemId?> = mutableMapOf()

    private val productListPagingSource =
        ProductListPagingSource(productRepository = productRepository)

    init {
        initLoad()
        loadProductHistories()
        loadCartQuantity()
    }

    private fun initLoad() {
        launch {
            showLoading(loadingProvider = LoadingProvider.SKELETON_LOADING)
            delay(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            productListPagingSource.load().mapCatching { pagingProduct ->
                val carts = shoppingCartRepository.getAllCarts().getOrNull()
                val products = updateProducts(pagingProduct.products, carts)
                pagingProduct.copy(products = products)
            }.onSuccess { pagingProduct ->
                hideError()
                val state = uiState.value ?: return@launch
                _uiState.postValue(state.copy(pagingProduct = pagingProduct))
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
            delay(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            hideLoading()
        }
    }

    private fun loadProductHistories() {
        launch {
            productHistoryRepository.getProductHistory(10).onSuccess { productHistories ->
                hideError()
                val state = uiState.value ?: return@launch
                _uiState.postValue(state.copy(productHistories = productHistories))
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun loadCartQuantity() {
        launch {
            shoppingCartRepository.getCartItemsCount().onSuccess { cartQuantity ->
                hideError()
                val state = uiState.value ?: return@launch
                _uiState.postValue(state.copy(cartQuantity = cartQuantity))
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun retry() {
        loadMoreProducts()
    }

    override fun navigateToProductDetail(productId: Long) {
        _navigateAction.emit(ProductListNavigateAction.NavigateToProductDetail(productId = productId))
    }

    fun navigateToShoppingCart() {
        _navigateAction.emit(ProductListNavigateAction.NavigateToShoppingCart)
    }

    override fun loadMoreProducts() {
        launch {
            productListPagingSource.load().onSuccess { pagingProduct ->
                hideError()
                val state = uiState.value ?: return@onSuccess
                val carts = shoppingCartRepository.getAllCarts().getOrNull()
                val products = updateProducts(pagingProduct.products, carts)
                loadCartQuantity()

                val newPagingCart =
                    PagingProduct(
                        products = state.pagingProduct.products + products,
                        last = pagingProduct.last,
                    )

                _uiState.postValue(state.copy(pagingProduct = newPagingCart))
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    fun updatePagingProduct() {
        launch {
            val state = uiState.value ?: return@launch
            val carts = shoppingCartRepository.getAllCarts().getOrNull()
            val products = updateProducts(state.pagingProduct.products, carts)
            loadProductHistories()
            loadCartQuantity()
            _uiState.postValue(state.copy(pagingProduct = PagingProduct(products)))
        }
    }

    private fun updateProducts(
        products: List<Product>,
        carts: Carts?,
    ): List<Product> {
        return products.map { product ->
            val cart = carts?.content?.find { it.product.id == product.id }
            if (cart == null) {
                shoppingCart[ProductItemId(product.id)] = null
                product.copy(quantity = 0)
            } else {
                shoppingCart[ProductItemId(product.id)] = CartItemId(cart.id)
                product.copy(quantity = cart.quantity)
            }
        }
    }

    override fun plusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = true)
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        updateProductQuantity(productId, increment = false)
    }

    private fun updateProductQuantity(
        productId: Long,
        increment: Boolean,
    ) {
        val state = uiState.value ?: return

        val updatedProductList =
            calculateUpdateProducts(state.pagingProduct.products, productId, increment)
        val updatedCartCount = updatedProductList.sumOf { it.quantity }
        _uiState.postValue(
            state.copy(
                pagingProduct = PagingProduct(updatedProductList),
                cartQuantity = updatedCartCount,
            ),
        )
    }

    private fun calculateUpdateProducts(
        products: List<Product>,
        productId: Long,
        increment: Boolean,
    ): List<Product> {
        return products.map { product ->
            if (product.id == productId) {
                val updatedQuantity = calculateUpdateQuantity(product.quantity, increment)
                updateCart(productId, updatedQuantity)
                product.copy(quantity = updatedQuantity)
            } else {
                product
            }
        }
    }

    private fun calculateUpdateQuantity(
        currentQuantity: Int,
        increment: Boolean,
    ): Int {
        return if (increment) currentQuantity + 1 else currentQuantity - 1
    }

    private fun updateCart(
        productId: Long,
        updatedQuantity: Int,
    ) {
        val cartItemId = shoppingCart[ProductItemId(productId)]
        if (cartItemId == null) {
            insertCartProduct(productId, updatedQuantity)
            return
        }

        if (updatedQuantity <= 0) {
            deleteCartProduct(productId, cartItemId.id)
        } else {
            updateCartProduct(cartItemId.id, updatedQuantity)
        }
    }

    private fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ) {
        launch {
            shoppingCartRepository.postCartItem(
                productId = productId,
                quantity = quantity,
            ).onSuccess { cartItemId ->
                hideError()
                shoppingCart[ProductItemId(productId)] = cartItemId
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun deleteCartProduct(
        productId: Long,
        cartId: Int,
    ) {
        launch {
            shoppingCartRepository.deleteCartItem(
                cartId = cartId,
            ).onSuccess {
                hideError()
                shoppingCart[ProductItemId(productId)] = null
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    private fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ) {
        launch {
            shoppingCartRepository.patchCartItem(
                cartId = cartId,
                quantity = quantity,
            ).onSuccess {
                hideError()
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    companion object {
        fun factory(
            productRepository: ProductRepository,
            shoppingCartRepository: ShoppingCartRepository,
            productHistoryRepository: ProductHistoryRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                ProductListViewModel(
                    productRepository,
                    shoppingCartRepository,
                    productHistoryRepository,
                )
            }
        }
    }
}
