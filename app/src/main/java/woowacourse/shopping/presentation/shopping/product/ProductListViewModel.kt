package woowacourse.shopping.presentation.shopping.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import timber.log.Timber
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.LoadCartUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.shopping.toShoppingUiModel
import woowacourse.shopping.presentation.shopping.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData
import kotlin.concurrent.thread

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val loadCartUseCase: LoadCartUseCase,
    private val increaseCartProductUseCase: IncreaseCartProductUseCase,
    private val decreaseCartProductUseCase: DecreaseCartProductUseCase,
) : ViewModel(), ProductItemListener {
    private val _uiState = MutableLiveData(ProductListUiState(currentPage = START_PAGE))
    val uiState: LiveData<ProductListUiState> get() = _uiState

    private val _navigateToDetailEvent = MutableSingleLiveData<Long>()
    val navigateToDetailEvent: SingleLiveData<Long> = _navigateToDetailEvent
    private val _errorEvent = MutableSingleLiveData<ProductListErrorEvent>()
    val errorEvent: SingleLiveData<ProductListErrorEvent> = _errorEvent

    init {
        loadProducts()
        loadRecentProducts()
    }

    override fun loadProducts() {
        val uiState = _uiState.value ?: return
        val currentPage = uiState.currentPage
        productRepository.loadProducts(currentPage, PAGE_SIZE)
            .onSuccess {
                val newProducts = it.map(Product::toShoppingUiModel)
                val nextPage = currentPage.plus(1)
                _uiState.value =
                    uiState.copy(
                        products = uiState.products + newProducts,
                        loadMoreModel = getLoadMore(nextPage),
                    )
                loadCartProducts()
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(ProductListErrorEvent.LoadProducts)
            }
    }

    fun loadCartProducts() {
        val uiState = uiState.value ?: return
        val ids = uiState.products.map { it.id }
        loadCartUseCase(ids).onSuccess { newCart ->
            val newPage = uiState.currentPage + 1
            _uiState.value =
                uiState.updateCartProducts(
                    newCart,
                    getLoadMore(newPage),
                )
        }.onFailure {
            Timber.e(it)
            _errorEvent.postValue(ProductListErrorEvent.LoadCartProducts)
        }
    }

    fun loadRecentProducts() {
        val uiState = _uiState.value ?: return
        thread {
            productRepository.loadRecentProducts(RECENT_PRODUCT_COUNT).onSuccess {
                _uiState.postValue(uiState.copy(recentProducts = it.map(Product::toUiModel)))
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(ProductListErrorEvent.LoadRecentProducts)
            }
        }
    }

    override fun increaseProductCount(id: Long) {
        thread {
            val uiState = _uiState.value ?: return@thread
            increaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess { newCart ->
                _uiState.postValue(uiState.increaseProductCount(id, newCart))
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(ProductListErrorEvent.IncreaseCartCount)
            }
        }
    }

    override fun decreaseProductCount(id: Long) {
        thread {
            val uiState = _uiState.value ?: return@thread
            decreaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess { newCart ->
                _uiState.postValue(uiState.decreaseProductCount(id, newCart))
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(ProductListErrorEvent.DecreaseCartCount)
            }
        }
    }

    override fun navigateToDetail(id: Long) {
        _navigateToDetailEvent.setValue(id)
    }

    private fun getLoadMore(page: Int): ShoppingUiModel.LoadMore? {
        return if (productRepository.canLoadMore(page, PAGE_SIZE).getOrNull() == true) {
            ShoppingUiModel.LoadMore
        } else {
            null
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PAGE_SIZE = 20
        private const val INCREMENT_AMOUNT = 1
        private const val START_PAGE = 0

        fun factory(
            productRepository: ProductRepository,
            loadCartUseCase: LoadCartUseCase,
            increaseCartProductUseCase: IncreaseCartProductUseCase,
            decreaseCartProductUseCase: DecreaseCartProductUseCase,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                ProductListViewModel(
                    productRepository,
                    loadCartUseCase,
                    increaseCartProductUseCase,
                    decreaseCartProductUseCase,
                )
            }
        }
    }
}

private fun ProductListUiState.updateCartProducts(
    cart: Cart,
    loadMore: ShoppingUiModel.LoadMore?,
): ProductListUiState {
    return copy(
        cart = cart,
        products =
            products.map { originalProduct ->
                val cartProduct =
                    cart.findCartProductByProductId(originalProduct.id)
                        ?: return@map originalProduct.copy(count = 0)
                originalProduct.copy(count = cartProduct.count)
            },
        loadMoreModel = loadMore,
    )
}

private fun ProductListUiState.increaseProductCount(
    productId: Long,
    newCart: Cart,
): ProductListUiState {
    val newProduct = newCart.findCartProductByProductId(productId) ?: return this
    return copy(
        cart = newCart,
        products =
            products.map {
                if (it.id == productId) {
                    it.copy(count = newProduct.count)
                } else {
                    it
                }
            },
    )
}

private fun ProductListUiState.decreaseProductCount(
    productId: Long,
    newCart: Cart,
): ProductListUiState {
    val newProduct =
        newCart.findCartProductByProductId(productId) ?: return copy(
            newCart,
            products =
                products.map {
                    if (it.id == productId) {
                        it.copy(count = 0)
                    } else {
                        it
                    }
                },
        )
    return copy(
        cart = newCart,
        products =
            products.map {
                if (it.id == productId) {
                    it.copy(count = newProduct.count)
                } else {
                    it
                }
            },
    )
}
