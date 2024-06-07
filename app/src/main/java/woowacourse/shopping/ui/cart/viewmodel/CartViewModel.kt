package woowacourse.shopping.ui.cart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
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

    val totalPrice: LiveData<Int> =
        _cart.map {
            it.cartItems.filter { it.isChecked }.sumOf { it.totalPrice }
        }

    val isTotalChbChecked: LiveData<Boolean> =
        _cart.map {
            it.cartItems.all { it.isChecked } && it.cartItems.isNotEmpty()
        }

    val checkedItemCount: LiveData<Int> =
        _cart.map {
            it.cartItems.filter { it.isChecked }.sumOf { it.quantity.value }
        }

    val isRecommendPage: MutableLiveData<Boolean> = MutableLiveData(false)

    val noRecommendProductState: MutableLiveData<Boolean> = MutableLiveData(false)

    val error: MutableSingleLiveData<Throwable> = MutableSingleLiveData()

    private val _order = MutableSingleLiveData<List<Long>>()
    val order: SingleLiveData<List<Long>> = _order

    private var removeState: Boolean = true
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            error.setValue(throwable)
        }

    init {
        loadCartItems()
    }

    override fun addCart(productId: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartRepository.postCartItems(productId, 1).onSuccess {
                changeRecommendProductCount(productId)
                loadCartItems()
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartRepository.getCartItemByProductId(productId).onSuccess {
                updateCountToPlus(it, productId)
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            cartRepository.getCartItemByProductId(productId).onSuccess {
                updateCountToMinus(it, productId)
            }
        }
    }

    override fun removeCartItem(productId: Long) {
        if (!removeState) return // 아직 아이템이 삭제되지 않은 경우

        removeState = false
        viewModelScope.launch {
            cartRepository.deleteCartItem(findCartIdByProductId(productId)).onSuccess {
                updateCartLiveData()
                removeState = true
            }.onFailure {
                removeState = false
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
        if (isRecommendPage.value == true) {
            _order.setValue(orderItemIds())
            return
        }
        viewModelScope.launch {
            recentProductRepository.findMostRecentProduct().onSuccess { recentProduct ->
                setRecommendProducts(recentProduct)
            }.onFailure {
                setRecommendPageFlag()
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

    private fun setRecommendPageFlag() {
        if (isRecommendPage.value == false) {
            isRecommendPage.value = true
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
        val recommendProducts = _products.value
        val current = _cart.value ?: return false
        if (isRecommendProduct(recommendProducts, productId)) return true
        return current.cartItems.firstOrNull { it.productId == productId }?.isChecked ?: false
    }

    private fun isRecommendProduct(
        recommendProducts: List<ProductWithQuantity>?,
        productId: Long,
    ): Boolean {
        if (recommendProducts != null) {
            if (recommendProducts.any { it.product.id == productId }) return true
        }
        return false
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
                    loadCartItems()
                }
            }
        }
    }

    private fun changeRecommendProductCount(productId: Long) {
        viewModelScope.launch {
            cartRepository.getCartItemByProductId(productId).onSuccess {
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
        viewModelScope.launch(coroutineExceptionHandler) {
            cartRepository.getAllCartItemsWithProduct().onSuccess {
                _cart.value =
                    CartItemsUiState(
                        it.map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                        isLoading = true,
                    )
                _cart.value = cart.value?.copy(isLoading = false)
            }
        }
    }

    private fun orderItemIds(): List<Long> {
        val cartIds =
            _cart.value?.cartItems?.filter { it.isChecked }
                ?.map { it.id }
                ?.toList() ?: emptyList()
        return cartIds
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
