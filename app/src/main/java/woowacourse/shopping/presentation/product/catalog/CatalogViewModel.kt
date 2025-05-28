package woowacourse.shopping.presentation.product.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository

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

    //    private val loadedProducts = mutableListOf<ProductUiModel>()
    private var currentPage = 0
    val page: Int get() = currentPage

    init {
        loadCatalogProducts()
        loadRecentViewedItems()
//        updateCartCount()
    }

    fun onQuantitySelectorToggled(product: ProductUiModel) {
        val toggled =
            product.copy(isExpanded = !product.isExpanded, quantity = product.quantity + 1)
        updateProduct(toggled)
    }

    fun increaseQuantity(product: ProductUiModel) {
        updateProduct(product.copy(quantity = product.quantity + 1))
    }

    fun decreaseQuantity(product: ProductUiModel) {
        val newQuantity = (product.quantity - 1).coerceAtLeast(0)
        val updated =
            product.copy(
                quantity = newQuantity,
                isExpanded = newQuantity > 0,
            )
        updateProduct(updated)
    }

    private fun updateProduct(updated: ProductUiModel) {
        if (updated.quantity == 0) {
            cartRepository.deleteCartItem(updated.id) { result ->
                result
                    .onSuccess {
                        applyProductChange(updated)
                    }
            }
        } else {
            cartRepository.upsertCartItem(updated.id, updated.quantity) { result ->
                result
                    .onSuccess {
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

        _pagingData.postValue(
            PagingData(
                products = updatedProducts,
                hasNext = currentPagingData.hasNext,
            ),
        )

        updateCartCount()
    }

    fun loadNextCatalogProducts() {
        currentPage++
        loadCatalogProducts()
    }

    private fun loadCatalogProducts(pageSize: Int = PAGE_SIZE) {
        productsRepository.getProducts(currentPage, pageSize) { result ->
            result.onSuccess { pagingData ->
                _pagingData.postValue(
                    pagingData,
                )
            }
        }
    }

    fun loadRecentViewedItems() {
        viewedRepository.getViewedItems { items ->
            _recentViewedItems.postValue(items)
            _hasRecentViewedItems.postValue(items.isNotEmpty())
        }
    }

    private fun updateCartCount() {
        cartRepository.getCarItemsCount { result ->
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
