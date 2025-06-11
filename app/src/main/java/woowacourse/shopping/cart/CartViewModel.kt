package woowacourse.shopping.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.domain.LoadingState
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData
import java.util.TreeSet

class CartViewModel(
    private val cartProductRepository: CartProductRepository,
    private val catalogProductRepository: CatalogProductRepository,
    private val recentlyViewedProductRepository: RecentlyViewedProductRepository,
) : ViewModel() {
    private val _cartProducts = MutableLiveData<TreeSet<ProductItem>>()
    val cartProducts: LiveData<TreeSet<ProductItem>> = _cartProducts

    val orderedProducts: Array<ProductUiModel>
        get() =
            cartProducts.value
                ?.map { it.productItem }
                ?.filter { it.isChecked }
                ?.toTypedArray()
                ?: emptyArray()

    private val _loadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.loading())
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val _totalCount = MutableLiveData<Int>(-1)
    val totalCount: LiveData<Int> get() = _totalCount

    private val _totalPrice = MutableLiveData<Int>(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _selectedEvent = MutableLiveData<Unit>()
    val selectedEvent: LiveData<Unit> = _selectedEvent

    private val selectedState: MutableMap<Long, Boolean> = mutableMapOf()

    private val _isAllSelected = MutableLiveData<Boolean>(true)
    val isAllSelected: LiveData<Boolean> = _isAllSelected

    private var isInitialLoad = true

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>(emptyList())
    val recommendedProducts: LiveData<List<ProductUiModel>> get() = _recommendedProducts

    private val _updatedProduct = MutableLiveData<ProductUiModel>()
    val updatedProduct: LiveData<ProductUiModel> = _updatedProduct

    private val _orderClicked = MutableLiveData<Unit>()
    val orderClicked: LiveData<Unit> = _orderClicked

    private val _errorMessage = MutableSingleLiveData<String>()
    val errorMessage: SingleLiveData<String> = _errorMessage

    init {
        loadCartProducts()
        loadRecommendProducts()
    }

    fun loadRecommendProducts() {
        viewModelScope.launch {
            val latestViewedProduct: ProductUiModel? =
                recentlyViewedProductRepository.getLatestViewedProduct().getOrNull()
            latestViewedProduct?.id ?: run {
                _errorMessage.setValue("최근 본 상품을 불러오는 데 실패했습니다.")
                return@launch
            }

            val categorizedProduct =
                catalogProductRepository.getProduct(latestViewedProduct.id).getOrElse {
                    _errorMessage.setValue("카테고리 상품을 가져오는 데 실패했습니다.")
                    return@launch
                }

            val recommendProducts: List<ProductUiModel> =
                catalogProductRepository
                    .getRecommendedProducts(categorizedProduct?.category ?: "", 0, 10)
                    .getOrElse {
                        _errorMessage.setValue("추천 상품을 가져오는 데 실패했습니다.")
                        return@launch
                    }

            val cartProductIds =
                cartProducts.value?.map { it.productItem.id }?.toSet() ?: emptySet()

            val filteredProducts = recommendProducts.filterNot { it.id in cartProductIds }

            _recommendedProducts.value = filteredProducts
        }
    }

    fun refreshProductsInfo() {
        updateTotalPrice()
        updateTotalCount()
    }

    fun deleteCartProduct(cartItemId: Long?) {
        cartItemId ?: return
        viewModelScope.launch {
            cartProductRepository.deleteCartProduct(cartItemId)
            loadCartProducts()
        }
    }

    fun updateQuantity(
        buttonEvent: ButtonEvent,
        product: ProductUiModel,
    ) {
        viewModelScope.launch {
            when (buttonEvent) {
                ButtonEvent.DECREASE -> {
                    if (product.cartItemId == null) return@launch

                    if (product.quantity == 1) {
                        val result: Boolean =
                            cartProductRepository.deleteCartProduct(product.cartItemId).getOrElse {
                                _errorMessage.setValue("상품 삭제에 실패했습니다.")
                                return@launch
                            }
                        if (result) {
                            _updatedProduct.value = product.copy(quantity = 0)
                            loadCartProducts()
                        }
                    } else {
                        val result: Boolean =
                            cartProductRepository
                                .updateProduct(
                                    product.cartItemId,
                                    product.quantity - 1,
                                ).getOrElse {
                                    _errorMessage.setValue("상품 업데이트에 실패했습니다.")
                                    return@launch
                                }
                        if (result) {
                            _updatedProduct.value = product.copy(quantity = product.quantity - 1)
                            loadCartProducts()
                        }
                    }
                }

                ButtonEvent.INCREASE -> {
                    if (product.cartItemId != null) {
                        val result: Boolean =
                            cartProductRepository
                                .updateProduct(
                                    product.cartItemId,
                                    product.quantity + 1,
                                ).getOrElse {
                                    _errorMessage.setValue("상품 업데이트에 실패했습니다.")
                                    return@launch
                                }
                        if (result) {
                            _updatedProduct.value = product.copy(quantity = product.quantity + 1)
                            loadCartProducts()
                        }
                    }
                }
            }
        }
    }

    fun addProduct(product: ProductUiModel) {
        viewModelScope.launch {
            val cartItemId =
                cartProductRepository.insertCartProduct(productId = product.id, quantity = 1).getOrElse {
                    _errorMessage.setValue("상품 추가에 실패했습니다.")
                    return@launch
                }
            val addedProduct = product.copy(quantity = 1, cartItemId = cartItemId)
            _updatedProduct.value = addedProduct
            refreshProductsInfo()
            loadCartProducts()
        }
    }

    fun changeProductSelection(productUiModel: ProductUiModel) {
        val products: MutableSet<ProductItem> = cartProducts.value ?: return
        if (selectedState.contains(productUiModel.id)) {
            selectedState[productUiModel.id] = selectedState[productUiModel.id]?.not() ?: false
        } else {
            selectedState[productUiModel.id] = false
        }
        products.removeIf { productUiModel.id == it.productItem.id }
        products.add(
            ProductItem(
                productUiModel.copy(
                    isChecked = selectedState[productUiModel.id] ?: false,
                ),
            ),
        )
        loadSelectAllState()
        _selectedEvent.postValue(Unit)
    }

    fun selectAll() {
        val products: MutableSet<ProductItem> = cartProducts.value ?: return

        if (isAllSelected.value == true) {
            products.forEach { product ->
                selectedState[product.productItem.id] = false
            }
        } else {
            products.forEach { product ->
                selectedState[product.productItem.id] = true
            }
        }
        loadCartProducts()
    }

    fun loadSelectAllState() {
        _isAllSelected.postValue(isAllProductsSelected())
    }

    fun order() {
        if (orderedProducts.isNotEmpty()) {
            _orderClicked.postValue(Unit)
        }
    }

    private fun isAllProductsSelected(): Boolean {
        val allProducts = cartProducts.value ?: return false
        val result = allProducts.filter { it.productItem.isChecked }.size == allProducts.size
        return result
    }

    private fun loadCartProducts() {
        if (isInitialLoad) {
            _loadingState.postValue(LoadingState.loading())
        } else {
            _loadingState.postValue(LoadingState.refreshing())
        }

        viewModelScope.launch {
            val totalSize: Long =
                cartProductRepository.getTotalElements().getOrElse {
                    _errorMessage.setValue("장바구니 총합 개수를 불러오는 데 실패했습니다.")
                    return@launch
                }
            val cartProducts: List<ProductUiModel> =
                cartProductRepository.getCartProductsInRange(0, totalSize.toInt()).getOrElse {
                    _errorMessage.setValue("장바구니 상품을 불러오는 데 실패했습니다.")
                    return@launch
                }
            val pagedProducts: List<ProductItem> =
                cartProducts
                    .map {
                        ProductItem(
                            if (selectedState.contains(it.id)) {
                                it.copy(isChecked = selectedState[it.id] == true)
                            } else {
                                it
                            },
                        )
                    }
            val items =
                TreeSet(
                    compareBy<ProductItem> { it.productItem.id },
                ).apply { addAll(pagedProducts) }

            _cartProducts.value = items
            updateTotalCount()
            updateTotalPrice()
            loadSelectAllState()

            _loadingState.value = LoadingState.loaded()
            isInitialLoad = false
        }
    }

    private fun updateTotalPrice() {
        val products: List<ProductUiModel> =
            cartProducts.value?.map { it.productItem } ?: emptyList()
        val amount =
            products
                .map { it.copy(isChecked = selectedState[it.id] ?: true) }
                .filter { it.isChecked == true }
                .sumOf { it.price * it.quantity }
        _totalPrice.postValue(amount)
    }

    private fun updateTotalCount() {
        val count = cartProducts.value?.filter { it.productItem.isChecked }?.size ?: 0
        _totalCount.postValue(count)
    }
}
