package woowacourse.shopping.feature.main

import com.example.domain.model.CartProducts
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel
import java.time.LocalDateTime

class MainPresenter(
    private val view: MainContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository
) : MainContract.Presenter {

    private var totalCount: Int = 0
    private var page = 1
    private var cartProducts: CartProducts = CartProducts(listOf())

    override fun loadProducts() {
        cartRepository.getAll(
            onSuccess = {
                cartProducts = it
                productRepository.getProducts(
                    page = page,
                    onSuccess = {
                        val productItems = matchCartProductCount(it)
                        view.addProducts(productItems)
                        ++page
                    },
                    onFailure = { view.showFailureMessage(it.message) }
                )
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }

    private fun matchCartProductCount(products: List<Product>): List<ProductUiModel> {
        return products.map { product ->
            val count = cartProducts.findByProductId(productId = product.id)?.count ?: 0
            product.toPresentation(count)
        }
    }

    override fun loadRecent() {
        val recentIds = recentProductRepository.getAll()
        val recentProducts = mutableListOf<RecentProductUiModel>()
        recentIds.forEach { recentId ->
            productRepository.getProductById(recentId)?.let {
                recentProducts.add(
                    RecentProductUiModel(it.toPresentation())
                )
            }
        }
        view.updateRecent(recentProducts)
    }

    override fun setCartProductCount() {
        totalCount = cartProducts.size
        view.updateCartProductCount(totalCount)
    }

    override fun moveToCart() {
        view.showCartScreen()
    }

    override fun moveToDetail(product: ProductUiModel) {
        addRecentProduct(RecentProduct(product.toDomain(), LocalDateTime.now()))
        loadRecent()

        val recentId = recentProductRepository.getMostRecentProduct()

        view.showProductDetailScreenByProduct(
            product,
            recentId?.let { productRepository.getProductById(recentId) }?.toPresentation()
        )
    }

    private fun addRecentProduct(recentProduct: RecentProduct) {
        recentProductRepository.addRecentProduct(recentProduct.copy(dateTime = LocalDateTime.now()))
    }

    override fun refresh() {
        productRepository.clearCache()
    }

    override fun increaseCartProduct(product: ProductUiModel, previousCount: Int) {
        if (previousCount == 0) {
            cartRepository.addProduct(
                product = product.toDomain(),
                onSuccess = {
                    cartRepository.getAll(
                        onSuccess = {
                            cartProducts = it
                            ++totalCount
                            view.updateCartProductCount(totalCount)
                            view.updateProductCount(product.copy(count = previousCount + 1))
                        },
                        onFailure = { view.showFailureMessage(it.message) }
                    )
                },
                onFailure = { view.showFailureMessage(it.message) }
            )
        } else {
            cartProducts.findByProductId(productId = product.toDomain().id)
                ?.let { cartProduct ->
                    cartRepository.updateProduct(
                        cartItemId = cartProduct.cartProductId.toInt(),
                        count = previousCount + 1,
                        onSuccess = {
                            cartProducts.updateProductCount(cartProduct, previousCount + 1)
                            view.updateProductCount(product.copy(count = previousCount + 1))
                        },
                        onFailure = { view.showFailureMessage(it.message) }
                    )
                }
        }
    }

    override fun decreaseCartProduct(product: ProductUiModel, previousCount: Int) {
        cartProducts.findByProductId(product.toDomain().id)?.let { cartProduct ->
            if (previousCount == 1) {
                cartRepository.deleteProduct(
                    cartItemId = cartProduct.cartProductId.toInt(),
                    onSuccess = {
                        cartProducts.delete(cartProduct)
                        --totalCount
                        view.updateCartProductCount(totalCount)
                        view.updateProductCount(product.copy(count = 0))
                    },
                    onFailure = { view.showFailureMessage(it.message) }
                )
            } else {
                cartRepository.updateProduct(
                    cartItemId = cartProduct.cartProductId.toInt(),
                    count = previousCount - 1,
                    onSuccess = {
                        cartProducts.updateProductCount(cartProduct, previousCount - 1)
                        view.updateProductCount(product.copy(count = previousCount - 1))
                    },
                    onFailure = { view.showFailureMessage(it.message) }
                )
            }
        }
    }

    override fun updateProducts() {
        cartRepository.getAll(
            onSuccess = {
                cartProducts = it
                val products = cartProducts.data.map {
                    it.product.toPresentation(count = it.count)
                }
                totalCount = products.size

                view.updateProductsCount(products)
                view.updateCartProductCount(products.size)
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }
}
