package woowacourse.shopping.ui.cart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.CartWithProduct
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProduct
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartItemClickListener
import woowacourse.shopping.ui.cart.CartItemsUiState
import woowacourse.shopping.ui.cart.CartUiModel
import woowacourse.shopping.ui.products.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel(), CountButtonClickListener, AddCartClickListener, CartItemClickListener {
    private val _cart: MutableLiveData<CartItemsUiState> = MutableLiveData()
    val cart: LiveData<CartItemsUiState> = _cart

    private val _products: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()

    val products: LiveData<List<ProductWithQuantity>> = _products

    val cartOfRecommendProductCount: LiveData<Int> =
        _products.map {
            it.sumOf { it.quantity.value }
        }

    val totalPrice: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cart) { value = totalPrice() }
            addSource(_products) { value = totalPrice() }
        }

    val isTotalChbChecked: LiveData<Boolean> =
        _cart.map {
            it.cartItems.all { it.isChecked } && it.cartItems.isNotEmpty()
        }

    val checkedItemCount: LiveData<Int> =
        _cart.map {
            it.cartItems.filter { it.isChecked }.size
        }

    val isRecommendPage: MutableLiveData<Boolean> = MutableLiveData(false)

    val productWithQuantity: MediatorLiveData<ProductWithQuantityUiState> = MediatorLiveData()

    val noRecommendProductState: MutableLiveData<Boolean> = MutableLiveData(false)

    val error: MutableSingleLiveData<Throwable> = MutableSingleLiveData()

    private val _order = MutableSingleLiveData<Unit>()
    val order: SingleLiveData<Unit> = _order

    init {
        loadCartItems()
    }

    override fun addCart(productId: Long) {
        viewModelScope.launch {
            cartRepository.postCartItems(productId, 1).onSuccess {
                changeRecommendProductCount(productId)
                loadCartItems()
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelScope.launch {
            cartRepository.getCartItem(productId).onSuccess {
                updateCountToPlus(it, productId)
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelScope.launch {
            cartRepository.getCartItem(productId).onSuccess {
                updateCountToMinus(it, productId)
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    override fun removeCartItem(productId: Long) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(findCartIdByProductId(productId)).onSuccess {
                updateCartLiveData()
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    override fun clickCheckBox(productId: Long) {
        val checkedCart =
            _cart.value?.cartItems?.firstOrNull { it.productId == productId }
                ?: error("해당하는 카트 아이템이 없습니다.")
        val currentList = requireNotNull(_cart.value?.cartItems?.toMutableList())
        currentList[currentList.indexOf(checkedCart)] =
            checkedCart.copy(isChecked = !checkedCart.isChecked)
        _cart.value = CartItemsUiState(currentList, isLoading = false)
    }

    fun clickOrderButton() {
        viewModelScope.launch {
            if (isRecommendPage.value == true) {
                _cart.value?.cartItems?.let { cartRepository.order(it.map { it.id }) }
                _order.setValue(Unit)
                return@launch
            }
            recentProductRepository.findMostRecentProduct().onSuccess { recentProduct ->
                setRecommendProducts(recentProduct)
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    private suspend fun setRecommendProducts(recentProduct: RecentProduct) {
        productRepository.find(recentProduct.productId).onSuccess { product ->
            if (isRecommendPage.value == false) {
                isRecommendPage.value = true
            }
            setRecommendProducts(product)
        }.onFailure {
            error.setValue(it)
        }
    }

    private suspend fun setRecommendProducts(it: Product) {
        productRepository.getProductsByCategory(it.category).onSuccess {
            _products.value =
                it.filterNot { product -> requireNotNull(_cart.value).cartItems.any { it.productId == product.id } }
                    .map { ProductWithQuantity(product = it) }
                    .subList(0, minOf(it.size, 10))
            noRecommendProductState.value = false
        }.onFailure {
            noRecommendProductState.value = true
        }
    }

    private fun totalPrice(): Int {
        val carts = _cart.value?.cartItems?.filter { it.isChecked }?.sumOf { it.totalPrice } ?: 0
        val recommends =
            _products.value?.filter { it.quantity.value >= 1 }?.sumOf { it.totalPrice } ?: 0
        return carts + recommends
    }

    fun totalCheckBoxCheck() {
        val currentCarts = requireNotNull(_cart.value)
        _cart.value =
            CartItemsUiState(
                currentCarts.cartItems.map {
                    it.copy(isChecked = !requireNotNull(isTotalChbChecked.value))
                },
                isLoading = false,
            )
    }

    private suspend fun updateCartLiveData() {
        cartRepository.getAllCartItemsWithProduct().onSuccess {
            _cart.value =
                CartItemsUiState(
                    it.map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                    isLoading = false,
                )
        }.onFailure {
            error.setValue(it)
        }
    }

    private fun findIsCheckedByProductId(productId: Long): Boolean {
        val current = _cart.value ?: return false
        return current.cartItems.firstOrNull { it.productId == productId }?.isChecked ?: false
    }

    private suspend fun updateCountToPlus(
        it: CartWithProduct,
        productId: Long,
    ) {
        cartRepository.patchCartItem(
            it.id,
            it.quantity.value.inc(),
        ).onSuccess {
            if (isRecommendPage.value == false) {
                loadCartItems()
            } else {
                changeRecommendProductCount(productId)
                loadCartItems()
            }
        }.onFailure {
            error.setValue(it)
        }
    }

    private suspend fun updateCountToMinus(
        it: CartWithProduct,
        productId: Long,
    ) {
        if (it.quantity.value > 0) {
            cartRepository.patchCartItem(
                it.id,
                it.quantity.value.dec(),
            ).onSuccess {
                if (isRecommendPage.value == false) {
                    loadCartItems()
                } else {
                    changeRecommendProductCount(productId)
                }
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    private fun changeRecommendProductCount(productId: Long) {
        viewModelScope.launch {
            cartRepository.getCartItem(productId).onSuccess {
                val current = productWithQuantities(productId, it.quantity)
                _products.value = current
            }.onFailure {
                val current = productWithQuantities(productId, Quantity())
                _products.value = current
            }
        }
    }

    private fun productWithQuantities(
        productId: Long,
        quantity: Quantity,
    ): MutableList<ProductWithQuantity> {
        val changedRecommend =
            requireNotNull(_products.value?.firstOrNull { it.product.id == productId })
        val current = _products.value?.toMutableList() ?: mutableListOf()
        current[current.indexOf(changedRecommend)] =
            changedRecommend.copy(quantity = quantity)
        return current
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartRepository.getAllCartItemsWithProduct().onSuccess {
                _cart.value =
                    CartItemsUiState(
                        it.map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                        isLoading = true,
                    )
                _cart.value = cart.value?.copy(isLoading = false)
            }.onFailure {
                error.setValue(it)
            }
        }
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: throw ShoppingException(ShoppingError.CartNotFound)
    }

    private fun CartWithProduct.toUiModel(isChecked: Boolean) =
        CartUiModel(
            this.id,
            this.product.id,
            this.product.name,
            this.product.price,
            this.quantity,
            this.product.imageUrl,
            isChecked,
        )
}
