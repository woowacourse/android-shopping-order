package woowacourse.shopping.presentation.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
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
import kotlin.concurrent.thread

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
        thread {
            productRepository.findCartByProductId(id).onSuccess { cart ->
                hideError()
                val state = _uiState.value ?: return@onSuccess
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
        productHistoryRepository.getProductHistory(2).onSuccess { productHistories ->
            hideError()
            val productHistory =
                if (productHistories.isNotEmpty() && cart.product.id == productHistories.first().id) {
                    if (productHistories.size >= 2) productHistories[1] else null
                } else {
                    productHistories.firstOrNull()
                }

            val isLastProductPage =
                when {
                    productHistories.isEmpty() -> true
                    cart.product.id == productHistories.first().id -> productHistories.size < 2
                    else -> false
                }

            uiState.value?.let { state ->
                _uiState.postValue(
                    state.copy(
                        cart = cart,
                        productHistory = productHistory,
                        isLastProductPage = isLastProductPage,
                    ),
                )
            }

            insertProductHistory(cart.product)
        }.onFailure { e ->
            showError(e)
            showMessage(MessageProvider.DefaultErrorMessage)
        }
    }

    fun addToCart() {
        val cart = uiState.value?.cart ?: return
        thread {
            if (cart.id == Cart.EMPTY_CART_ID) {
                insertCart(cart)
            } else {
                updateCart(cart)
            }
        }
    }

    private fun insertCart(cart: Cart) {
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

    private fun updateCart(cart: Cart) {
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

    override fun plusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        val state = uiState.value ?: return
        state.cart?.let { product ->
            _uiState.value = state.copy(cart = product.copy(quantity = product.quantity + 1))
        }
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        val state = uiState.value ?: return
        state.cart?.let { product ->
            _uiState.value = state.copy(cart = product.copy(quantity = product.quantity - 1))
        }
    }

    private fun insertProductHistory(productValue: Product) {
        thread {
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
