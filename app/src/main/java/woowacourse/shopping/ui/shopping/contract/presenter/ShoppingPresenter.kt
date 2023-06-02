package woowacourse.shopping.ui.shopping.contract.presenter

import android.util.Log
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.ui.shopping.ProductItem
import woowacourse.shopping.ui.shopping.ProductReadMore
import woowacourse.shopping.ui.shopping.ProductsItemType
import woowacourse.shopping.ui.shopping.RecentProductsItem
import woowacourse.shopping.ui.shopping.contract.ShoppingContract

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
    private val cachedCartProducts = mutableListOf<CartProduct>()

    override fun initProducts() {
        cartRepository.getAllProductInCart(onSuccess = { cartProducts ->
            cachedCartProducts.clear()
            cachedCartProducts.addAll(cartProducts)
            productRepository.getMoreProducts(
                productOffset.getOffset(),
                PRODUCT_COUNT,
                onSuccess = { products ->
                    productSize += products.size
                    productsData.removeIf { it is ProductItem }
                    productsData += products.map { product ->
                        ProductItem(
                            product.toUIModel(),
                            cachedCartProducts.find { it.product.id == product.id }?.quantity ?: 0,
                        )
                    }
                    view.setProducts(productsData.plus(ProductReadMore))
                },
            )
        }, {})
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
        productOffset = productOffset.plus(PRODUCT_COUNT)
        productRepository.getMoreProducts(
            productOffset.getOffset(),
            PRODUCT_COUNT,
            onSuccess = { products ->
                productSize += products.size
                productsData += products.map { product ->
                    ProductItem(
                        product.toUIModel(),
                        cachedCartProducts.find { it.product.id == product.id }?.quantity ?: 0,
                    )
                }
                view.addProducts(productsData.plus(ProductReadMore))
            },
        )
    }

    override fun navigateToItemDetail(id: Long) {
        val latestProduct = recentRepository.getRecent(1).firstOrNull()?.toUIModel()
        view.navigateToProductDetail(id, latestProduct)
    }

    override fun insertItem(id: Long, count: Int) {
        cartRepository.insert(id, onSuccess = {
            Log.d("insert", "insert")
            view.updateItem(id, count)
            updateCountSize()
        }, onFailure = {})
    }

    override fun increaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let { cartRepository.updateCount(it, getCount(id) + 1, {}, {}) }
        cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) + 1
        view.updateItem(id, getCount(id))
    }

    override fun decreaseCount(id: Long) {
        val cartProduct = cachedCartProducts.find { it.product.id == id }
        cartProduct?.id?.let { cartRepository.updateCount(it, getCount(id) - 1, {}, {}) }
        cachedCartProducts.find { it.product.id == id }?.quantity = getCount(id) - 1
        view.updateItem(id, getCount(id))
    }

    override fun updateCountSize() {
        cartRepository.getAllProductInCart(onSuccess = { cartProducts ->
            cachedCartProducts.clear()
            cachedCartProducts.addAll(cartProducts)
            view.showCountSize(cachedCartProducts.size)
        }, onFailure = {})
    }

    private fun getCount(id: Long): Int {
        return cachedCartProducts.find { it.product.id == id }?.quantity ?: 0
    }

    companion object {
        private const val RECENT_PRODUCT_COUNT = 10
        private const val PRODUCT_COUNT = 20
    }
}
