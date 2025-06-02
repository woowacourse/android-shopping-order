package woowacourse.shopping.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.LoadingState
import woowacourse.shopping.product.catalog.ProductUiModel
import java.util.TreeSet

class CartViewModel(
    private val cartProductRepository: CartProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _cartProducts = MutableLiveData<TreeSet<ProductItem>>()
    val cartProducts: LiveData<TreeSet<ProductItem>> = _cartProducts

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

    private var isInitialLoad = true

    init {
        loadRecommendProducts()
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
                            loadCartProducts()
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
                        loadCartProducts()
                    }
                }
            }
        }
    }

    fun addProduct(product: ProductUiModel) {
        cartProductRepository.insertCartProduct(product.copy(quantity = 1)) { product ->
            refreshProductsInfo()
            loadCartProducts()
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
        _selectedEvent.postValue(Unit)
    }

    private fun loadCartProducts() {
        if (isInitialLoad) {
            _loadingState.postValue(LoadingState.loading())
        } else {
            _loadingState.postValue(LoadingState.refreshing())
        }

        cartProductRepository.getTotalElements { totalSize ->
            cartProductRepository.getCartProductsInRange(0, totalSize) { cartProducts ->
                val pagedProducts: List<ProductItem> =
                    cartProducts
                        .map {
                            if (selectedState.contains(it.id)) {
                                it.copy(isChecked = selectedState[it.id] == true)
                            } else {
                                it
                            }
                        }.map { ProductItem(it) }

                val items =
                    TreeSet(
                        compareBy<ProductItem> { it.productItem.id },
                    ).apply { addAll(pagedProducts) }

                _cartProducts.postValue(items)
                postTotalCount()
                postTotalAmount()

                _loadingState.postValue(LoadingState.loaded())
                isInitialLoad = false
            }
        }
    }

    private fun postTotalAmount() {
        val products: List<ProductUiModel> =
            cartProducts.value?.map { it.productItem } ?: emptyList()
        val amount =
            products
                .map { it.copy(isChecked = selectedState[it.id] ?: true) }
                .filter { it.isChecked == true }
                .sumOf { it.price * it.quantity }
        _totalAmount.postValue(amount)
    }

    private fun postTotalCount() {
        val count = cartProducts.value?.size ?: 0
        _totalCount.postValue(count)
    }
}
