package woowacourse.shopping.presentation.shopping.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    private var productLoadJob: Job? = null

    init {
        loadProducts()
        loadCartProducts()
        loadRecentProducts()
    }

    override fun loadProducts() {
        productLoadJob = viewModelScope.launch {
            val currentPage = _uiState.value?.currentPage ?: return@launch
            _uiState.value = _uiState.value?.copy(isLoading = true)
            productRepository.loadProducts(currentPage, PAGE_SIZE)
                .onSuccess {
                    val uiState = _uiState.value ?: return@onSuccess
                    val newProducts = it.map(Product::toShoppingUiModel)
                    val nextPage = currentPage.plus(1)
                    _uiState.value =
                        uiState.copy(
                            products = uiState.products + newProducts,
                            loadMoreModel = getLoadMore(nextPage),
                            isLoading = false,
                        )
                }.onFailure {
                    Timber.e(it)
                    val uiState = _uiState.value ?: return@onFailure
                    _uiState.value = uiState.copy(isLoading = false)
                    _errorEvent.postValue(ProductListErrorEvent.LoadProducts)
                }
            Timber.e("finish productLoadJob")
        }
    }

    fun loadCartProducts() {
        viewModelScope.launch {
            // 아직 Product 가 최초 로드되지 않았으면 대기
            if (productLoadJob?.isActive == true) productLoadJob?.join()
            val ids = uiState.value?.products?.map { it.id } ?: return@launch
            loadCartUseCase(ids).onSuccess { newCart ->
                val uiState = uiState.value ?: return@onSuccess
                val nextPage = uiState.currentPage + 1
                _uiState.value =
                    uiState.updateCartProducts(
                        newCart,
                        getLoadMore(nextPage),
                    )
            }.onFailure {
                _errorEvent.setValue(ProductListErrorEvent.LoadCartProducts)
            }
        }
    }

    fun loadRecentProducts() {
        viewModelScope.launch {
            productRepository.loadRecentProducts(RECENT_PRODUCT_COUNT).onSuccess {
                val uiState = _uiState.value ?: return@launch
                _uiState.value = uiState.copy(recentProducts = it.map(Product::toUiModel))
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(ProductListErrorEvent.LoadRecentProducts)
            }
        }
    }

    override fun increaseProductCount(id: Long) {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            increaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess { newCart ->
                _uiState.value = uiState.increaseProductCount(id, newCart)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(ProductListErrorEvent.IncreaseCartCount)
            }
        }
    }

    override fun decreaseProductCount(id: Long) {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            decreaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess { newCart ->
                _uiState.value = uiState.decreaseProductCount(id, newCart)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(ProductListErrorEvent.DecreaseCartCount)
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
        products = products.map { originalProduct ->
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
