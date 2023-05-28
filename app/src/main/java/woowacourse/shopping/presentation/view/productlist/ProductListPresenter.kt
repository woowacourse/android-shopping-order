package woowacourse.shopping.presentation.view.productlist

import woowacourse.shopping.data.respository.cart.CartRepository
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.model.RecentProductModel.Companion.errorData
import woowacouse.shopping.data.repository.product.ProductRepository
import woowacouse.shopping.data.repository.recentproduct.RecentProductRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductListPresenter(
    private val view: ProductContract.View,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ProductContract.Presenter {
    private val products = mutableListOf<ProductModel>()
    private val recentProducts = mutableListOf<RecentProductModel>()
    private var lastScroll = 0
    private var productsStartIndex = 0

    override fun initRecentProductItems() {
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN))
        recentProductRepository.deleteNotTodayRecentProducts(today)
    }

    override fun initProductItems() {
        productRepository.loadDatas(::onFailure) {
            val allProducts = it.map { product -> product.toUIModel() }
            products.addAll(allProducts)

            loadCartItems()
            loadRecentProductItems()
            view.setLayoutVisibility()
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }

    override fun loadCartItems() {
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val newProducts = products.map { product ->
                product.copy(
                    count = carts.find { cart -> cart.product.id == product.id }?.quantity ?: 0,
                )
            }

            products.clear()
            products.addAll(newProducts)

            val allCount = newProducts.sumOf { it.count }

            view.setProductItemsView(products.subList(0, getSubToIndex()).toList())
            view.updateToolbarCartCountView(allCount)
            updateVisibilityCartCount(allCount)
        }
    }

    override fun updateProductItems(startIndex: Int) {
        productsStartIndex = startIndex
        view.setProductItemsView(products.subList(0, getSubToIndex()).toList())
    }

    private fun getSubToIndex(): Int {
        return if (products.size > productsStartIndex + DISPLAY_PRODUCT_COUNT) {
            productsStartIndex + DISPLAY_PRODUCT_COUNT
        } else {
            products.size
        }
    }

    override fun loadRecentProductItems() {
        recentProductRepository.getRecentProducts(LOAD_RECENT_PRODUCT_COUNT) {
            recentProducts.clear()
            recentProducts.addAll(it.toUIModel().recentProducts)
            view.setRecentProductItemsView(recentProducts.toList())
        }
    }

    override fun saveRecentProduct(productId: Long) {
        recentProductRepository.addCart(productId)
    }

    override fun actionOptionItem() {
        view.moveToCartView()
    }

    override fun getLastRecentProductItem(lastRecentIndex: Int): RecentProductModel {
        if (recentProducts.isEmpty())
            return errorData
        return recentProducts[lastRecentIndex]
    }

    override fun getRecentProductsLastScroll(): Int = lastScroll

    override fun updateRecentProductsLastScroll(lastScroll: Int) {
        this.lastScroll = lastScroll
    }

    override fun updateCount(productId: Long, count: Int) {
        val product = products.find { it.id == productId } ?: return
        if (product.count == 0) {
            cartRepository.addCartProduct(productId, ::onFailure) {
                product.count = count

                cartRepository.loadAllCarts(::onFailure) { carts ->
                    val cartProduct =
                        carts.find { it.product.id == product.id } ?: return@loadAllCarts
                    cartRepository.addLocalCart(cartProduct.id)
                }

                val allCount = products.sumOf { it.count }
                view.updateToolbarCartCountView(allCount)
                updateVisibilityCartCount(allCount)
            }
            return
        }
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val cartProduct = (carts.find { it.product.id == productId } ?: return@loadAllCarts)

            cartRepository.addLocalCart(cartProduct.id)

            val newCartProduct = cartProduct.copy(quantity = count)

            cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                if (count == 0) {
                    cartRepository.deleteLocalCart(newCartProduct.id)
                }

                product.count = count
                val allCount = products.sumOf { it.count }

                view.updateToolbarCartCountView(allCount)
                updateVisibilityCartCount(allCount)
            }
        }
    }

    private fun updateVisibilityCartCount(count: Int) {
        if (count == 0) {
            view.setGoneToolbarCartCountView()
            return
        }
        view.setVisibleToolbarCartCountView()
    }

    companion object {
        private const val LOCAL_DATE_PATTERN = "yyyy-MM-dd"
        private const val LOAD_RECENT_PRODUCT_COUNT = 10

        private const val DISPLAY_PRODUCT_COUNT = 20
    }
}
