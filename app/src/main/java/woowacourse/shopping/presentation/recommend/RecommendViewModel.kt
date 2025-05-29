package woowacourse.shopping.presentation.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.mapper.toUiModel
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class RecommendViewModel(
    private val productsRepository: ProductsRepository,
    private val cartItemRepository: CartItemRepository,
    private val viewedItemRepository: ViewedItemRepository,
) : ViewModel() {
    private val _items: MutableLiveData<List<ProductUiModel>> = MutableLiveData(emptyList())
    val items: LiveData<List<ProductUiModel>>
        get() = _items

    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int>
        get() = _price

    private val _count: MutableLiveData<Int> = MutableLiveData()
    val count: LiveData<Int>
        get() = _count

    init {
        loadRecommendedProducts()
    }

    fun setOrderInfo(
        price: Int,
        count: Int,
    ) {
        _price.value = price
        _count.value = count
    }

    private fun loadRecommendedProducts() {
        viewedItemRepository.getLastViewedItem { item ->
            item?.let { loadProductsByCategory(it.category) }
        }
    }

    private fun loadProductsByCategory(category: String) {
        productsRepository.getProductsByCategory(category) { result ->
            result
                .onSuccess { products ->
                    _items.postValue(products.map { it.toUiModel() }.take(10))
                }
                .onFailure {

                }
        }
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecommendViewModel(
                    productsRepository = RepositoryProvider.productsRepository,
                    cartItemRepository = RepositoryProvider.cartItemRepository,
                    viewedItemRepository = RepositoryProvider.viewedItemRepository,
                )
            }
        }
    }
}