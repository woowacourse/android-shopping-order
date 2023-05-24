package woowacourse.shopping.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.domain.model.Product
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.feature.main.MainContract.View.MainScreenEvent
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class MainPresenter(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : MainContract.Presenter {
    private lateinit var cartProducts: List<CartProductUiModel>

    private val _products: MutableLiveData<List<ProductUiModel>> = MutableLiveData()
    override val products: LiveData<List<ProductUiModel>>
        get() = _products

    private val _recentProducts: MutableLiveData<List<RecentProductUiModel>> = MutableLiveData()
    override val recentProducts: LiveData<List<RecentProductUiModel>>
        get() = _recentProducts

    private val _badgeCount: MutableLiveData<Int> = MutableLiveData()
    override val badgeCount: LiveData<Int>
        get() = _badgeCount

    private val _mainScreenEvent: MutableLiveData<MainScreenEvent> =
        MutableLiveData()
    override val mainScreenEvent: LiveData<MainScreenEvent>
        get() = _mainScreenEvent

    override fun initLoadData() {
        initLoadCarts()
    }

    private fun initLoadCarts() {
        cartRepository.getAll(
            onSuccess = { carts ->
                cartProducts = carts.map { it.copy().toPresentation() }
                initLoadProducts()
            },
            onFailure = {
            },
        )
    }

    override fun initLoadProducts() {
        productRepository.fetchFirstProducts(
            onSuccess = {
                val productUiModels = makeProductUiModels(it)
                _products.postValue(productUiModels)
            },
            onFailure = {},
        )
    }

    override fun loadRecent() {
        val recentProducts = recentProductRepository.getAll().map { it.toPresentation() }
        _recentProducts.value = recentProducts
    }

    override fun changeProductCartCount(productId: Long, count: Int) {
        val cartProduct: CartProductUiModel =
            cartProducts.find { it.productUiModel.id == productId }
                ?: return addFirstProductToCart(productId)

        if (count == 0) return deleteCartProduct(cartProduct.cartId)
        updateCartProductCount(cartProduct.cartId, count)
    }

    private fun addFirstProductToCart(productId: Long) {
        val newCartProducts = cartProducts.map { it.copy() }.toMutableList()
        cartRepository.addCartProduct(
            productId,
            onSuccess = { cartId ->
                val productUiModel =
                    products.value?.find { it.id == productId } ?: return@addCartProduct
                newCartProducts.add(CartProductUiModel(cartId, productUiModel.copy(), true))
                cartProducts = newCartProducts
                productUiModel.count = 1
            },
            onFailure = {
            },
        )
    }

    private fun updateCartProductCount(cartId: Long, count: Int) {
        cartRepository.changeCartProductCount(
            cartId,
            count,
            onSuccess = { cartId ->
                val cartProductUiModel =
                    cartProducts.find { it.cartId == cartId } ?: return@changeCartProductCount
                val productUiModel =
                    _products.value?.find { it.id == cartProductUiModel.productUiModel.id }
                        ?: return@changeCartProductCount

                productUiModel.count = count
            },
            onFailure = {},
        )
    }

    private fun deleteCartProduct(cartId: Long) {
        cartRepository.deleteCartProduct(
            cartId,
            onSuccess = {
                val cartProduct =
                    cartProducts.find { it.cartId == cartId } ?: return@deleteCartProduct
                val newCartProducts = cartProducts.map { it.copy() }.toMutableList()
                newCartProducts.removeIf { it.cartId == cartId }
                cartProducts = newCartProducts

                _products.value?.find { it.id == cartProduct.productUiModel.id }?.count = 0
            },
            onFailure = {},
        )
    }

    override fun loadMoreProduct() {
        val lastProductId = _products.value?.lastOrNull()?.id ?: 0
        productRepository.fetchNextProducts(
            lastProductId,
            onSuccess = {
                val nextProductUiModels = makeProductUiModels(it)
                val alreadyProducts = products.value ?: emptyList()
                _products.postValue(alreadyProducts + nextProductUiModels)
            },
            onFailure = {},
        )
    }

    private fun makeProductUiModels(products: List<Product>): List<ProductUiModel> {
        val productUiModels: List<ProductUiModel> = products.map { product ->
            val findCartProduct = cartProducts.find { it.productUiModel.id == product.id }
            if (findCartProduct == null) {
                return@map product.toPresentation()
            } else {
                return@map product.toPresentation()
                    .apply { count = findCartProduct.productUiModel.count }
            }
        }

        return productUiModels
    }
}
