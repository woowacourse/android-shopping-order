package woowacourse.shopping.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.LoadingState
import woowacourse.shopping.product.catalog.ProductUiModel

class CartViewModel(
    private val cartProductRepository: CartProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository
) : ViewModel() {
    private val _cartProducts = MutableLiveData<List<ProductUiModel>>()
    val cartProducts: LiveData<List<ProductUiModel>> = _cartProducts

    private val _isNextButtonEnabled = MutableLiveData<Boolean>(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _isPrevButtonEnabled = MutableLiveData<Boolean>(false)
    val isPrevButtonEnabled: LiveData<Boolean> = _isPrevButtonEnabled

    private val _page = MutableLiveData<Int>(INITIAL_PAGE)
    val page: LiveData<Int> = _page

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _loadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.loading())
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val productSelections = mutableMapOf<Int, Boolean>()

    private val _totalCount = MutableLiveData<Int>(-1)
    val totalCount: LiveData<Int> get() = _totalCount

    private val _totalProducts = MutableLiveData<MutableSet<ProductUiModel>>()
    val totalProducts: LiveData<MutableSet<ProductUiModel>> get() = _totalProducts

    private val _totalAmount = MutableLiveData<Int>(0)
    val totalAmount: LiveData<Int> get() = _totalAmount

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private var category: String? = null
    init {
        loadRecentlyViewedProduct()
        loadRecommendedProducts(category)
        loadAllCartProducts()
        loadCartProducts()
    }

    fun loadRecentlyViewedProduct() {
        recentlyViewedProductRepository.getLatestViewedProduct { product ->
            catalogProductRepository.getProduct(product.id) { categoryProduct ->
                category = categoryProduct.category
            }
        }
    }

    fun loadRecommendedProducts(category: String?) {
        if (category == null) return
        catalogProductRepository.getRecommendedProducts(category, 0, 10) { products ->
            _recommendedProducts.postValue(products)
        }
    }

    fun postTotalAmount() {
        val amount = totalProducts.value?.filter { it.isChecked == true }?.sumOf { it.price * it.quantity } ?: 0
        Log.d("COUNT", "amount : $amount")
        _totalAmount.postValue(amount)
    }

    fun postTotalCount() {
        val count = totalProducts.value?.size ?: 0
        Log.d("COUNT", "count : $count")
        _totalCount.postValue(count)
    }

    fun deleteCartProduct(cartProduct: ProductItem) {
        val set = _totalProducts.value ?: mutableSetOf()
        set.remove(cartProduct.productItem)
        _totalProducts.postValue(set.toMutableSet())
        cartProductRepository.deleteCartProduct(cartProduct.productItem)

        cartProductRepository.getTotalElements { updatedSize ->
            val currentPage = page.value ?: INITIAL_PAGE
            val startIndex = currentPage * PAGE_SIZE
            if (startIndex >= updatedSize && currentPage > 0) {
                decreasePage()
            }
            loadCartProducts()
        }
    }

    fun onPaginationButtonClick(buttonDirection: Int) {
        cartProductRepository.getTotalElements { totalSize ->
            val currentPage = page.value ?: INITIAL_PAGE
            val lastPage = (totalSize - 1) / PAGE_SIZE

            when (buttonDirection) {
                PREV_BUTTON -> {
                    if (currentPage > 0) {
                        decreasePage()
                        loadCartProducts()
                    }
                }

                NEXT_BUTTON -> {
                    if (currentPage < lastPage) {
                        increasePage()
                        loadCartProducts()
                    }
                }
            }
        }
    }

    fun updateQuantity(
        event: Int,
        product: ProductUiModel,
    ) {
        if (event == DECREASE_BUTTON) {
            if (product.quantity != 1) {
                cartProductRepository.updateProduct(product, product.quantity - 1) { result ->
                    if (result == true) {
                        _updatedItem.postValue(product.copy(quantity = product.quantity - 1))
                        val set = _totalProducts.value ?: mutableSetOf()
                        set.remove(product)
                        set.add(product.copy(quantity = product.quantity - 1))
                        _totalProducts.postValue(set.toMutableSet())
                        postTotalAmount()
                    }
                }
            }
        } else if (event == INCREASE_BUTTON) {
            cartProductRepository.updateProduct(product, product.quantity + 1) { result ->
                if (result == true) {
                    _updatedItem.postValue(product.copy(quantity = product.quantity + 1))
                    val set = _totalProducts.value ?: mutableSetOf()
                    set.remove(product)
                    set.add(product.copy(quantity = product.quantity + 1))
                    _totalProducts.postValue(set.toMutableSet())
                    postTotalAmount()
                }
            }
        }
    }

    fun changeProductSelection(productUiModel: ProductUiModel) {
        val set = _totalProducts.value ?: mutableSetOf()
        set.remove(productUiModel.copy(isChecked = productSelections[productUiModel.id] ?: true))
        productSelections[productUiModel.id] = productSelections[productUiModel.id]?.not() == true
        set.add(productUiModel.copy(isChecked = productSelections[productUiModel.id] ?: true))
        _totalProducts.postValue(set.toMutableSet())
        postTotalAmount()
    }

    private fun checkNextButtonEnabled(totalSize: Int) {
        val currentPage = page.value ?: INITIAL_PAGE
        val lastPage = (totalSize - 1) / PAGE_SIZE
        _isNextButtonEnabled.postValue(currentPage < lastPage)
    }

    private fun checkPrevButtonEnabled() {
        val currentPage = page.value ?: INITIAL_PAGE
        _isPrevButtonEnabled.postValue(currentPage >= 1)
    }

    private fun increasePage() {
        _page.postValue(page.value?.plus(1))
    }

    private fun decreasePage() {
        _page.postValue(page.value?.minus(1))
    }

    private fun loadAllCartProducts() {
        cartProductRepository.getTotalElements { totalSize ->
            cartProductRepository
                .getCartProductsInRange(0, totalSize) { cartProducts ->
                    _totalProducts.postValue(cartProducts.toMutableSet())
                }
        }
    }

    private fun loadCartProducts(pageSize: Int = PAGE_SIZE) {
        _loadingState.postValue(LoadingState.loading())

        cartProductRepository.getTotalElements { totalSize ->
            val currentPage = page.value ?: INITIAL_PAGE
            val startIndex = currentPage * pageSize
            val endIndex = minOf(startIndex + pageSize, totalSize)

            if (startIndex >= totalSize) {
                return@getTotalElements
            }

            cartProductRepository
                .getCartProductsInRange(currentPage, pageSize) { cartProducts ->
                    val pagedProducts: List<ProductUiModel> =
                        cartProducts.map {
                            if (productSelections.contains(it.id)) {
                                it.copy(isChecked = productSelections[it.id] == true)
                            } else {
                                it
                            }
                        }

                    _cartProducts.postValue(pagedProducts)
                    checkNextButtonEnabled(totalSize)
                    checkPrevButtonEnabled()
                    postTotalCount()

                    _loadingState.postValue(LoadingState.loaded())
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_PAGE = 0
        private const val PREV_BUTTON = 1
        private const val NEXT_BUTTON = 2
        private const val DECREASE_BUTTON = 0
        private const val INCREASE_BUTTON = 1
    }
}
