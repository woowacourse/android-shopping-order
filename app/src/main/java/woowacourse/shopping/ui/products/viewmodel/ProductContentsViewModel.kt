package woowacourse.shopping.ui.products.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.data.cart.Cart
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.product.ProductRepository
import woowacourse.shopping.data.recentproduct.RecentProduct
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductWithQuantity
import woowacourse.shopping.model.Quantity
import woowacourse.shopping.ui.CountButtonClickListener
import woowacourse.shopping.ui.products.ProductItemClickListener
import woowacourse.shopping.ui.products.ProductWithQuantityUiState
import woowacourse.shopping.ui.products.toUiModel
import woowacourse.shopping.ui.utils.AddCartClickListener
import woowacourse.shopping.ui.utils.MutableSingleLiveData
import woowacourse.shopping.ui.utils.SingleLiveData

class ProductContentsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) :
    ViewModel(), CountButtonClickListener, ProductItemClickListener, AddCartClickListener {

    private val products: MutableLiveData<List<Product>> = MutableLiveData()

    private val cart: MutableLiveData<List<Cart>> = MutableLiveData()

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

    private val _recentProducts: MutableLiveData<List<RecentProduct>> = MutableLiveData()
    val recentProducts: LiveData<List<Product>> =
        _recentProducts.map { recentProducts ->
            recentProducts.map { productRepository.find(it.productId) }
        }

    private val _productDetailId = MutableSingleLiveData<Long>()
    val productDetailId: SingleLiveData<Long> get() = _productDetailId

    private fun currentProduct(): ProductWithQuantityUiState =
        productWithQuantity.value ?: ProductWithQuantityUiState.DEFAULT

    init {
        loadProducts()
    }

    fun loadProducts() {
        val handler = Handler(Looper.getMainLooper())
        runCatching {
            productWithQuantity.value = currentProduct().copy(isLoading = true)
            productRepository.getProducts(
                currentProduct().productWithQuantities.size / PAGE_SIZE,
                PAGE_SIZE
            )
        }.onSuccess { loadedProducts ->
            handler.postDelayed({
                products.value = (products.value ?: emptyList()) + loadedProducts
            }, 1000)
        }
    }

    override fun plusCount(productId: Long) {
        cartRepository.patchCartItem(
            findCartItemByProductId(productId),
            findCartItemQuantityByProductId(productId).inc().value,
        )
        loadCartItems()
    }

    override fun minusCount(productId: Long) {
        val currentCount = findCartItemQuantityByProductId(productId).dec().value
        if (currentCount == 0) {
            cartRepository.deleteCartItem(findCartItemByProductId(productId))
        } else {
            cartRepository.patchCartItem(findCartItemByProductId(productId), currentCount)
        }
        loadCartItems()
    }

    override fun itemClickListener(productId: Long) {
        _productDetailId.setValue(productId)
    }

    override fun addCart(productId: Long) {
        cartRepository.postCartItems(productId, 1)
        loadCartItems()
    }

    fun loadCartItems() {
        val handler = Handler(Looper.getMainLooper())
        runCatching {
            productWithQuantity.value = currentProduct().copy(isLoading = true)
            cartRepository.getAllCartItems()
        }.onSuccess { carts ->
            handler.postDelayed({
                cart.value = carts
            }, 1000)
        }
    }

    fun loadRecentProducts() {
        _recentProducts.value = recentProductRepository.findAll()
    }

    private fun updateProductWithQuantity(): ProductWithQuantityUiState {
        val currentProducts = products.value ?: emptyList()
        val updatedList =
            currentProducts.map { product ->
                ProductWithQuantity(product = product, quantity = getQuantity(product.id))
            }

        return currentProduct().copy(
            productWithQuantities = updatedList.map { it.toUiModel() },
            isLoading = false
        )
    }

    private fun getQuantity(productId: Long): Quantity {
        val cart = findCartContainProduct(productId) ?: return Quantity()
        return cart.quantity
    }

    private fun findCartContainProduct(productId: Long): Cart? {
        cart.value?.let { items ->
            return items.find { it.productId == productId }
        }
        return null
    }

    private fun findCartItemByProductId(productId: Long): Long {
        return cart.value?.firstOrNull { it.productId == productId }?.id
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    private fun findCartItemQuantityByProductId(productId: Long): Quantity {
        return cart.value?.firstOrNull { it.productId == productId }?.quantity
            ?: error("일치하는 장바구니 아이템이 없습니다.")
    }

    companion object {
        private const val DEFAULT_CART_ITEMS_COUNT = 0
        private const val PAGE_SIZE = 20
    }
}
