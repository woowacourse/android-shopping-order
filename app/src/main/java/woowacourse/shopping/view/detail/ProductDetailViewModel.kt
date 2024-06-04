package woowacourse.shopping.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.model.CartItem.Companion.DEFAULT_CART_ITEM_ID
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.DEFAULT_PRODUCT_ID
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentlyProductRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.utils.livedata.MutableSingleLiveData
import woowacourse.shopping.utils.livedata.SingleLiveData
import woowacourse.shopping.view.BaseViewModel
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.model.event.ErrorEvent

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val recentlyProductRepository: RecentlyProductRepository,
) : BaseViewModel(), OnClickCartItemCounter, OnClickDetail {
    private val _product: MutableLiveData<Product> = MutableLiveData(Product.defaultProduct)
    val product: LiveData<Product> get() = _product
    private var cartItemId: Long = DEFAULT_CART_ITEM_ID

    private val _recentlyProduct: MutableLiveData<RecentlyProduct> =
        MutableLiveData(RecentlyProduct.defaultRecentlyProduct)
    val recentlyProduct: LiveData<RecentlyProduct> get() = _recentlyProduct

    private val _productDetailEvent = MutableSingleLiveData<ProductDetailEvent>()
    val productDetailEvent: SingleLiveData<ProductDetailEvent> = _productDetailEvent

    fun addShoppingCartItem(product: Product) {
        runCatching {
            checkValidProduct(product)
            when (cartItemId) {
                DEFAULT_CART_ITEM_ID -> {
                    shoppingCartRepository.addCartItem(product)
                }

                else -> {
                    shoppingCartRepository.updateCartItem(
                        product = product,
                        UpdateCartItemType.UPDATE(product.cartItemCounter.itemCount),
                    )
                }
            }
        }
            .onSuccess {
                _productDetailEvent.setValue(
                    ProductDetailEvent.AddShoppingCart.Success(
                        productId = product.id,
                        count = product.cartItemCounter.itemCount,
                    ),
                )
            }
            .onFailure {
                handleException(it)
            }
    }

    fun loadProductItem(productId: Long) {
        val loadItemCounter = loadProductItemCount(productId)
        runCatching {
            productRepository.getProduct(productId).getOrThrow()
        }.onSuccess { product ->
            product.updateItemSelector(true)
            product.updateCartItemCount(loadItemCounter.itemCount)
            loadRecentlyProduct(product)
            _product.value = product
        }
            .onFailure {
                handleException(it)
            }
    }

    private fun loadProductItemCount(productId: Long): CartItemCounter {
        return try {
            val result =
                shoppingCartRepository.getCartItemResultFromProductId(productId = productId)
                    .getOrThrow()
            cartItemId = result.cartItemId
            result.counter
        } catch (e: Exception) {
            handleException(e)
            CartItemCounter()
        }
    }

    private fun increaseItemCounter() {
        product.value?.cartItemCounter?.increase()
        _product.value =
            product.value?.cartItemCounter?.let {
                product.value?.copy(
                    cartItemCounter = it,
                )
            }
    }

    private fun decreaseItemCounter() {
        val productCount = product.value?.cartItemCounter?.itemCount ?: DEFAULT_ITEM_COUNT
        if (productCount > DEFAULT_ITEM_COUNT) {
            product.value?.cartItemCounter?.decrease()
            _product.value =
                product.value?.cartItemCounter?.let {
                    product.value?.copy(
                        cartItemCounter = it,
                    )
                }
        }
    }

    private fun saveRecentlyProduct(product: Product) {
        runCatching {
            recentlyProductRepository.addRecentlyProduct(
                RecentlyProduct(
                    productId = product.id,
                    imageUrl = product.imageUrl,
                    name = product.name,
                    category = product.category,
                ),
            )
        }
            .onFailure {
                handleException(it)
            }
    }

    private fun deletePrevRecentlyProduct(recentlyProductId: Long) {
        recentlyProductRepository.deleteRecentlyProduct(recentlyProductId)
    }

    private fun updateRecentlyProduct(recentlyProduct: RecentlyProduct) {
        runCatching {
            deletePrevRecentlyProduct(recentlyProduct.id)
            productRepository.getProduct(recentlyProduct.productId).getOrThrow()
        }
            .onSuccess { product ->
                val loadItemCounter = loadProductItemCount(recentlyProduct.productId)
                product.updateItemSelector(true)
                product.updateCartItemCount(loadItemCounter.itemCount)
                _product.value = product
                _recentlyProduct.value = RecentlyProduct.defaultRecentlyProduct
                _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
            }
            .onFailure {
                handleException(it)
            }
    }

    private fun loadRecentlyProduct(product: Product) {
        runCatching {
            recentlyProductRepository.getMostRecentlyProduct().getOrThrow()
        }
            .onSuccess { recentlyProduct ->
                _recentlyProduct.value = recentlyProduct
                _productDetailEvent.setValue(ProductDetailEvent.UpdateRecentlyProductItem.Success)
                if (recentlyProduct.productId != product.id) {
                    saveRecentlyProduct(product)
                }
            }
            .onFailure {
                handleException(it)
            }
    }

    private fun checkValidProduct(product: Product) {
        if (product.id == DEFAULT_PRODUCT_ID) throw ErrorEvent.LoadDataEvent()
        if (product.cartItemCounter.itemCount == DEFAULT_ITEM_COUNT) throw ErrorEvent.LoadDataEvent()
    }

    override fun clickIncrease(product: Product) {
        increaseItemCounter()
    }

    override fun clickDecrease(product: Product) {
        decreaseItemCounter()
    }

    override fun clickAddCart(product: Product) {
        addShoppingCartItem(product)
    }

    override fun clickRecently(recentlyProduct: RecentlyProduct) {
        updateRecentlyProduct(recentlyProduct)
    }
}
