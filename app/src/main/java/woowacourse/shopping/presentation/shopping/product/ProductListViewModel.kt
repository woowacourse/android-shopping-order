package woowacourse.shopping.presentation.shopping.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.domain.entity.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ShoppingRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.shopping.toShoppingUiModel
import woowacourse.shopping.presentation.shopping.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class ProductListViewModel(
    private val shoppingRepository: ShoppingRepository,
    private val cartRepository: CartRepository,
) : ViewModel(), ProductItemListener {
    private val _uiState = MutableLiveData(ProductListUiState(START_PAGE))
    val uiState: LiveData<ProductListUiState> get() = _uiState

    private val _navigateToDetailEvent = MutableSingleLiveData<Long>()
    val navigateToDetailEvent: SingleLiveData<Long> = _navigateToDetailEvent
    private val _errorEvent = MutableSingleLiveData<ProductListErrorEvent>()
    val errorEvent: SingleLiveData<ProductListErrorEvent> = _errorEvent

    init {
        _uiState.value = _uiState.value?.copy(isLoading = true)
        loadProducts()
        loadCartProducts()
        loadRecentProducts()
        _uiState.value = _uiState.value?.copy(isLoading = false)
    }

    override fun loadProducts() {
        println("load products start")
        var uiState = _uiState.value ?: return
        val currentPage = uiState.currentPage
        println("current page : ${uiState.currentPage}")
        viewModelScope.launch {
            println("this : ${Thread.currentThread().name}")
            shoppingRepository.products(currentPage - 1, PAGE_SIZE)
                .onSuccess {
                    println("type : ${it.javaClass.name}")
                    uiState = _uiState.value ?: return@launch
                    val newProducts = it.map(Product::toShoppingUiModel)
                    _uiState.value = uiState.addProducts(newProducts, getLoadMore(currentPage + 1))
                }.onFailure {
                    println(it.localizedMessage)
                    _errorEvent.setValue(ProductListErrorEvent.LoadProducts)
                }
        }
    }

    fun loadCartProducts() {
        var uiState = _uiState.value ?: return
        val ids = uiState.products.map { it.id }
        viewModelScope.launch {
            cartRepository.filterCartProducts(ids)
                .onSuccess { newCartProducts ->
                    uiState = _uiState.value ?: return@launch
                    val newProducts = newCartProducts.map(CartProduct::toShoppingUiModel)
                    _uiState.value = uiState.updateProducts(newProducts)
                }.onFailure {
                    _errorEvent.setValue(ProductListErrorEvent.LoadCartProducts)
                }
        }
    }

    fun loadRecentProducts() {
        viewModelScope.launch {
            shoppingRepository.recentProducts(RECENT_PRODUCT_COUNT).onSuccess {
                val uiState = _uiState.value ?: return@launch
                _uiState.value = uiState.copy(recentProducts = it.map(Product::toUiModel))
            }.onFailure {
                _errorEvent.setValue(ProductListErrorEvent.LoadRecentProducts)
            }
        }
    }

    override fun increaseProductCount(id: Long) {
        viewModelScope.launch {
            var uiState = _uiState.value ?: return@launch
            val product = uiState.findProduct(id) ?: return@launch
            cartRepository.updateCartProduct(id, product.count + INCREMENT_AMOUNT).onSuccess {
                uiState = _uiState.value ?: return@launch
                _uiState.value = uiState.increaseProductCount(id, INCREMENT_AMOUNT)
            }.onFailure {
                _errorEvent.setValue(ProductListErrorEvent.DecreaseCartCount)
            }
        }
    }

    override fun decreaseProductCount(id: Long) {
        var uiState = _uiState.value ?: return
        viewModelScope.launch {
            if (uiState.shouldDeleteFromCart(id)) {
                cartRepository.deleteCartProduct(id).onSuccess {
                    uiState = _uiState.value ?: return@launch
                    _uiState.value = uiState.decreaseProductCount(id, INCREMENT_AMOUNT)
                }
                return@launch
            }
            val product = uiState.findProduct(id) ?: return@launch
            cartRepository.updateCartProduct(id, product.count - INCREMENT_AMOUNT).onSuccess {
                _uiState.value = uiState.decreaseProductCount(id, INCREMENT_AMOUNT)
            }.onFailure {
                _errorEvent.setValue(ProductListErrorEvent.DecreaseCartCount)
            }
        }
    }

    override fun navigateToDetail(id: Long) {
        _navigateToDetailEvent.setValue(id)
    }

    private fun getLoadMore(page: Int): List<ShoppingUiModel.LoadMore> {
        return if (shoppingRepository.canLoadMore(page - 1, PAGE_SIZE).getOrNull() == true) {
            listOf(ShoppingUiModel.LoadMore)
        } else {
            emptyList()
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PAGE_SIZE = 20
        private const val INCREMENT_AMOUNT = 1
        private const val START_PAGE = 1

        fun factory(
            shoppingRepository: ShoppingRepository,
            cartRepository: CartRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory { ProductListViewModel(shoppingRepository, cartRepository) }
        }
    }
}
