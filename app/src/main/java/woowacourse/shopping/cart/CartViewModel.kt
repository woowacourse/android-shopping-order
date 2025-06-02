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
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _cartProducts = MutableLiveData<MutableSet<ProductItem>>()
    val cartProducts: LiveData<MutableSet<ProductItem>> = _cartProducts

    private val _updatedItem = MutableLiveData<ProductUiModel>()
    val updatedItem: LiveData<ProductUiModel> = _updatedItem

    private val _loadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.loading())
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _totalCount = MutableLiveData<Int>(-1)
    val totalCount: LiveData<Int> get() = _totalCount

    private val _totalAmount = MutableLiveData<Int>(0)
    val totalAmount: LiveData<Int> get() = _totalAmount

    private val _selectedEvent = MutableLiveData<Unit>()
    val selectedEvent: LiveData<Unit> = _selectedEvent

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private val selectedState: MutableMap<Int, Boolean> = mutableMapOf()

    init {
        loadRecommendProducts()
        loadAllCartProducts()
        loadCartProducts()
    }

    fun loadRecommendProducts() {
        recentlyViewedProductRepository.getLatestViewedProduct { product ->
            catalogProductRepository.getProduct(product.id) { categoryProduct ->
                val category = categoryProduct.category ?: ""
                catalogProductRepository.getRecommendedProducts(category, 0, 10) { products ->
                    _recommendedProducts.postValue(products)
                }
            }
        }
    }

    fun refreshProductsInfo() {
        postTotalAmount()
        postTotalCount()
    }

    fun postTotalAmount() {
        val products: List<ProductUiModel> =
            cartProducts.value?.map { it.productItem } ?: emptyList()
        val amount =
            products
                .map { it.copy(isChecked = selectedState[it.id] ?: true) }
                .filter { it.isChecked == true }
                .sumOf { it.price * it.quantity }
        _totalAmount.postValue(amount)
    }

    fun postTotalCount() {
        val count = cartProducts.value?.size ?: 0
        _totalCount.postValue(count)
    }

    fun deleteCartProduct(cartProduct: ProductItem) {
        cartProductRepository.deleteCartProduct(cartProduct.productItem.cartItemId ?: return) {
            loadCartProducts()
        }
    }

    fun updateQuantity(
        buttonEvent: ButtonEvent,
        product: ProductUiModel,
    ) {
        when (buttonEvent) {
            ButtonEvent.DECREASE -> {
                if (product.quantity != 1) {
                    cartProductRepository.updateProduct(
                        product.cartItemId ?: return,
                        product.quantity - 1,
                    ) { result ->
                        if (result == true) {
                            val updatedItem: ProductUiModel =
                                product.copy(quantity = product.quantity - 1)
                            _updatedItem.postValue(updatedItem)

                            loadCartProducts()
                            postTotalAmount()
                        }
                    }
                }
            }

            ButtonEvent.INCREASE -> {
                cartProductRepository.updateProduct(
                    product.cartItemId ?: return,
                    product.quantity + 1,
                ) { result ->
                    if (result == true) {
                        val updatedItem: ProductUiModel =
                            product.copy(quantity = product.quantity + 1)
                        _updatedItem.postValue(updatedItem)

                        loadCartProducts()
                        postTotalAmount()
                    }
                }
            }
        }
    }

    fun addProduct(product: ProductUiModel) {
        cartProductRepository.insertCartProduct(product.copy(quantity = 1)) { product ->
            _updatedItem.postValue(product)
            refreshProductsInfo()
        }
    }

    fun changeProductSelection(productUiModel: ProductUiModel) {
        val items: MutableSet<ProductItem> = _cartProducts.value ?: return
        if (selectedState.contains(productUiModel.id)) {
            selectedState[productUiModel.id] = selectedState[productUiModel.id]?.not() ?: false
        } else {
            selectedState[productUiModel.id] = false
        }
        items.removeIf { productUiModel.id == it.productItem.id }
        items.add(
            ProductItem(
                productUiModel.copy(
                    isChecked = selectedState[productUiModel.id] ?: false,
                ),
            ),
        )

        Log.d("호출됨", "changeProductSelection")
        _selectedEvent.postValue(Unit)
    }

    private fun loadAllCartProducts() {
        cartProductRepository.getTotalElements { totalSize ->
            cartProductRepository
                .getCartProductsInRange(0, totalSize) { cartProducts ->
                    val items = cartProducts.map { ProductItem(it) }.toMutableSet()
                    _cartProducts.postValue(items)
                }
        }
    }

    private fun loadCartProducts() {
        _loadingState.postValue(LoadingState.loading())

        cartProductRepository.getTotalElements { totalSize ->
            cartProductRepository
                .getCartProductsInRange(0, totalSize) { cartProducts ->
                    val pagedProducts: List<ProductItem> =
                        cartProducts
                            .map {
                                if (selectedState.contains(it.id)) {
                                    it.copy(isChecked = selectedState[it.id] == true)
                                } else {
                                    it
                                }
                            }.map { ProductItem(it) }

                    _cartProducts.postValue(pagedProducts.toMutableSet())
                    postTotalCount()

                    _loadingState.postValue(LoadingState.loaded())
                }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
        private const val INITIAL_PAGE = 0
    }
}
