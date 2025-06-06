package woowacourse.shopping.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel

class CartRecommendationFragmentViewModel(
    private val cartProductRepository: CartProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private val _totalPurchasePrice = MutableLiveData<Int>(0)
    val totalPurchasePrice: LiveData<Int> get() = _totalPurchasePrice

    private val _selectedProductsCount = MutableLiveData<Int>(0)
    val selectedProductsCount: LiveData<Int> get() = _selectedProductsCount

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    init {
        loadRecentlyViewedProduct()
    }

    fun loadRecentlyViewedProduct() {
        recentlyViewedProductRepository.getLatestViewedProduct { product ->
            catalogProductRepository.getProduct(product.id) { categoryProduct ->
                val category = categoryProduct.category ?: ""
                catalogProductRepository.getRecommendedProducts(category, 0, 10) { products ->
                    _recommendedProducts.postValue(products)
                }
            }
        }
    }

    fun fetchTotalCount() {
        cartProductRepository.getTotalElements { count ->
            _selectedProductsCount.postValue(count)
        }
    }

    fun fetchPurchasePrice() {
        val amount =
            _recommendedProducts.value?.sumOf { it.price * it.quantity }
                ?: 0
        Log.d("test", "실행됨? ${_recommendedProducts.value}")
        _totalPurchasePrice.postValue(amount)
    }

    fun increaseQuantity(product: ProductUiModel) {
        if (product.quantity == 0) {
            val newProduct = product.copy(quantity = 1)
            cartProductRepository.insertCartProduct(newProduct) { }
            _updatedItem.postValue(newProduct)
            _recommendedProducts.value = _recommendedProducts.value?.map {
                if (it.id == newProduct.id) newProduct else it
            }
        } else {
            cartProductRepository.updateProduct(product, product.quantity + 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                    _recommendedProducts.value = _recommendedProducts.value?.map {
                        if (it.id == product.id) product else it
                    }
                }
            }
        }
        fetchTotalCount()
        fetchPurchasePrice()
    }

    fun decreaseQuantity(product: ProductUiModel) {
        if (product.quantity == 1) {
            cartProductRepository.deleteCartProduct(product)
            _updatedItem.postValue(product.copy(quantity = 0))
            _recommendedProducts.value = _recommendedProducts.value?.map {
                if (it.id == product.id) product else it
            }
        } else {
            cartProductRepository.updateProduct(product, product.quantity - 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                    _recommendedProducts.value = _recommendedProducts.value?.map {
                        if (it.id == product.id) product else it
                    }
                }
            }
        }
        fetchTotalCount()
        fetchPurchasePrice()
    }
}
