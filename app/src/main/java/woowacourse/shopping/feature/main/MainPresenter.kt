package woowacourse.shopping.feature.main

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

    override fun loadProducts() {
        productRepository.getProducts(
            page = page,
            onSuccess = {
                val productItems = matchCartProductCount(it)
                view.addProducts(productItems)
                ++page
            },
            onFailure = {}
        )
    }

    private fun matchCartProductCount(products: List<Product>): List<ProductUiModel> {
        val cartProducts = cartRepository.getAll()
        return products.map { product ->
            val count = cartProducts.find { it.product.id == product.id }?.count ?: 0
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
        val count = cartRepository.getAll().size
        view.updateCartProductCount(count)
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
        cartRepository.addProduct(product.toDomain(), previousCount + 1)
        totalCount = cartRepository.getAll().size
        view.updateCartProductCount(totalCount)
        view.updateProductCount(product.copy(count = previousCount + 1))
    }

    override fun decreaseCartProduct(product: ProductUiModel, previousCount: Int) {
        if (previousCount == 1) {
            cartRepository.deleteProduct(product.toDomain())
            totalCount = cartRepository.getAll().size
            view.updateCartProductCount(totalCount)
        } else {
            cartRepository.addProduct(product.toDomain(), previousCount - 1)
        }
        view.updateProductCount(product.copy(count = previousCount + 1))
    }

    override fun updateProducts() {
        val products = cartRepository.getAll().map {
            it.product.toPresentation(count = it.count)
        }
        view.updateProductsCount(products)
        view.updateCartProductCount(products.size)
    }

    override fun loadPointInfo() {
        view.createCheckPointDialog(100, 20)
    }
}
