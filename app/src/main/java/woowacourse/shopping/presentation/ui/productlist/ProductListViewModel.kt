package woowacourse.shopping.presentation.ui.productlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
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
import kotlin.concurrent.thread

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
    }

    private fun initLoad() {
        thread {
            showLoading(loadingProvider = LoadingProvider.SKELETON_LOADING)
            Thread.sleep(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            productListPagingSource.load().mapCatching { pagingProduct ->

                val carts = loadCarts()
                val products = updateProducts(pagingProduct.products, carts)
                val productHistories = loadProductHistories()
                val cartQuantity = loadCartQuantity()

                ProductListUiState(
                    pagingProduct = pagingProduct.copy(products = products),
                    productHistories = productHistories,
                    cartQuantity = cartQuantity,
                )
            }.onSuccess { productListUiState ->
                hideError()
                _uiState.postValue(productListUiState)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }

            Thread.sleep(1000) // TODO 스켈레톤 UI를 보여주기 위한 sleep..zzz
            hideLoading()
        }
    }

    private fun loadProductHistories(): List<Product> {
        return productHistoryRepository.getProductHistory(10).getOrDefault(emptyList())
    }

    private fun loadCartQuantity(): Int {
        return shoppingCartRepository.getCartItemsCount().getOrDefault(0)
    }

    private fun loadCarts(): Carts? {
        return shoppingCartRepository.getAllCarts().getOrNull()
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
        thread {
            productListPagingSource.load().onSuccess { pagingProduct ->
                hideError()
                val state = uiState.value ?: return@onSuccess
                val carts = loadCarts()
                val products = updateProducts(pagingProduct.products, carts)
                val cartQuantity = loadCartQuantity()

                val newPagingCart =
                    PagingProduct(
                        products = state.pagingProduct.products + products,
                        last = pagingProduct.last,
                    )
                _uiState.postValue(
                    state.copy(
                        pagingProduct = newPagingCart,
                        cartQuantity = cartQuantity,
                    ),
                )
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    fun updatePagingProduct() {
        thread {
            val state = uiState.value ?: return@thread
            val carts = loadCarts()
            val products = updateProducts(state.pagingProduct.products, carts)
            val productHistories = loadProductHistories()
            val cartQuantity = loadCartQuantity()

            _uiState.postValue(
                state.copy(
                    pagingProduct = PagingProduct(products),
                    productHistories = productHistories,
                    cartQuantity = cartQuantity,
                ),
            )
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
        thread {
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
        thread {
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
        thread {
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
