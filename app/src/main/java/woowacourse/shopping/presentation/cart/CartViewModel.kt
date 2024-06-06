package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.usecase.DecreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.DeleteCartProductUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductUseCase
import woowacourse.shopping.domain.usecase.LoadCartUseCase
import woowacourse.shopping.domain.usecase.LoadPagingCartUseCase
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class CartViewModel(
    private val cartRepository: CartRepository,
    private val increaseCartProductUseCase: IncreaseCartProductUseCase,
    private val decreaseCartProductUseCase: DecreaseCartProductUseCase,
    private val deleteCartProductUseCase: DeleteCartProductUseCase,
    private val loadCartUseCase: LoadCartUseCase,
    private val loadPagingCartUseCase: LoadPagingCartUseCase,
) : ViewModel(), CartProductListener {
    private val _uiState = MutableLiveData<CartUiState>(CartUiState())
    val uiState: LiveData<CartUiState> get() = _uiState
    private val _errorEvent = MutableSingleLiveData<CartErrorEvent>()
    val errorEvent: SingleLiveData<CartErrorEvent> = _errorEvent
    private val _updateCartEvent = MutableSingleLiveData<Unit>()
    val updateCartEvent: SingleLiveData<Unit> get() = _updateCartEvent
    private val _navigateToRecommendEvent: MutableSingleLiveData<List<CartProductUi>> =
        MutableSingleLiveData()
    val navigateToRecommendEvent: SingleLiveData<List<CartProductUi>> get() = _navigateToRecommendEvent

    init {
        loadTotalCartProducts()
    }

    override fun increaseProductCount(id: Long) {
        viewModelScope.launch {
            increaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess {
                val uiState = _uiState.value ?: return@launch
                val newUiState = uiState.updateCart(PAGE_SIZE, it)
                updateUiState(newUiState)
                _updateCartEvent.postValue(Unit)
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(CartErrorEvent.UpdateCartProducts)
            }
        }
    }

    override fun decreaseProductCount(id: Long) {
        viewModelScope.launch {
            decreaseCartProductUseCase(id, INCREMENT_AMOUNT).onSuccess {
                val uiState = _uiState.value ?: return@launch
                val newUiState = uiState.updateCart(PAGE_SIZE, it)
                updateUiState(newUiState)
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(CartErrorEvent.UpdateCartProducts)
            }
        }
    }

    override fun deleteProduct(product: CartProductUi) {
        viewModelScope.launch {
            deleteCartProductUseCase(product.product.id).onSuccess {
                val uiState = _uiState.value ?: return@launch
                val newUiState = uiState.updateCart(PAGE_SIZE, it)
                updateUiState(newUiState)
                _updateCartEvent.setValue(Unit)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(CartErrorEvent.DeleteCartProduct)
            }
        }
    }

    override fun toggleOrderProduct(product: CartProductUi) {
        val uiState = _uiState.value ?: return
        val newUiState = uiState.toggleOrderProduct(product.product.id)
        _uiState.value = newUiState
    }

    fun toggleTotalOrderProducts() {
        val uiState = _uiState.value ?: return
        val newUiState = uiState.toggleAllOrderProducts()
        _uiState.value = newUiState
    }

    fun moveToNextPage() {
        val currentPage = uiState.value?.currentPage ?: return
        loadCurrentPageCartProducts(currentPage + INCREMENT_AMOUNT)
    }

    fun moveToPrevPage() {
        val currentPage = uiState.value?.currentPage ?: return
        loadCurrentPageCartProducts(currentPage - INCREMENT_AMOUNT)
    }

    fun navigateToRecommend() {
        val orderedProductIds = _uiState.value?.orderedProducts ?: return
        if (orderedProductIds.isEmpty()) return _errorEvent.setValue(CartErrorEvent.EmptyOrderProduct)
        _navigateToRecommendEvent.setValue(orderedProductIds)
    }

    private fun updateUiState(
        newUiState: CartUiState,
        currentPage: Int = newUiState.currentPage,
        isLoading: Boolean = false,
    ) {
        val canLoadPrevPage = canLoadMoreCartProducts(currentPage - INCREMENT_AMOUNT)
        val canLoadNextPage = canLoadMoreCartProducts(currentPage + INCREMENT_AMOUNT)
        _uiState.value =
            newUiState.copy(
                currentPage = currentPage,
                canLoadPrevPage = canLoadPrevPage,
                canLoadNextPage = canLoadNextPage,
                isLoading = isLoading,
            )
    }

    private fun loadCurrentPageCartProducts(page: Int) {
        viewModelScope.launch {
            loadPagingCartUseCase(page.minus(1), PAGE_SIZE).onSuccess { newCart ->
                val uiState = _uiState.value ?: return@launch
                val newProducts = uiState.cart.addAll(newCart)
                val newUiState = uiState.updateCart(PAGE_SIZE, newProducts)
                updateUiState(newUiState, currentPage = page)
            }.onFailure {
                Timber.e(it)
                _errorEvent.setValue(CartErrorEvent.LoadCartProducts)
            }
        }
    }

    fun loadTotalCartProducts() {
        viewModelScope.launch {
            val uiState = _uiState.value ?: return@launch
            loadCartUseCase().onSuccess { newCart ->
                val newUiState = uiState.updateCart(PAGE_SIZE, newCart)
                updateUiState(newUiState, currentPage = START_PAGE, isLoading = false)
            }.onFailure {
                Timber.e(it)
                _errorEvent.postValue(CartErrorEvent.LoadCartProducts)
                _uiState.postValue(uiState.copy(isLoading = false))
            }
        }
    }

    private fun canLoadMoreCartProducts(page: Int): Boolean {
        cartRepository.canLoadMoreCartProducts(page - 1, PAGE_SIZE).onSuccess {
            return it
        }.onFailure {
            Timber.e(it)
            _errorEvent.postValue(CartErrorEvent.CanLoadMoreCartProducts)
        }
        return false
    }

    companion object {
        private const val START_PAGE: Int = 1
        private const val PAGE_SIZE = 5
        private const val INCREMENT_AMOUNT = 1

        fun factory(
            repository: CartRepository,
            increaseCartProductUseCase: IncreaseCartProductUseCase,
            decreaseCartProductUseCase: DecreaseCartProductUseCase,
            deleteCartProductUseCase: DeleteCartProductUseCase,
            loadCartUseCase: LoadCartUseCase,
            loadPagingCartUseCase: LoadPagingCartUseCase,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                CartViewModel(
                    repository,
                    increaseCartProductUseCase,
                    decreaseCartProductUseCase,
                    deleteCartProductUseCase,
                    loadCartUseCase,
                    loadPagingCartUseCase,
                )
            }
        }
    }
}

