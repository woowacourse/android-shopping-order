package woowacourse.shopping.ui.products.viewmodel

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
import java.util.Timer
import java.util.TimerTask

class ProductContentsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) :
    ViewModel(), CountButtonClickListener, ProductItemClickListener, AddCartClickListener {
    private val items = mutableListOf<Product>()
    private val products: MutableLiveData<List<Product>> = MutableLiveData()
    private val cart: MutableLiveData<List<Cart>> = MutableLiveData()

    val productWithQuantity: MediatorLiveData<ProductWithQuantityUiState> = MediatorLiveData()

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
            recentProducts.map { productRepository.find(it.productId).getOrThrow() }
        }

    private val _productDetailId = MutableSingleLiveData<Long>()
    val productDetailId: SingleLiveData<Long> get() = _productDetailId

    private val _error: MutableSingleLiveData<Throwable> = MutableSingleLiveData()
    val error: SingleLiveData<Throwable> = _error

    private var currentOffset = 0

    init {
        productWithQuantity.addSource(products) { updateProductWithQuantity() }
        productWithQuantity.addSource(cart) { updateProductWithQuantity() }
        loadProducts()
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

    fun loadProducts() {
        productRepository.getProducts(currentOffset++, 20).onSuccess {
            items.addAll(it)
            this.products.value = items
            Timer().schedule(
                object : TimerTask() {
                    override fun run() {
                    }
                },
                1000,
            )
        }.onFailure {
            _error.setValue(it)
        }
    }

    fun loadCartItems() {
        cartRepository.getAllCartItems().onSuccess {
            cart.value = it
        }.onFailure {
            _error.setValue(it)
        }
    }

    fun loadRecentProducts() {
        recentProductRepository.findAll().onSuccess {
            _recentProducts.value = it
        }
    }

    private fun updateProductWithQuantity() {
        val currentProducts = products.value ?: emptyList()
        val updatedList =
            currentProducts.map { product ->
                ProductWithQuantity(product = product, quantity = getQuantity(product.id))
            }
        productWithQuantity.value =
            ProductWithQuantityUiState(updatedList.map { it.toUiModel() }, isLoading = true)
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
    }
}
