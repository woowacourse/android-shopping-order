package woowacourse.shopping.cart.recoomendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRecommendationRepository
import woowacourse.shopping.product.catalog.ProductUiModel

class RecommendationFragmentViewModel(
    private val cartRecommendationRepository: CartRecommendationRepository,
) : ViewModel() {
    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private val _totalPurchasePrice = MutableLiveData<Int>(INITIAL_TOTAL_PURCHASE_PRICE)
    val totalPurchasePrice: LiveData<Int> get() = _totalPurchasePrice

    private val _selectedProductsCount = MutableLiveData<Int>(INITIAL_SELECTED_PRODUCTS_COUNT)
    val selectedProductsCount: LiveData<Int> get() = _selectedProductsCount

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _isMovePay = MutableLiveData<Unit>()
    val isMovePay: LiveData<Unit> get() = _isMovePay

    init {
        loadRecentlyViewedProduct()
    }

    fun orderProduct(): List<ProductUiModel> {
        return _recommendedProducts.value?.filter { it.isChecked == true } ?: emptyList()
    }

    fun movePay() {
        if (_selectedProductsCount.value == 0) return
        _isMovePay.value = Unit
    }

    fun loadRecentlyViewedProduct() {
        viewModelScope.launch {
            val products = cartRecommendationRepository.getRecommendedProducts()
            _recommendedProducts.postValue(
                products,
            )
        }
    }

    fun fetchTotalCount() {
        viewModelScope.launch {
            val count = cartRecommendationRepository.getSelectedProductsCount()
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
            INITIAL_PRODUCT_COUNT -> {
                val newProduct = product.copy(quantity = A_COUNT)
                viewModelScope.launch {
                    val product = cartRecommendationRepository.insertCartProduct(newProduct)
                    updateItem(product)
                }
            }

            else -> {
                viewModelScope.launch {
                    val success = cartRecommendationRepository.updateCartProduct(
                        product,
                        product.quantity + A_COUNT,
                    )
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
            A_COUNT -> {
                val newProduct = product.copy(quantity = INITIAL_PRODUCT_COUNT)
                viewModelScope.launch {
                    val success = cartRecommendationRepository.deleteCartProduct(product)
                    if (success) {
                        updateItem(newProduct)
                    }
                }
            }

            else -> {
                val newProduct = product.copy(quantity = product.quantity - A_COUNT)
                viewModelScope.launch {
                    val success = cartRecommendationRepository.updateCartProduct(
                        product,
                        product.quantity - A_COUNT,
                    )
                    if (success) {
                        updateItem(newProduct)
                    }
                }
            }
        }
    }

    private fun updateItem(newProduct: ProductUiModel) {
        _updatedItem.postValue(newProduct)
        _recommendedProducts.value =
            _recommendedProducts.value?.map {
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
    }
}
