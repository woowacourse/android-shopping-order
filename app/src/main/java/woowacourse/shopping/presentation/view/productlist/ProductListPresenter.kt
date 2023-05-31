package woowacourse.shopping.presentation.view.productlist

import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.model.RecentProductModel.Companion.errorData
import woowacouse.shopping.data.repository.cart.CartRepository
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
    private var cartProducts = CartProductsModel(listOf())

    private val recentProducts = mutableListOf<RecentProductModel>()
    private var lastScroll = 0
    private var productsStartIndex = 0

    override fun initRecentProductItems() {
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN))
        recentProductRepository.deleteNotTodayRecentProducts(today)
    }

    override fun initProductItems() {
        productRepository.loadDatas(::onFailure) { remoteProducts ->
            cartRepository.loadAllCarts(::onFailure) { remoteCarts ->
                val remoteCartProducts = remoteCarts.map { it.toUIModel() }
                cartProducts = CartProductsModel(
                    remoteProducts.map { it.toUIModel() }
                        .updateCartProductsInfo(remoteCartProducts)
                )

                val allCount = cartProducts.toModel().totalCount

                val displayProducts =
                    cartProducts.toModel().getDisplayList(productsStartIndex, DISPLAY_PRODUCT_COUNT)
                        .map { it.toUIModel() }

                view.setProductItemsView(displayProducts)
                loadRecentProductItems()
                view.setLayoutVisibility()
                updateCartCount(allCount)
            }
        }
    }

    private fun List<ProductModel>.updateCartProductsInfo(
        cartProducts: List<CartModel>
    ): List<CartModel> {
        return map { product ->
            cartProducts.find { it.product.id == product.id }
                ?: CartModel.getNoHasCountCartProduct(product)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }

    override fun loadCartItems() {
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val cartIdsCount = carts.associate { it.id to it.count }
            cartProducts =
                cartProducts.toModel().updateCartCountByCartIds(cartIdsCount).toUIModel()

            val allCount = cartProducts.toModel().totalCount

            val displayProducts =
                cartProducts.toModel().getDisplayList(productsStartIndex, DISPLAY_PRODUCT_COUNT)
                    .map { it.toUIModel() }

            view.setProductItemsView(displayProducts)
            updateCartCount(allCount)
        }
    }

    private fun updateCartCount(allCount: Int) {
        view.updateToolbarCartCountView(allCount)
        updateVisibilityCartCount(allCount)
    }

    override fun updateProductItems(startIndex: Int) {
        productsStartIndex = startIndex
        val displayProducts =
            cartProducts.toModel().getDisplayList(productsStartIndex, DISPLAY_PRODUCT_COUNT)
                .map { it.toUIModel() }
        view.setProductItemsView(displayProducts)
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
        val targetCartProduct = cartProducts.toModel().getCartByProductId(productId) ?: return
        if (targetCartProduct.count == 0) {
            cartRepository.addCartProduct(productId, ::onFailure) { cartId ->
                cartProducts = cartProducts.toModel()
                    .updateCartCountByCartId(targetCartProduct.id, count)
                    .toUIModel()

                cartRepository.addLocalCart(cartId)
                cartProducts = cartProducts.toModel().updateCartId(cartId, productId).toUIModel()

                val allCount = cartProducts.toModel().totalCount
                view.updateToolbarCartCountView(allCount)
                updateVisibilityCartCount(allCount)
            }
            return
        }

        cartRepository.loadAllCarts(::onFailure) { carts ->
            val remoteCartProduct = carts.find { it.product.id == productId } ?: return@loadAllCarts
            cartRepository.addLocalCart(remoteCartProduct.id)

            val newCartProduct = remoteCartProduct.copy(count = count)

            cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                if (count == 0) {
                    cartRepository.deleteLocalCart(newCartProduct.id)
                }

                cartProducts =
                    cartProducts.toModel().updateCartCountByCartId(remoteCartProduct.id, count)
                        .toUIModel()

                val allCount = cartProducts.toModel().totalCount

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
