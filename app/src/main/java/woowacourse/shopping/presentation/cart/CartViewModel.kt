package woowacourse.shopping.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class CartViewModel(
    private val cartRepository: CartRepository,
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
        val product = _uiState.value?.findProductAtCurrentPage(id) ?: return
        val uiState = _uiState.value ?: return
        cartRepository.updateCartProduct(id, product.count + INCREMENT_AMOUNT).onSuccess {
            val newUiState = uiState.increaseProductCount(id, INCREMENT_AMOUNT)
            updateUiState(newUiState)
            _updateCartEvent.setValue(Unit)
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.UpdateCartProducts)
        }
    }

    override fun decreaseProductCount(id: Long) {
        val product = _uiState.value?.findProductAtCurrentPage(id) ?: return
        val uiState = _uiState.value ?: return
        if (!uiState.canDecreaseProductCount(id, CART_PRODUCT_COUNT_LIMIT)) {
            return _errorEvent.setValue(CartErrorEvent.DecreaseCartCountLimit)
        }
        cartRepository.updateCartProduct(id, product.count - INCREMENT_AMOUNT).onSuccess {
            val newUiState = uiState.decreaseProductCount(id, INCREMENT_AMOUNT)
            updateUiState(newUiState)
            _updateCartEvent.setValue(Unit)
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.UpdateCartProducts)
        }
    }

    override fun deleteProduct(product: CartProductUi) {
        cartRepository.deleteCartProduct(product.product.id).onSuccess {
            refreshCartProducts()
            _updateCartEvent.setValue(Unit)
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.DeleteCartProduct)
        }
    }

    override fun toggleOrderProduct(product: CartProductUi) {
        val uiState = _uiState.value ?: return
        val newUiState = uiState.toggleProductSelected(product.product.id)
        updateUiState(newUiState)
    }

    fun toggleTotalOrderProducts() {
        val uiState = _uiState.value ?: return
        val newUiState = uiState.toggleTotalOrderProducts()
        updateUiState(newUiState)
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
    ) {
        val canLoadPrevPage = canLoadMoreCartProducts(currentPage - INCREMENT_AMOUNT)
        val canLoadNextPage = canLoadMoreCartProducts(currentPage + INCREMENT_AMOUNT)
        _uiState.value =
            newUiState.copy(
                currentPage = currentPage,
                canLoadPrevPage = canLoadPrevPage,
                canLoadNextPage = canLoadNextPage,
            )
    }

    private fun loadCurrentPageCartProducts(page: Int) {
        cartRepository.cartProducts(page - 1, PAGE_SIZE).onSuccess { carts ->
            val newProducts = carts.map { it.toUiModel() }
            val uiState = _uiState.value ?: return
            val newUiState = uiState.updateTargetPageProducts(newProducts, page)
            updateUiState(newUiState)
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.LoadCartProducts)
        }
    }

    fun loadTotalCartProducts() {
        val uiState = _uiState.value ?: return
        _uiState.value = uiState.copy(isLoading = true)
        cartRepository.totalCartProducts().onSuccess { carts ->
            val newProducts = carts.map { it.toUiModel() }
            val newUiState = uiState.updateTotalProducts(newProducts)
            _uiState.value = newUiState.copy(isLoading = false)
            updateUiState(newUiState, currentPage = START_PAGE)
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.LoadCartProducts)
            _uiState.value = uiState.copy(isLoading = false)
        }
    }

    private fun refreshCartProducts() {
        val currentPage = _uiState.value?.currentPage ?: START_PAGE
        loadCurrentPageCartProducts(currentPage)
    }

    private fun canLoadMoreCartProducts(page: Int): Boolean {
        cartRepository.canLoadMoreCartProducts(page - 1, PAGE_SIZE).onSuccess {
            return it
        }.onFailure {
            _errorEvent.setValue(CartErrorEvent.CanLoadMoreCartProducts)
        }
        return false
    }

    companion object {
        private const val START_PAGE: Int = 1
        private const val PAGE_SIZE = 5
        private const val INCREMENT_AMOUNT = 1
        private const val CART_PRODUCT_COUNT_LIMIT = 1

        fun factory(repository: CartRepository): ViewModelProvider.Factory {
            return BaseViewModelFactory { CartViewModel(repository) }
        }
    }
}
