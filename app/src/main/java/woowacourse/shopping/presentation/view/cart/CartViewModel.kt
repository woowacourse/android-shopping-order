package woowacourse.shopping.presentation.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.model.toCartItem
import woowacourse.shopping.presentation.model.toCartItemUiModel
import woowacourse.shopping.presentation.model.toUiModel
import kotlin.math.max

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItemUiModel>>()
    val cartItems: LiveData<List<CartItemUiModel>> = _cartItems

    private val _recommendedProducts = MutableLiveData<List<ProductUiModel>>()
    val recommendedProducts: LiveData<List<ProductUiModel>> = _recommendedProducts

    private val _deleteState = MutableLiveData<Long>()
    val deleteState: LiveData<Long> = _deleteState

    private val _itemUpdateEvent = MutableLiveData<ProductUiModel>()
    val itemUpdateEvent: LiveData<ProductUiModel> = _itemUpdateEvent

    private val _page = MutableLiveData(START_PAGE)
    val page: LiveData<Int> = _page

    private var _hasMore = MutableLiveData<Boolean>()
    val hasMore: LiveData<Boolean> = _hasMore

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _totalCount = MutableLiveData<Int>()
    val totalCount: LiveData<Int> = _totalCount

    private val _allSelected = MutableLiveData<Boolean>()
    val allSelected: LiveData<Boolean> = _allSelected

    private val selectedStates = mutableMapOf<Long, Boolean>()

    init {
        fetchShoppingCart(false)
    }

    fun fetchShoppingCart(
        isNextPage: Boolean,
        isRefresh: Boolean = false,
    ) {
        val currentPage = _page.value ?: DEFAULT_PAGE
        val newPage = calculatePage(isNextPage, currentPage, isRefresh)

        cartRepository.getCartItems(
            page = newPage - DEFAULT_PAGE,
            limit = PAGE_SIZE,
        ) { products, hasMore ->
            val updatedItems =
                products.map {
                    it.toCartItemUiModel().copy(isSelected = selectedStates[it.cartId] ?: false)
                }
            _cartItems.postValue(updatedItems)
            _page.postValue(newPage)
            _hasMore.postValue(hasMore)
            updateSelectionInfo()
        }
    }

    fun initialAddToCart(product: ProductUiModel) {
        val updatedProduct = product.copy(amount = 1)
        cartRepository.addCartItem(updatedProduct.toCartItem()) {
            cartRepository.getAllCartItems { cartItems ->
                val found = cartItems?.find { it.product.id == updatedProduct.id }
                if (found != null) {
                    val newUiModel =
                        found.toCartItemUiModel().copy(
                            isSelected = true,
                        )
                    updateProductAmountInLists(found.cartId, newUiModel.cartItem.amount)
                    _itemUpdateEvent.postValue(newUiModel.cartItem.toUiModel())
                    _cartItems.postValue((_cartItems.value ?: emptyList()) + newUiModel)
                    updateSelectionInfo()
                } else {
                    fetchShoppingCart(isNextPage = false, isRefresh = true)
                }
            }
        }
    }

    fun deleteProduct(cartItem: CartItemUiModel) {
        selectedStates.remove(cartItem.cartItem.cartId)
        cartRepository.deleteCartItem(cartItem.cartItem.cartId) {
            _deleteState.postValue(it)
        }
    }

    private fun updateProductAmountInLists(
        cartId: Long,
        newAmount: Int,
    ) {
        _recommendedProducts.value =
            _recommendedProducts.value
                ?.map { product ->
                    if (product.cartId == cartId) {
                        val updated = product.copy(amount = newAmount)
                        _itemUpdateEvent.postValue(updated) // 상품 수량 변경 시 이벤트 발생
                        updated
                    } else {
                        product
                    }
                }

        _cartItems.postValue(
            _cartItems.value
                ?.map {
                    if (it.cartItem.cartId == cartId) {
                        it.copy(cartItem = it.cartItem.copy(amount = newAmount))
                    } else {
                        it
                    }
                }?.toList(),
        )
    }

    fun increaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        cartRepository.increaseCartItem(cartItem) { id ->
            getCartItemById(id) {
                it?.let { item ->
                    val updatedItem =
                        item.toCartItemUiModel().copy(
                            isSelected = selectedStates[item.cartId] ?: false,
                        )
                    updateProductAmountInLists(item.cartId, updatedItem.cartItem.amount)
                    _itemUpdateEvent.postValue(updatedItem.cartItem.toUiModel()) // 변경: ProductUiModel 이벤트 발행
                    updateProducts(updatedItem)
                }
            }
        }
    }

    fun decreaseAmount(product: ProductUiModel) {
        val cartItem = product.toCartItem()
        if (cartItem.amount <= 1) {
            deleteProduct(cartItem.toCartItemUiModel())
            updateProductAmountInLists(cartItem.cartId, 0)
        } else {
            cartRepository.decreaseCartItem(cartItem) { id ->
                getCartItemById(id) {
                    it?.let { item ->
                        val updatedItem =
                            item.toCartItemUiModel().copy(
                                isSelected = selectedStates[item.cartId] ?: false,
                            )

                        updateProductAmountInLists(item.cartId, updatedItem.cartItem.amount)

                        _itemUpdateEvent.postValue(updatedItem.cartItem.toUiModel()) // 변경: ProductUiModel 이벤트 발행
                        updateProducts(updatedItem)
                    }
                }
            }
        }
    }

    fun setCartItemSelection(
        cartItem: CartItemUiModel,
        isSelected: Boolean,
    ) {
        selectedStates[cartItem.cartItem.cartId] = isSelected
        _cartItems.postValue(
            _cartItems.value?.map {
                if (it.cartItem.cartId == cartItem.cartItem.cartId) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            },
        )
        updateSelectionInfo()
    }

    fun setAllSelections(selectAll: Boolean) {
        cartRepository.getAllCartItems { allItems ->
            allItems?.forEach { cartItem ->
                selectedStates[cartItem.cartId] = selectAll
                _cartItems.postValue(
                    _cartItems.value?.map {
                        if (it.cartItem.cartId == cartItem.cartId) {
                            it.copy(isSelected = selectAll)
                        } else {
                            it
                        }
                    },
                )
            }
            updateSelectionInfo()
            fetchShoppingCart(false, true)
        }
    }

    fun fetchRecommendedProducts() {
        productRepository.getMostRecentProduct {
            val recommendedCategory = it?.category
            productRepository.loadProductsByCategory(recommendedCategory.orEmpty()) {
                cartRepository.getAllCartItems { allCartItems ->
                    val products =
                        it
                            .filter { !allCartItems.orEmpty().map { it.product.id }.contains(it.id) }
                            .take(10)
                    _recommendedProducts.postValue(products.map { it.toUiModel() })
                }
            }
        }
    }

    private fun calculatePage(
        isNextPage: Boolean,
        currentPage: Int,
        isRefresh: Boolean,
    ): Int =
        when {
            isRefresh -> currentPage
            isNextPage -> currentPage + DEFAULT_PAGE
            else -> max(DEFAULT_PAGE, currentPage - DEFAULT_PAGE)
        }

    private fun getCartItemById(
        id: Long,
        callback: (CartItem?) -> Unit,
    ) {
        cartRepository.getAllCartItems { items ->
            val foundItem = items?.find { it.cartId == id }
            callback(foundItem)
        }
    }

    private fun updateProducts(updatedItem: CartItemUiModel) {
        _cartItems.value =
            _cartItems.value?.map { cartItem ->
                if (cartItem.cartItem.cartId == updatedItem.cartItem.cartId) {
                    updatedItem
                } else {
                    cartItem
                }
            }
        updateSelectionInfo()
    }

    private fun updateSelectionInfo() {
        cartRepository.getAllCartItems { allItems ->
            val selectedItemIds = selectedStates.filter { it.value }.map { it.key }.toSet()
            val selectedItems =
                (allItems.orEmpty()).filter { selectedItemIds.contains(it.cartId) }
            _totalPrice.postValue(selectedItems.sumOf { it.totalPrice })
            _totalCount.postValue(selectedItems.sumOf { it.amount })
            _allSelected.postValue(selectedItems.size == allItems?.size)
        }
    }

    companion object {
        private const val START_PAGE = 0
        private const val DEFAULT_PAGE = 1
        private const val PAGE_SIZE = 5

        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val productRepository = RepositoryProvider.productRepository
                    val cartRepository = RepositoryProvider.cartRepository
                    return CartViewModel(productRepository, cartRepository) as T
                }
            }
    }
}
