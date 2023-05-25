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
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductSize: Int,
    private val productLoadSize: Int,
) : ShoppingContract.Presenter {
    private var productSize: Int = 0

    init {
        loadMoreProduct()
    }

    override fun updateCartChange() {
        productRepository.getProductsInSize(
            0,
            productSize,
            onSuccess = { products ->
                view.updateProducts(products.value.map { it.toView() })
                updateCartAmount()
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

    override fun setUpCartAmount() {
        updateCartAmount()
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

    override fun loadMoreProduct() {
        productRepository.getProductsInSize(
            productSize,
            productLoadSize,
            onSuccess = { loadedProducts ->
                productSize += loadedProducts.value.size
                view.addProducts(loadedProducts.value.map { it.toView() })
            },
            onFailure = { view.notifyLoadFailed() }
        )
    }

    private fun updateCartAmount() {
        val totalAmount = cartRepository.getTotalAmount()
        view.updateCartAmount(totalAmount)
    }

    override fun decreaseCartProductAmount(shoppingProductModel: ShoppingProductModel) {
        var cartProduct = getCartProduct(shoppingProductModel.product)
        cartProduct = cartProduct.decreaseAmount()
        if (cartProduct.quantity > 0) {
            updateCartProduct(cartProduct)
        } else {
            removeFromCart(cartProduct)
        }
        updateShoppingProduct(shoppingProductModel, cartProduct.quantity)
        updateCartAmount()
    }

    private fun removeFromCart(cartProduct: CartProduct) {
        cartRepository.deleteCartProduct(cartProduct)
    }

    override fun increaseCartProductAmount(shoppingProductModel: ShoppingProductModel) {
        cartRepository.findId(
            productId = shoppingProductModel.product.id,
            onSuccess = {
                if (it == null) {
                    addToCart(shoppingProductModel)
                } else {
                    // updateCartProduct(cartProduct)
                    // updateShoppingProduct(shoppingProductModel, cartProduct.quantity)
                    // updateCartAmount()
                }
            },
            onFailure = {}
        )
    }

    private fun getCartProduct(productModel: ProductModel): CartProduct {
        var cartProduct: CartProduct? =
            cartRepository.getCartProductByProduct(productModel.toDomain())
        if (cartProduct == null) {
            cartProduct = CartProduct(
                id = 0,
                quantity = 0,
                isChecked = true,
                product = productModel.toDomain()
            )
        }
        return cartProduct
    }

    private fun updateCartProduct(cartProduct: CartProduct) {
        cartRepository.modifyCartProduct(cartProduct)
    }

    private fun addToCart(shoppingProductModel: ShoppingProductModel) {
        cartRepository.addCartProduct(
            shoppingProductModel.toDomain().product,
            onSuccess = {
                updateShoppingProduct(shoppingProductModel, 1)
                updateCartAmount()
            },
            onFailure = {}
        )
    }

    private fun updateShoppingProduct(
        shoppingProductModel: ShoppingProductModel,
        quantity: Int
    ) {
        val newShoppingProductModel = ShoppingProductModel(
            shoppingProductModel.product,
            quantity
        )
        view.updateShoppingProduct(shoppingProductModel, newShoppingProductModel)
    }
}
