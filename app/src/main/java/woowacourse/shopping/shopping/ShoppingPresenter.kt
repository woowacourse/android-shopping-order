package woowacourse.shopping.shopping

import woowacourse.shopping.common.model.ProductModel
import woowacourse.shopping.common.model.ShoppingProductModel
import woowacourse.shopping.common.model.mapper.ProductMapper.toDomain
import woowacourse.shopping.common.model.mapper.ProductMapper.toView
import woowacourse.shopping.common.model.mapper.RecentProductMapper.toView
import woowacourse.shopping.common.model.mapper.ShoppingProductMapper.toDomain
import woowacourse.shopping.common.model.mapper.ShoppingProductMapper.toView
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.ShoppingProduct
import woowacourse.shopping.domain.ShoppingProducts
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import kotlin.math.min

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductSize: Int,
    private val productLoadSize: Int,
) : ShoppingContract.Presenter {
    private var productSize: Int = 0
    private lateinit var shoppingProducts: ShoppingProducts

    init {
        productSize += productLoadSize
        loadProducts()
    }

    override fun loadProducts() {
        productRepository.getProducts(
            onSuccess = { products ->
                productSize = minOf(productSize, products.value.size)
                shoppingProducts = products
                view.updateProducts(shoppingProducts.value.map { it.toView() })
                setCartQuantity()
            },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    override fun updateRecentProducts() {
        recentProductRepository.getAll(
            onSuccess = { recentProducts ->
                val recentProductsInSize = recentProducts.getRecentProducts(recentProductSize)
                view.updateRecentProducts(recentProductsInSize.value.map { it.toView() })
            },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    override fun setCartQuantity() {
        updateCartQuantity()
    }

    override fun openProduct(productModel: ProductModel) {
        recentProductRepository.getLatestRecentProduct(
            onSuccess = {
                updateRecentProducts(productModel)

                if (productModel.toDomain().isLatestRecentProduct(it)) {
                    view.showProductDetail(productModel, null)
                } else {
                    view.showProductDetail(productModel, it?.product?.toView())
                }
            },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    private fun Product.isLatestRecentProduct(latestRecentProduct: RecentProduct?) =
        latestRecentProduct?.product == this

    private fun updateRecentProducts(productModel: ProductModel) {
        val recentProduct = RecentProduct(LocalDateTime.now(), productModel.toDomain())
        val isExist = recentProductRepository.isExist(productModel.id)

        if (isExist) {
            updateRecentProduct(recentProduct)
        } else {
            addRecentProduct(recentProduct)
        }
    }

    private fun addRecentProduct(recentProduct: RecentProduct) {
        recentProductRepository.addRecentProduct(recentProduct)
    }

    private fun updateRecentProduct(recentProduct: RecentProduct) {
        recentProductRepository.updateRecentProduct(recentProduct)
    }

    override fun openCart() {
        view.showCart()
    }

    override fun loadMoreProducts() {
        val toIndex = min(productSize + productLoadSize, shoppingProducts.value.size)
        val products = shoppingProducts.value.subList(productSize, toIndex)
        productSize += products.size
        view.addProducts(products.map { it.toView() })
    }

    private fun updateCartQuantity() {
        view.updateCartQuantity(shoppingProducts.totalQuantity)
    }

    override fun decreaseCartProductAmount(shoppingProductModel: ShoppingProductModel) {
        cartRepository.findByProductId(
            productId = shoppingProductModel.product.id,
            onSuccess = {
                if (it != null) {
                    val cartProduct = CartProduct(
                        id = it.id,
                        quantity = shoppingProductModel.amount - 1,
                        isChecked = true,
                        product = shoppingProductModel.product.toDomain()
                    )
                    updateCartProductQuantity(cartProduct)
                }
            },
            onFailure = {}
        )
        // var cartProduct = getCartProduct(shoppingProductModel.product)
        // cartProduct = cartProduct.decreaseAmount()
        // if (cartProduct.quantity > 0) {
        //     updateCartProductQuantity(cartProduct)
        // } else {
        //     removeFromCart(cartProduct)
        // }
        // updateShoppingProduct(shoppingProductModel, cartProduct.quantity)
        // updateCartAmount()
    }

    override fun increaseCartProductAmount(shoppingProductModel: ShoppingProductModel) {
        cartRepository.findByProductId(
            productId = shoppingProductModel.product.id,
            onSuccess = {
                if (it == null) {
                    addToCart(shoppingProductModel)
                } else {
                    val cartProduct = CartProduct(
                        id = it.id,
                        quantity = shoppingProductModel.amount + 1,
                        isChecked = true,
                        product = shoppingProductModel.product.toDomain()
                    )
                    updateCartProductQuantity(cartProduct)
                }
            },
            onFailure = {}
        )
    }

    private fun updateCartProductQuantity(cartProduct: CartProduct) {
        if (cartProduct.quantity > 0) {
            cartRepository.updateCartProductQuantity(
                cartProduct = cartProduct,
                onSuccess = {
                    val shoppingProduct = ShoppingProduct(cartProduct.product, cartProduct.quantity)
                    updateShoppingProductQuantity(shoppingProduct)
                },
                onFailure = {}
            )
        } else {
            removeFromCart(cartProduct)
        }
    }

    private fun removeFromCart(cartProduct: CartProduct) {
        cartRepository.deleteCartProduct(
            cartProduct = cartProduct,
            onSuccess = {
                val shoppingProduct = ShoppingProduct(cartProduct.product, cartProduct.quantity)
                updateShoppingProductQuantity(shoppingProduct)
            },
            onFailure = {}
        )
    }

    private fun updateShoppingProductQuantity(shoppingProduct: ShoppingProduct) {
        shoppingProducts = shoppingProducts.replaceShoppingProduct(shoppingProduct)
        view.updateShoppingProduct(shoppingProduct.toView())
        updateCartQuantity()
    }

    private fun addToCart(shoppingProductModel: ShoppingProductModel) {
        cartRepository.addCartProduct(
            shoppingProductModel.toDomain().product,
            onSuccess = {
                val shoppingProduct = ShoppingProduct(shoppingProductModel.product.toDomain(), 1)
                shoppingProducts = shoppingProducts.replaceShoppingProduct(shoppingProduct)
                view.updateShoppingProduct(shoppingProduct.toView())
                updateCartQuantity()
            },
            onFailure = {}
        )
    }
}
