package woowacourse.shopping.ui.cart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError
import woowacourse.shopping.domain.result.getOrNull
import woowacourse.shopping.domain.result.getOrThrow
import woowacourse.shopping.domain.result.onError
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.cart.CartError
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartItemsUiState
import woowacourse.shopping.ui.cart.cartitem.uimodel.CartUiModel
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.BaseViewModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class CartViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : BaseViewModel(), CountButtonClickListener, AddCartClickListener {
    private val _recommendProducts: MutableLiveData<List<ProductWithQuantity>> = MutableLiveData()
    val recommendProducts: LiveData<List<ProductWithQuantity>> = _recommendProducts

    private val _errorScope: MutableSingleLiveData<CartError> = MutableSingleLiveData()
    val errorScope: SingleLiveData<CartError> get() = _errorScope

    val cartOfRecommendProductCount: LiveData<Int> =
        _recommendProducts.map {
            it.sumOf { it.quantity.value }
        }

    private val _cart: MutableLiveData<CartItemsUiState> = MutableLiveData()
    val cart: LiveData<CartItemsUiState> = _cart

    val totalPrice: MediatorLiveData<Int> =
        MediatorLiveData<Int>().apply {
            addSource(_cart) { value = totalPrice() }
            addSource(_recommendProducts) { value = totalPrice() }
        }

    val isTotalChbChecked: LiveData<Boolean> =
        _cart.map {
            it.cartItems.all { it.isChecked }
        }

    val checkedItemCount: LiveData<Int> =
        _cart.map {
            it.cartItems.filter { it.isChecked }.size
        }

    val isRecommendPage: MutableLiveData<Boolean> = MutableLiveData(false)

    val productWithQuantity: MediatorLiveData<ProductWithQuantityUiState> = MediatorLiveData()

    val noRecommendProductState: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun currentCartState(): CartItemsUiState =
        _cart.value ?: CartItemsUiState(emptyList(), true)

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelLaunch {
            _cart.value = currentCartState().copy(isLoading = true)
            cartRepository.getAllCartItems().onSuccess { carts ->
                _cart.value =
                    currentCartState().copy(
                        cartItems = carts.map { it.toUiModel(isAlreadyChecked(it.product.id)) },
                        isLoading = false,
                    )
            }.onError {
                setError(it, CartError.LoadCart)
            }
        }
    }

    fun loadRecommendProducts() {
        viewModelLaunch {
            val recentProductId: Long =
                recentProductRepository.getMostRecentProduct().getOrNull()?.productId
                    ?: DEFAULT_RECENT_PRODUCT_ID
            val category =
                productRepository.getProductById(recentProductId).getOrNull()?.category
                    ?: DEFAULT_RECOMMEND_CATEGORY
            productRepository.getAllRecommendProducts(category).onSuccess {
                _recommendProducts.value = it.map { ProductWithQuantity(product = it) }
                noRecommendProductState.value = false
            }.onError {
                setError(it, CartError.LoadRecommend)
            }
        }
    }

    private fun totalPrice(): Int {
        val carts =
            _cart.value?.cartItems?.filter { it.isChecked }?.sumOf { it.totalPrice }
                ?: DEFAULT_TOTAL_PRICE
        val recommends =
            _recommendProducts.value?.filter { it.quantity.value >= 1 }?.sumOf { it.totalPrice }
                ?: DEFAULT_TOTAL_PRICE
        return carts + recommends
    }

    fun removeCartItem(productId: Long) {
        viewModelLaunch {
            cartRepository.deleteCartItem(findCartIdByProductId(productId)).onSuccess {
                loadCartItems()
            }.onError {
                setError(it, CartError.UpdateCart)
            }
        }
    }

    fun clickCheckBox(productId: Long) {
        val checkedCart =
            _cart.value?.cartItems?.firstOrNull { it.productId == productId }
                ?: error("해당하는 카트 아이템이 없습니다.")
        val currentList = requireNotNull(_cart.value?.cartItems?.toMutableList())
        currentList[currentList.indexOf(checkedCart)] =
            checkedCart.copy(isChecked = !checkedCart.isChecked)
        _cart.value = CartItemsUiState(currentList, isLoading = false)
    }

    fun totalCheckBoxCheck(isChecked: Boolean) {
        val currentCarts = requireNotNull(_cart.value)
        _cart.value =
            CartItemsUiState(
                currentCarts.cartItems.map {
                    it.copy(isChecked = isChecked)
                },
                isLoading = false,
            )
    }

    override fun addCart(productId: Long) {
        viewModelLaunch {
            cartRepository.postCartItems(productId, INITIAL_CART_COUNT).onSuccess {
                changeRecommendProductCount(productId)
                loadCartItems()
            }.onError {
                setError(it, CartError.UpdateCart)
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelLaunch {
            val cartItem = cartRepository.getCartItem(productId).getOrThrow()
            cartRepository.patchCartItem(
                cartItem.id,
                cartItem.quantity.value.inc(),
            ).onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.onError {
                setError(it, CartError.UpdateCart)
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelLaunch {
            val cartItem = cartRepository.getCartItem(productId).getOrThrow()
            if (cartItem.quantity.value <= 0) return@viewModelLaunch
            cartRepository.patchCartItem(
                cartItem.id,
                cartItem.quantity.value.dec(),
            ).onSuccess {
                if (requireNotNull(isRecommendPage.value)) {
                    changeRecommendProductCount(productId)
                }
                loadCartItems()
            }.onError {
                setError(it, CartError.UpdateCart)
            }
        }
    }

    private fun changeRecommendProductCount(productId: Long) {
        viewModelLaunch {
            cartRepository.getCartItem(productId).onSuccess { cartItem ->
                val current = productWithQuantities(productId, cartItem.quantity)
                _recommendProducts.value = current
            }.onError { error ->
                if (error is DataError.NotFound) {
                    val current = productWithQuantities(productId, Quantity())
                    _recommendProducts.value = current
                } else {
                    setError(error, CartError.UpdateCart)
                }
            }
        }
    }

    private fun productWithQuantities(
        productId: Long,
        quantity: Quantity,
    ): MutableList<ProductWithQuantity> {
        val changedRecommend =
            requireNotNull(_recommendProducts.value?.firstOrNull { it.product.id == productId })
        val current = _recommendProducts.value?.toMutableList() ?: mutableListOf()
        current[current.indexOf(changedRecommend)] =
            changedRecommend.copy(quantity = quantity)
        return current
    }

    private fun findCartIdByProductId(productId: Long): Long {
        return cart.value?.cartItems?.firstOrNull { it.productId == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }


    private fun CartWithProduct.toUiModel(isChecked: Boolean) =
        CartUiModel(
            this.id,
            this.product.id,
            this.product.name,
            this.product.price,
            this.quantity.value,
            this.product.imageUrl,
            isChecked,
        )

    private fun isAlreadyChecked(productId: Long): Boolean =
        _cart.value?.cartItems?.firstOrNull {
            it.productId == productId
        }?.isChecked ?: false || _recommendProducts.value?.any { it.product.id == productId && it.quantity.value > 0 } ?: false

    private fun setError(dataError: DataError, errorScope: CartError) {
        if (dataError is ShowError) {
            _dataError.setValue(dataError)
        } else {
            _errorScope.setValue(errorScope)
        }
    }

    companion object {
        private const val DEFAULT_RECENT_PRODUCT_ID = 0L
        private const val DEFAULT_RECOMMEND_CATEGORY = "fashion"
        private const val DEFAULT_TOTAL_PRICE = 0
        private const val INITIAL_CART_COUNT = 1
    }
}
