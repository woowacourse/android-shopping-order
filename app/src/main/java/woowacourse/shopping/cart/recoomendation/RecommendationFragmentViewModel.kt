package woowacourse.shopping.cart.recoomendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.product.catalog.ProductUiModel

class RecommendationFragmentViewModel(
    private val cartProductRepository: CartProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private val _totalPurchasePrice = MutableLiveData<Int>(INITIAL_TOTAL_PURCHASE_PRICE)
    val totalPurchasePrice: LiveData<Int> get() = _totalPurchasePrice

    private val _selectedProductsCount = MutableLiveData<Int>(INITIAL_SELECTED_PRODUCTS_COUNT)
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
                catalogProductRepository.getRecommendedProducts(category, ZERO, TOTAL_RECOMMENDATION_PRODUCTS_COUNT) { products ->
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
                ?: INITIAL_TOTAL_PURCHASE_PRICE
        _totalPurchasePrice.postValue(amount)
    }

    fun increaseQuantity(product: ProductUiModel) {
        when (product.quantity) {
            0 -> {
                val newProduct = product.copy(quantity = A_COUNT)
                cartProductRepository.insertCartProduct(newProduct) { product ->
                    updateItem(product)
                }
            }

            else -> {
                cartProductRepository.updateProduct(product, product.quantity + A_COUNT) { success ->
                    if (success) {
                        val newProduct = product.copy(quantity = product.quantity + A_COUNT)
                        updateItem(newProduct)
                    }
                }
            }
        }
    }

    fun decreaseQuantity(product: ProductUiModel) {
        when (product.quantity) {
            1 -> {
                val newProduct = product.copy(quantity = INITIAL_PRODUCT_COUNT)
                cartProductRepository.deleteCartProduct(product) { success ->
                    if (success) {
                        updateItem(newProduct)
                    }
                }
            }

            else -> {
                val newProduct = product.copy(quantity = product.quantity - A_COUNT)
                cartProductRepository.updateProduct(product, product.quantity - A_COUNT) { success ->
                    if (success) {
                        updateItem(newProduct)
                    }
                }
            }
        }
    }

    private fun updateItem(newProduct: ProductUiModel) {
        _updatedItem.postValue(newProduct)
        _recommendedProducts.value = _recommendedProducts.value?.map {
            if (it.id == newProduct.id) newProduct else it
        }
        fetchTotalCount()
        fetchPurchasePrice()
    }

    companion object {
        private const val INITIAL_TOTAL_PURCHASE_PRICE: Int = 0
        private const val INITIAL_SELECTED_PRODUCTS_COUNT: Int = 0
        private const val INITIAL_PRODUCT_COUNT: Int = 0
        private const val A_COUNT: Int = 1
        private const val ZERO: Int = 0
        private const val TOTAL_RECOMMENDATION_PRODUCTS_COUNT: Int = 10
    }
}
