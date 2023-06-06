package woowacourse.shopping.ui.shopping.contract.presenter

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.shopping.ProductItemModel
import woowacourse.shopping.ui.shopping.ProductReadMore
import woowacourse.shopping.ui.shopping.ProductsItemType
import woowacourse.shopping.ui.shopping.RecentProductsItem
import woowacourse.shopping.ui.shopping.contract.ShoppingContract
import woowacourse.shopping.ui.shopping.toUIModel

class ShoppingPresenter(
    private val view: ShoppingContract.View,
    private val productRepository: ProductRepository,
    private val recentRepository: RecentRepository,
    private val cartRepository: CartRepository,
    offset: Int = 0,
) : ShoppingContract.Presenter {
    private var productsData: MutableList<ProductsItemType> = mutableListOf()
    private var productSize: Int = 0
    private var productOffset = ProductsOffset(offset, productRepository)

    override fun initProducts() {
        productRepository.getMoreProducts(PRODUCT_COUNT, productOffset.getOffset()) { products ->
            productSize += products.size
            productsData.removeIf { it is ProductItemModel }
            productsData += products.map { it.toUIModel() }
            view.setProducts(productsData.plus(ProductReadMore))
        }
    }

    override fun updateProducts() {
        val recentProductsData = RecentProductsItem(
            recentRepository.getRecent(RECENT_PRODUCT_COUNT).map { it.toUIModel() },
        )
        when {
            productsData.isEmpty() && recentProductsData.product.isNotEmpty() -> productsData.add(
                recentProductsData,
            )

            productsData.isNotEmpty() -> {
                updateProductsDataWithRecentData(recentProductsData)
            }
        }
        view.setProducts(productsData.plus(ProductReadMore))
    }

    private fun updateProductsDataWithRecentData(recentProductsData: RecentProductsItem) {
        when {
            recentProductsData.product.isEmpty() -> productsData.removeIf { it is RecentProductsItem }
            productsData[0] is RecentProductsItem -> productsData[0] = recentProductsData
            else -> productsData.add(0, recentProductsData)
        }
    }

    override fun fetchMoreProducts() {
        productOffset = productOffset.plus(1)
        productRepository.getMoreProducts(PRODUCT_COUNT, productOffset.getOffset()) { products ->
            productSize += products.size
            productsData.removeIf { it is ProductItemModel }
            productsData += products.map { it.toUIModel() }
            view.addProducts(productsData.plus(ProductReadMore))
        }
    }

    override fun navigateToItemDetail(id: Long) {
        val latestProduct = recentRepository.getRecent(1).firstOrNull()?.toUIModel()
        view.navigateToProductDetail(id, latestProduct)
    }

    override fun insertItem(id: Long, count: Int) {
        cartRepository.insert(id, count) {
            view.updateItem(id, count)
            updateCountSize()
        }
    }

    override fun increaseCount(id: Long) {
        cartRepository.findByProductId(id) {
            if (it == null) {
                insertItem(id, 1)
                return@findByProductId
            }
            updateCartProductQuantity(it, it.quantity + 1)
        }
    }

    override fun decreaseCount(id: Long) {
        cartRepository.findByProductId(id) {
            if (it != null) {
                updateCartProductQuantity(it, it.quantity - 1)
            }
        }
    }

    override fun updateCountSize() {
        cartRepository.getAllProductInCart { cartProducts ->
            view.showCountSize(cartProducts.size)
        }
    }

    private fun updateCartProductQuantity(cartProduct: CartProduct, count: Int) {
        cartRepository.updateCount(cartProduct.id, count) {
            view.updateItem(cartProduct.product.id, count)
        }
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PRODUCT_COUNT = 20
    }
}
