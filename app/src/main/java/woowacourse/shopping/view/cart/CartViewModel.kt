package woowacourse.shopping.view.cart

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.cart.adapter.ShoppingCartViewItem.CartViewItem
import woowacourse.shopping.view.home.HomeClickListener
import woowacourse.shopping.view.home.HomeViewModel.Companion.ASCENDING_SORT_ORDER
import woowacourse.shopping.view.home.adapter.product.HomeViewItem
import woowacourse.shopping.view.state.UiState

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
) : ViewModel(), CartItemClickListener, QuantityClickListener, HomeClickListener {
    private val _isCartScreen = MutableLiveData(true)
    val isCartScreen: LiveData<Boolean>
        get() = _isCartScreen

    private val _cartUiState =
        MutableLiveData<UiState<List<CartViewItem>>>(UiState.Loading)
    val cartUiState: LiveData<UiState<List<CartViewItem>>>
        get() = _cartUiState

    private val _navigateToDetail = MutableLiveData<Event<Int>>()
    val navigateToDetail: LiveData<Event<Int>>
        get() = _navigateToDetail

    private val _navigateToRecommend = MutableLiveData<Event<Unit>>()
    val navigateToRecommend: LiveData<Event<Unit>>
        get() = _navigateToRecommend

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack: LiveData<Event<Unit>>
        get() = _navigateBack

    private val _notifyDeletion = MutableLiveData<Event<Boolean>>()
    val notifyDeletion: LiveData<Event<Boolean>>
        get() = _notifyDeletion

    private val _updatedCartItem = MutableLiveData<CartItem>()
    val updatedCartItem: LiveData<CartItem>
        get() = _updatedCartItem

    private val _selectChangeId = MutableLiveData<Int>()
    val selectChangeId: LiveData<Int>
        get() = _selectChangeId

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    private val cartItems: MutableList<CartViewItem> = mutableListOf()

    private val _isBackButtonClicked = MutableLiveData<Event<Boolean>>()
    val isBackButtonClicked: LiveData<Event<Boolean>>
        get() = _isBackButtonClicked

    private val _isEntireCheckboxSelected = MutableLiveData(false)
    val isEntireCheckboxSelected: LiveData<Boolean>
        get() = _isEntireCheckboxSelected

    private val _selectedItems =
        MutableLiveData<List<CartViewItem>>(emptyList())
    val selectedItems: LiveData<List<CartViewItem>>
        get() = _selectedItems

    val recentProduct = recentProductRepository.findMostRecentProduct()

    private val _recommendedProducts =
        MutableLiveData<UiState<List<HomeViewItem.ProductViewItem>>>(UiState.Loading)
    val recommendedProducts: LiveData<UiState<List<HomeViewItem.ProductViewItem>>>
        get() = _recommendedProducts

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            loadPage()
        }, 1000)
    }

    private fun loadPage() {
        runCatching {
            val totalQuantity = cartRepository.getCartTotalQuantity().getOrNull()?.quantity ?: 0
            val cartItemResponse =
                cartRepository.getCartItems(0, totalQuantity, DESCENDING_SORT_ORDER)
                    .getOrNull()
            val cartViewItems = (cartItemResponse?.cartItems?.map(::CartViewItem) ?: emptyList())
            cartItems.addAll(cartViewItems)
            _isEmpty.value = cartItems.isEmpty()
            _cartUiState.value = UiState.Success(cartItems)
        }.onFailure {
            _cartUiState.value = UiState.Error(it)
        }
    }

    fun deleteItem(itemId: Int) {
        cartRepository.deleteCartItem(itemId)
        val position = cartItems.indexOfFirst { it.cartItem.cartItemId == itemId }
        cartItems.removeAt(position)
        _isEmpty.value = cartItems.isEmpty()
        _cartUiState.value = UiState.Success(cartItems)
        _totalPrice.value =
            selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
    }

    fun selectAll(isChecked: Boolean) {
        if (isChecked) {
            _selectedItems.value = cartItems
            _totalPrice.value =
                selectedItems.value?.sumOf { item ->
                    item.cartItem.totalPrice()
                }
        } else {
            _selectedItems.value = mutableListOf()
            _totalPrice.value =
                selectedItems.value?.sumOf { item ->
                    item.cartItem.totalPrice()
                }
        }
    }

    override fun onCartItemClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onDeleteButtonClick(itemId: Int) {
        deleteItem(itemId)
        val cartItem = cartItems.firstOrNull { it.cartItem.cartItemId == itemId } ?: return
        if (selectedItems.value?.contains(cartItem) == true) {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        }
        _notifyDeletion.value = Event(true)
    }

    override fun onSelectChanged(
        itemId: Int,
        isSelected: Boolean,
    ) {
        val cartItem = cartItems.firstOrNull { it.cartItem.cartItemId == itemId } ?: return
        if (selectedItems.value?.contains(cartItem) == false && isSelected) {
            _selectedItems.value = selectedItems.value?.plus(cartItem)
        } else {
            _selectedItems.value = selectedItems.value?.minus(cartItem)
        }
        _selectChangeId.value = itemId
        _totalPrice.value =
            selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
    }

    override fun onQuantityPlusButtonClick(productId: Int) {
        var targetCartItem =
            cartItems.firstOrNull { it.cartItem.product.productId == productId } ?: return
        // TODO
        targetCartItem =
            targetCartItem.copy(
                cartItem = targetCartItem.cartItem.plusQuantity(),
            )
        val position =
            cartItems.indexOfFirst { it.cartItem.cartItemId == targetCartItem.cartItem.cartItemId }
        cartItems[position] = targetCartItem
        if (selectedItems.value?.contains(targetCartItem) == true) {
            val selectedPosition =
                selectedItems.value?.indexOfFirst { it.cartItem.product.productId == productId }
                    ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = targetCartItem
        }
        cartRepository.updateCartItem(
            targetCartItem.cartItem.cartItemId,
            targetCartItem.cartItem.quantity,
        )
        _updatedCartItem.value = targetCartItem.cartItem
        _totalPrice.value =
            selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
    }

    override fun onQuantityMinusButtonClick(productId: Int) {
        var targetCartItem =
            cartItems.firstOrNull { it.cartItem.product.productId == productId } ?: return
        targetCartItem =
            targetCartItem.copy(
                cartItem = targetCartItem.cartItem.minusQuantity(),
            )
        val position =
            cartItems.indexOfFirst { it.cartItem.cartItemId == targetCartItem.cartItem.cartItemId }
        cartItems[position] = targetCartItem
        if (selectedItems.value?.contains(targetCartItem) == true) {
            val selectedPosition =
                selectedItems.value?.indexOfFirst { it.cartItem.product.productId == productId }
                    ?: return
            (_selectedItems.value as MutableList)[selectedPosition] = targetCartItem
        }
        cartRepository.updateCartItem(
            targetCartItem.cartItem.cartItemId,
            targetCartItem.cartItem.quantity,
        )
        _updatedCartItem.value = targetCartItem.cartItem
        _totalPrice.value =
            selectedItems.value?.sumOf { item ->
                item.cartItem.totalPrice()
            }
    }

    fun onNextButtonClick() {
        if (isCartScreen.value == true) {
            _navigateToRecommend.value = Event(Unit)
            _isCartScreen.value = false
            getRecommendedItems()
        } else {
            val result =
                orderRepository.postOrder(
                    selectedItems.value?.map {
                        it.cartItem.cartItemId
                    } ?: emptyList(),
                ).getOrNull()
            if (result != null) _navigateBack.value = Event(Unit)
        }
    }

    private fun getRecommendedItems() {
        val response =
            productRepository.getProductResponse(
                recentProduct?.category,
                0,
                10 + cartItems.size,
                ASCENDING_SORT_ORDER
            )
                .getOrNull()
        val cartItems =
            response?.products?.filter { it.productId !in cartItems.map { cartItem -> cartItem.cartItem.product.productId } }
                ?: emptyList()
        _recommendedProducts.value =
            UiState.Success(
                cartItems.map {
                    HomeViewItem.ProductViewItem(it)
                },
            )
    }

    fun onBackButtonClick() {
        _isBackButtonClicked.value = Event(true)
    }

    override fun onProductClick(productId: Int) {
        _navigateToDetail.value = Event(productId)
    }

    override fun onLoadMoreButtonClick() {
        Unit
    }

    override fun onShoppingCartButtonClick() {
        Unit
    }

    override fun onPlusButtonClick(product: Product) {
        cartRepository.addCartItem(product.productId, 1).getOrNull()
    }

    companion object {
        const val DESCENDING_SORT_ORDER = "desc"
    }
}
