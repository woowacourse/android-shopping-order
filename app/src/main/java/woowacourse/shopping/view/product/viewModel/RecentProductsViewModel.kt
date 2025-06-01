package woowacourse.shopping.view.product.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.data.product.repository.ProductsRepository
import woowacourse.shopping.view.product.ProductsItem

class RecentProductsViewModel(
    private val productsRepository: ProductsRepository = DefaultProductsRepository.get(),
) : ViewModel() {
    private val _recentProducts = MutableLiveData<ProductsItem.RecentWatchingItem>()
    val recentProducts: LiveData<ProductsItem.RecentWatchingItem> get() = _recentProducts

    init {
        updateRecentProducts()
    }

    fun updateRecentProducts() {
        viewModelScope.launch {
            val recentWatchingProducts = productsRepository.getRecentWatchingProducts(10)
            _recentProducts.value =
                ProductsItem.RecentWatchingItem(
                    recentWatchingProducts.map {
                        ProductsItem.ProductItem(product = it)
                    },
                )
        }
    }
}
