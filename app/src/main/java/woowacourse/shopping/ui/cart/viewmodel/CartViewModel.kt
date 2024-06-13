package woowacourse.shopping.ui.cart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.exception.ShoppingError
import woowacourse.shopping.data.model.RecentProduct
import woowacourse.shopping.domain.model.cart.CartWithProduct
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.product.ProductWithQuantity
import woowacourse.shopping.domain.model.product.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.base.BaseViewModel
import woowacourse.shopping.ui.cart.CartItemClickListener
import woowacourse.shopping.ui.cart.CartItemsUiState
import woowacourse.shopping.ui.cart.toUiModel
import woowacourse.shopping.ui.listener.AddCartClickListener
import woowacourse.shopping.ui.listener.CountButtonClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : BaseViewModel(), CountButtonClickListener, AddCartClickListener, CartItemClickListener {
    private val _cart: MutableLiveData<CartItemsUiState> =
        MutableLiveData(
            CartItemsUiState(
                emptyList(),
            ),
        )
    val cart: LiveData<CartItemsUiState> = _cart

    private val _products: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()

    val products: LiveData<List<ProductWithQuantity>> = _products

    val totalPrice: LiveData<Int> =
        _cart.map { cartItemUiState ->
            cartItemUiState.cartItems.filter { it.isChecked }.sumOf { it.totalPrice }
        }

    val isTotalChbChecked: LiveData<Boolean> =
        _cart.map { cartItemUiState ->
            cartItemUiState.cartItems.all { it.isChecked } && cartItemUiState.cartItems.isNotEmpty()
        }

    val checkedItemCount: LiveData<Int> =
        _cart.map { cartItemUiState ->
            cartItemUiState.cartItems.filter { it.isChecked }.sumOf { it.quantity.value }
        }

    val isRecommendPage: MutableLiveData<Boolean> = MutableLiveData(false)

    val noRecommendProductState: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _order = MutableSingleLiveData<List<Long>>()
    val order: SingleLiveData<List<Long>> = _order

    private var removeState: Boolean = true

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
                setError(it as ShoppingError)
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
            setError(it as ShoppingError)
        }
    }

    private fun setRecommendPageFlag() {
        if (isRecommendPage.value == false) {
            isRecommendPage.value = true
        }
    }

    private suspend fun setRecommendProducts(it: Product) {
        productRepository.getProductsByCategory(it.category).onSuccess { products ->
            _products.value =
                products.filterNot { product ->
                    requireNotNull(_cart.value).cartItems.any { cartUiModel ->
                        cartUiModel.productId == product.id
                    }
                }
                    .map { ProductWithQuantity(product = it) }
                    .subList(0, minOf(products.size, 10))
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
        cartRepository.getAllCartItemsWithProduct().onSuccess { cartWithProducts ->
            _cart.value =
                CartItemsUiState(
                    cartWithProducts.map { it.toUiModel(findIsCheckedByProductId(it.product.id)) },
                    isLoading = false,
                )
        }.onFailure {
            setError(it as ShoppingError)
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
        if (recommendProducts == null) return false
        return recommendProducts.any { it.product.id == productId }
    }

    private suspend fun updateCountToPlus(
        it: CartWithProduct,
        productId: Long,
    ) {
        cartRepository.patchCartItem(
            it.id,
            it.quantity.value.inc(),
        ).onSuccess {
            if (isRecommendPage.value == true) {
                changeRecommendProductCount(productId)
            }
            loadCartItems()
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
                if (isRecommendPage.value == true) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
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

    private fun loadCartItems() {
        _cart.value = _cart.value?.copy(isLoading = true)
        viewModelScope.launch(coroutineExceptionHandler) {
            cartRepository.getAllCartItemsWithProduct().onSuccess {
                _cart.value =
                    CartItemsUiState(
                        it.map { cartWithProduct ->
                            cartWithProduct.toUiModel(findIsCheckedByProductId(cartWithProduct.product.id))
                        },
                        isLoading = false,
                    )
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

    private fun orderItemIds(): List<Long> {
        return _cart.value?.cartItems?.filter { it.isChecked }
            ?.map { it.id }
            ?.toList() ?: emptyList()
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: throw IllegalStateException()
    }
}
