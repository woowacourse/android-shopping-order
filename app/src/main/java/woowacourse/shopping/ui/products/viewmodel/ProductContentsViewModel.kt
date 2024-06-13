package woowacourse.shopping.ui.products.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.ShowError
import woowacourse.shopping.domain.result.getOrNull
import woowacourse.shopping.domain.result.onError
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.toUiModel
import woowacourse.shopping.ui.products.uimodel.ProductContentError
import woowacourse.shopping.ui.products.uimodel.ProductItemClickListener
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.BaseViewModel
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class ProductContentsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : BaseViewModel(), CountButtonClickListener, ProductItemClickListener, AddCartClickListener {
    private val products: MutableLiveData<List<Product>> = MutableLiveData()

    private val cart: MutableLiveData<List<CartWithProduct>> = MutableLiveData()

    val productWithQuantity: MediatorLiveData<ProductWithQuantityUiState> =
        MediatorLiveData<ProductWithQuantityUiState>().apply {
            addSource(products) { value = updateProductWithQuantity() }
            addSource(cart) { value = updateProductWithQuantity() }
        }

    val isCartEmpty: LiveData<Boolean> =
        cart.map {
            it.isEmpty()
        }

    val totalProductCount: LiveData<Int> =
        cart.map {
            if (it.isEmpty()) {
                DEFAULT_CART_ITEMS_COUNT
            } else {
                it.sumOf { cartItem -> cartItem.quantity.value }
            }
        }

    private val _recentProducts: MutableLiveData<List<Product>> = MutableLiveData()
    val recentProducts: LiveData<List<Product>> = _recentProducts

    private val _productDetailId = MutableSingleLiveData<Long>()
    val productDetailId: SingleLiveData<Long> get() = _productDetailId

    private val _errorScope: MutableSingleLiveData<ProductContentError> = MutableSingleLiveData()
    val errorScope: SingleLiveData<ProductContentError> get() = _errorScope

    private fun currentProduct(): ProductWithQuantityUiState =
        productWithQuantity.value ?: ProductWithQuantityUiState.DEFAULT

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelLaunch {
            productWithQuantity.value = currentProduct().copy(isLoading = true)
            productRepository.getAllProducts(
                currentProduct().productWithQuantities.size / PAGE_SIZE,
                PAGE_SIZE,
            ).onSuccess { loadedProducts ->
                products.value = (products.value ?: emptyList()) + loadedProducts
            }.onError {
                setError(it, ProductContentError.Product)
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelLaunch {
            cartRepository.patchCartItem(
                findCartItemByProductId(productId),
                findCartItemQuantityByProductId(productId).inc().value,
            ).onSuccess {
                loadCartItems()
            }.onError {
                setError(it, ProductContentError.Cart)
            }
        }
    }

    override fun minusCount(productId: Long) {
        viewModelLaunch {
            val currentCount = findCartItemQuantityByProductId(productId).dec().value
            if (currentCount == NO_CART_STATE) {
                cartRepository.deleteCartItem(findCartItemByProductId(productId))
                    .onSuccess { loadCartItems() }.onError {
                        setError(it, ProductContentError.Cart)
                    }
            } else {
                cartRepository.patchCartItem(findCartItemByProductId(productId), currentCount)
                    .onError {
                        setError(it, ProductContentError.Cart)
                    }
            }
        }
    }

    override fun itemClickListener(productId: Long) {
        _productDetailId.setValue(productId)
    }

    override fun addCart(productId: Long) {
        viewModelLaunch {
            cartRepository.postCartItems(productId, 1).onSuccess {
                loadCartItems()
            }.onError {
                setError(it, ProductContentError.Cart)
            }
        }
    }

    fun loadCartItems() {
        viewModelLaunch {
            productWithQuantity.value = currentProduct().copy(isLoading = true)
            cartRepository.getAllCartItems().onSuccess { carts ->
                cart.value = carts
            }.onError {
                setError(it, ProductContentError.Cart)
            }
        }
    }

    fun loadRecentProducts() {
        viewModelLaunch {
            recentProductRepository.getAllRecentProducts().onSuccess { recentProducts ->
                _recentProducts.value =
                    recentProducts.mapNotNull {
                        productRepository.getProductById(it.productId).getOrNull()
                    }
            }.onError {
                setError(it, ProductContentError.RecentProduct)
            }
        }
    }

    private fun updateProductWithQuantity(): ProductWithQuantityUiState {
        val currentProducts = products.value ?: emptyList()
        val updatedList =
            currentProducts.map { product ->
                ProductWithQuantity(product = product, quantity = getQuantity(product.id))
            }

        return currentProduct().copy(
            productWithQuantities = updatedList.map { it.toUiModel() },
            isLoading = false,
        )
    }

    private fun getQuantity(productId: Long): Quantity {
        val cart = findCartContainProduct(productId) ?: return Quantity()
        return cart.quantity
    }

    private fun findCartContainProduct(productId: Long): CartWithProduct? {
        cart.value?.let { items ->
            return items.find { it.product.id == productId }
        }
        return null
    }

    private fun findCartItemByProductId(productId: Long): Long {
        return cart.value?.firstOrNull { it.product.id == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun findCartItemQuantityByProductId(productId: Long): Quantity {
        return cart.value?.firstOrNull { it.product.id == productId }?.quantity
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun setError(
        dataError: DataError,
        errorScope: ProductContentError,
    ) {
        if (dataError is ShowError) {
            mutableDataError.setValue(dataError)
        } else {
            _errorScope.setValue(errorScope)
        }
    }

    companion object {
        private const val DEFAULT_CART_ITEMS_COUNT = 0
        private const val PAGE_SIZE = 20
        private const val NO_CART_STATE = 0
    }
}
