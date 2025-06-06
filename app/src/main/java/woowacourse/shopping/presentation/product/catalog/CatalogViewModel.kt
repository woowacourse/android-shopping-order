package woowacourse.shopping.presentation.product.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.RepositoryProvider.viewedItemRepository
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.presentation.util.SingleLiveEvent

class CatalogViewModel(
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartItemRepository,
    private val viewedRepository: ViewedItemRepository,
) : ViewModel() {
    private val _pagingData = MutableLiveData<PagingData>()
    val pagingData: LiveData<PagingData> = _pagingData

    private val _cartCount = MutableLiveData(0)
    val cartCount: LiveData<Int> = _cartCount

    private val _recentViewedItems = MutableLiveData<List<ProductUiModel>>()
    val recentViewedItems: LiveData<List<ProductUiModel>> = _recentViewedItems

    private val _hasRecentViewedItems = MutableLiveData(false)
    val hasRecentViewedItems: LiveData<Boolean> = _hasRecentViewedItems

    private val _updatedProduct = SingleLiveEvent<ProductUiModel>()
    val updatedProduct: LiveData<ProductUiModel> = _updatedProduct

    private var currentPage = 0
    val page: Int get() = currentPage

    fun toggleQuantity(product: ProductUiModel) {
        val toggled =
            product.copy(quantity = product.quantity + 1)

        viewModelScope.launch {
            val result = cartRepository.addCartItem(toggled.id, toggled.quantity)
            result.onSuccess {
                _updatedProduct.postValue(toggled)
                applyProductChange(toggled)
            }
        }
    }

    fun increaseQuantity(product: ProductUiModel) {
        val newProduct = product.copy(quantity = product.quantity + 1)
        viewModelScope.launch {
            val result = cartRepository.updateCartItemQuantity(newProduct.id, newProduct.quantity)
            result
                .onSuccess {
                    _updatedProduct.postValue(newProduct)
                    applyProductChange(newProduct)
                }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val newQuantity = (product.quantity - 1).coerceAtLeast(0)
        val updated = product.copy(quantity = newQuantity)

        viewModelScope.launch {
            if (product.quantity == 0) {
                val result = cartRepository.deleteCartItem(product.id)
                result.onSuccess {
                    applyProductChange(product)
                }
            } else {
                val result = cartRepository.updateCartItemQuantity(updated.id, updated.quantity)
                result
                    .onSuccess {
                        _updatedProduct.postValue(updated)
                        applyProductChange(updated)
                    }
            }
        }
    }

    private fun applyProductChange(updated: ProductUiModel) {
        val currentPagingData = _pagingData.value ?: return
        val updatedProducts =
            currentPagingData.products.map {
                if (it.id == updated.id) updated else it
            }
        _pagingData.postValue(currentPagingData.copy(products = updatedProducts))
        updateCartCount()
    }

    fun loadNextCatalogProducts() {
        loadCatalogProducts()
    }

    fun loadCatalogProducts(pageSize: Int = PAGE_SIZE) {
        viewModelScope.launch {
            val result = productsRepository.getProducts(currentPage, pageSize)

            result.onSuccess { pagingData ->
                val newPagingData: PagingData = cartRepository.getQuantity(pagingData)
                val currentProducts = _pagingData.value?.products ?: emptyList()
                _pagingData.postValue(
                    newPagingData.copy(products = currentProducts + newPagingData.products),
                )
                currentPage++
            }
        }
    }

    fun loadRecentViewedItems() {
        viewModelScope.launch {
            val items = viewedItemRepository.getViewedItems()
            _recentViewedItems.postValue(items)
            _hasRecentViewedItems.postValue(items.isNotEmpty())
        }
    }

    fun updateCartCount() {
        viewModelScope.launch {
            val result = cartRepository.getCartItemsCount()

            result.onSuccess { cartCount ->
                _cartCount.postValue(cartCount)
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20

        val FACTORY: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CatalogViewModel(
                        productsRepository = RepositoryProvider.productsRepository,
                        cartRepository = RepositoryProvider.cartItemRepository,
                        viewedRepository = RepositoryProvider.viewedItemRepository,
                    )
                }
            }
    }
}
