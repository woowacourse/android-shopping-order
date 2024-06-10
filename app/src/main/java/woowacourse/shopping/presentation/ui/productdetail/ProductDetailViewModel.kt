package woowacourse.shopping.presentation.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductHistoryRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.productdetail.ProductDetailActivity.Companion.PUT_EXTRA_PRODUCT_ID

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val productHistoryRepository: ProductHistoryRepository,
) : BaseViewModel(), ProductCountHandler {
    private var id = requireNotNull(savedStateHandle.get<Long>(PUT_EXTRA_PRODUCT_ID))

    private val _uiState: MutableLiveData<ProductDetailUiState> =
        MutableLiveData(ProductDetailUiState())
    val uiState: LiveData<ProductDetailUiState> get() = _uiState

    init {
        getProduct()
    }

    fun getProduct() {
        launch {
            productRepository.findCartByProductId(id).onSuccess { cart ->
                hideError()
                val state = uiState.value ?: return@onSuccess
                if (state.isLastProductPage) {
                    _uiState.postValue(state.copy(cart = cart))
                    insertProductHistory(cart.product)
                } else {
                    getProductHistory(cart)
                }
            }.onFailure { e ->
                showError(e)
            }
        }
    }

    override fun retry() {
        getProduct()
    }

    private fun getProductHistory(cart: Cart) {
        launch {
            productHistoryRepository.getProductHistory(2).onSuccess { productHistories ->
                hideError()
                val state = uiState.value ?: return@launch

                _uiState.postValue(
                    state.copy(
                        cart = cart,
                        productHistory = getMostHistoryProduct(productHistories, cart.product.id),
                        isLastProductPage = isLastProductPage(productHistories, cart),
                    ),
                )

                insertProductHistory(cart.product)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    private fun getMostHistoryProduct(
        productHistories: List<Product>,
        cartProductId: Long,
    ): Product? {
        return if (isMostHistoryProduct(
                productHistories,
                cartProductId,
            )
        ) {
            getSecondHistoryProduct(productHistories)
        } else {
            productHistories.firstOrNull()
        }
    }

    private fun isMostHistoryProduct(
        productHistories: List<Product>,
        cartProductId: Long,
    ): Boolean {
        return productHistories.isNotEmpty() && cartProductId == productHistories.first().id
    }

    private fun getSecondHistoryProduct(productHistories: List<Product>): Product? {
        return if (productHistories.size >= 2) productHistories[1] else null
    }

    private fun isLastProductPage(
        productHistories: List<Product>,
        cart: Cart,
    ) = when {
        productHistories.isEmpty() -> true
        cart.product.id == productHistories.first().id -> productHistories.size < 2
        else -> false
    }

    fun addToCart() {
        val cart = uiState.value?.cart ?: return
        launch {
            if (shouldInsertCart(cart)) {
                insertCart(cart)
            } else {
                updateCart(cart)
            }
        }
    }

    private fun shouldInsertCart(cart: Cart): Boolean = cart.id == Cart.EMPTY_CART_ID

    private fun insertCart(cart: Cart) {
        launch {
            shoppingCartRepository.postCartItem(
                productId = cart.product.id,
                quantity = cart.quantity,
            ).onSuccess {
                hideError()
                showMessage(ProductDetailMessage.AddToCartSuccessMessage)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    private fun updateCart(cart: Cart) {
        launch {
            shoppingCartRepository.patchCartItem(
                cartId = cart.id,
                quantity = cart.quantity,
            ).onSuccess {
                hideError()
                showMessage(ProductDetailMessage.AddToCartSuccessMessage)
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    override fun plusProductQuantity(productId: Long) {
        val state = uiState.value ?: return
        val cart = state.cart ?: return
        _uiState.value = state.copy(cart = cart.copy(quantity = cart.quantity + 1))
    }

    override fun minusProductQuantity(productId: Long) {
        val state = uiState.value ?: return
        val cart = state.cart ?: return
        _uiState.value = state.copy(cart = cart.copy(quantity = cart.quantity - 1))
    }

    private fun insertProductHistory(productValue: Product) {
        launch {
            productHistoryRepository.insertProductHistory(
                productId = productValue.id,
                name = productValue.name,
                price = productValue.price,
                category = productValue.category,
                imageUrl = productValue.imageUrl,
            ).onSuccess {
                hideError()
            }.onFailure { e ->
                showError(e)
                showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    fun refresh(productId: Long) {
        val state = uiState.value ?: return
        _uiState.value = state.copy(isLastProductPage = true)
        id = productId
        getProduct()
    }

    companion object {
        fun factory(
            productRepository: ProductRepository,
            shoppingCartRepository: ShoppingCartRepository,
            productHistoryRepository: ProductHistoryRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory { extras ->
                ProductDetailViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    productRepository = productRepository,
                    shoppingCartRepository = shoppingCartRepository,
                    productHistoryRepository = productHistoryRepository,
                )
            }
        }
    }
}