private fun CartUiState.toggleOrderProduct(productId: Long): CartUiState {
    val newCurrentProducts =
        currentPageProducts.map {
            if (it.product.id == productId) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
    val newPagingProducts = pagingProducts + (currentPage to newCurrentProducts)
    return copy(
        pagingProducts = newPagingProducts,
    )
}

private fun CartUiState.toggleAllOrderProducts(): CartUiState {
    val isSelectedAll = isTotalProductsOrdered
    val newPagingProducts =
        pagingProducts.map {
            it.key to
                    it.value.map { cartProductUi ->
                        cartProductUi.copy(isSelected = !isSelectedAll)
                    }
        }.toMap()
    return copy(
        pagingProducts = newPagingProducts,
    )
}

private fun CartUiState.updateCart(
    pageSize: Int,
    newCart: Cart,
): CartUiState {
    val orderedProductsIds = orderedProducts.map { it.product.id }
    val newCartProducts =
        newCart.toUiModel().map { newCartProduct ->
            if (newCartProduct.product.id in orderedProductsIds) {
                val original =
                    orderedProducts.find { it.product.id == newCartProduct.product.id }
                        ?: return@map newCartProduct
                newCartProduct.copy(isSelected = original.isSelected)
            } else {
                newCartProduct
            }
        }
    // paging
    val newPagingProducts =
        newCartProducts.chunked(pageSize).mapIndexed { index, chunkedProducts ->
            (index + 1) to chunkedProducts
        }.toMap()
    return copy(
        cart = newCart,
        pagingProducts = newPagingProducts,
    )
}
