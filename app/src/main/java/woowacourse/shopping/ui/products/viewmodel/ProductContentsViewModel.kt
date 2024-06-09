package woowacourse.shopping.ui.products.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.local.room.recentproduct.RecentProduct
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithQuantity
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.onSuccess
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.toUiModel
import woowacourse.shopping.ui.products.uimodel.ProductListError
import woowacourse.shopping.ui.products.uimodel.ProductItemClickListener
import woowacourse.shopping.ui.products.uimodel.ProductWithQuantityUiState
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData
import woowacourse.shopping.ui.utils.viewModelLaunch

class ProductContentsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) :
    ViewModel(), CountButtonClickListener, ProductItemClickListener, AddCartClickListener {
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

    private val _error: MutableSingleLiveData<ProductListError> = MutableSingleLiveData()
    val error: SingleLiveData<ProductListError> get() = _error

    private fun currentProduct(): ProductWithQuantityUiState =
        productWithQuantity.value ?: ProductWithQuantityUiState.DEFAULT

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelLaunch(::productExceptionHandler) {
            productWithQuantity.value = currentProduct().copy(isLoading = true)

            productRepository.allProductsResponse(
                currentProduct().productWithQuantities.size / PAGE_SIZE,
                PAGE_SIZE,
            ).onSuccess { loadedProducts ->
                products.value = (products.value ?: emptyList()) + loadedProducts
            }.checkError {
                _error.setValue(it)
            }
        }
    }

    override fun plusCount(productId: Long) {
        viewModelLaunch(::updateCountExceptionHandler) {
            cartRepository.patchCartItem(
                findCartItemByProductId(productId),
                findCartItemQuantityByProductId(productId).inc().value,
            ).onSuccess {
                loadCartItems()
            }.checkError {
                _error.setValue(it)
            }

        }
    }

    override fun minusCount(productId: Long) {
        viewModelLaunch(::updateCountExceptionHandler) {
            val currentCount = findCartItemQuantityByProductId(productId).dec().value
            if (currentCount == NO_CART_STATE) {
                cartRepository.deleteCartItem(findCartItemByProductId(productId))
                    .checkError { _error.setValue(it) }.onSuccess { loadCartItems() }
            } else {
                cartRepository.patchCartItem(findCartItemByProductId(productId), currentCount)
                    .checkError { _error.setValue(it) }.onSuccess { loadCartItems() }
            }
        }

    }

    override fun itemClickListener(productId: Long) {
        _productDetailId.setValue(productId)
    }

    override fun addCart(productId: Long) {
        viewModelLaunch(::addCartExceptionHandler) {
            cartRepository.postCartItems(productId, 1).onSuccess {
                loadCartItems()
            }.checkError { _error.setValue(it) }
        }
    }

    fun loadCartItems() {
        viewModelLaunch(::cartExceptionHandler) {
            productWithQuantity.value = currentProduct().copy(isLoading = true)
            cartRepository.allCartItemsResponse().onSuccess { carts ->
                cart.value = carts
            }.checkError {
                _error.setValue(it)
            }
        }
    }

    fun loadRecentProducts() {
        viewModelLaunch(::recentExceptionHandler) {
            recentProductRepository.allRecentProductsResponse().onSuccess { recentProducts ->
                _recentProducts.value =
                    recentProducts.mapNotNull { productRepository.productByIdOrNull(it.productId) }
            }.checkError {
                _error.setValue(it)
            }
        }
    }

    private fun updateProductWithQuantity(): ProductWithQuantityUiState {

        val currentProducts = products.value ?: emptyList()
        val updatedList = currentProducts.map { product ->
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

    private fun addCartExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductListError.AddCart)
    }

    private fun updateCountExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductListError.UpdateCount)
    }

    private fun productExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductListError.LoadProduct)
    }

    private fun recentExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductListError.LoadProduct)
    }

    private fun cartExceptionHandler(throwable: Throwable) {
        _error.setValue(ProductListError.LoadProduct)
    }

    private inline fun <reified T : Any?> Result<T>.checkError(excute: (ProductListError) -> Unit) = apply {
        when (this) {
            is Result.Success -> {}
            is Fail.InvalidAuthorized -> excute(ProductListError.InvalidAuthorized)
            is Fail.Network -> excute(ProductListError.Network)
            is Fail.NotFound -> {
                when (T::class) {
                    Product::class -> excute(ProductListError.LoadProduct)
                    RecentProduct::class -> excute(ProductListError.LoadRecentProduct)
                    else -> excute(ProductListError.UnKnown)
                }
            }

            is Result.Exception -> {
                Log.d(this.javaClass.simpleName, "${this.e}")
                excute(ProductListError.UnKnown)
            }
        }
    }

    companion object {
        private const val DEFAULT_CART_ITEMS_COUNT = 0
        private const val PAGE_SIZE = 20
        private const val NO_CART_STATE = 0
    }
}
