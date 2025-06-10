package woowacourse.shopping.view.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.view.common.MutableSingleLiveData
import woowacourse.shopping.view.common.SingleLiveData
import woowacourse.shopping.view.product.ProductsEvent
import woowacourse.shopping.view.product.ProductsItem

class RecentProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
) : ViewModel() {
    private val _recentProducts = MutableLiveData<ProductsItem.RecentWatchingItem>()
    val recentProducts: LiveData<ProductsItem.RecentWatchingItem> get() = _recentProducts

    private val _event: MutableSingleLiveData<ProductsEvent> = MutableSingleLiveData()
    val event: SingleLiveData<ProductsEvent> get() = _event

    private val handler =
        CoroutineExceptionHandler { _, exception ->
            _event.postValue(ProductsEvent.UPDATE_RECENT_WATCHING_PRODUCTS_FAILURE)
        }

    init {
        updateRecentProducts()
    }

    fun updateRecentProducts() {
        viewModelScope.launch(handler) {
            val recentWatchingProducts =
                productsRepository.getRecentWatchingProducts(10).getOrThrow()
            _recentProducts.value =
                ProductsItem.RecentWatchingItem(
                    recentWatchingProducts.map {
                        ProductsItem.ProductItem(product = it)
                    },
                )
        }
    }
}
