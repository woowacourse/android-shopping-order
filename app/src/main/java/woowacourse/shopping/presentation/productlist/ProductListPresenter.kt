package woowacourse.shopping.presentation.productlist

import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.UnCheckableCartProductModel

class ProductListPresenter(
    private val view: ProductListContract.View,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ProductListContract.Presenter {

    private var lastProductId: Long = 0

    override fun loadProducts() {
        loadProductsWithSize(PRODUCTS_SIZE) { cartProductModels, isLast ->
            view.setProductModels(cartProductModels, isLast)
            loadCartCount()
        }
    }

    private fun loadCartCount() {
        cartRepository.getCartProducts() {
            val cartCount = it.sumOf { it.quantity }
            view.setCartCount(cartCount)
        }
    }

    private fun loadProductsWithSize(
        pageItemCount: Int,
        callback: (List<CartProductModel>, Boolean) -> Unit,
    ) {
        cartRepository.getProductsByRange(
            lastProductId,
            pageItemCount,
        ) { cartProductModels, isLast ->
            if (cartProductModels.isNotEmpty()) {
                lastProductId = cartProductModels.last().productModel.id
            }
            callback(cartProductModels, isLast)
            view.stopLoading()
        }
    }

    override fun loadRecentProducts() {
        getRecentProductModels { recentProductModels ->
            if (recentProductModels.isNotEmpty()) {
                view.setRecentProductModels(recentProductModels)
            }
        }
    }

    override fun saveRecentProductId(productModel: ProductModel) {
        recentProductRepository.addRecentProduct(productModel.toDomain()) {}
    }

    override fun addCartProductCount(cartProductModel: CartProductModel) {
        val nextCount = cartProductModel.count + COUNT_UNIT
        if (cartProductModel.cartId == 0L) {
            insertCartProduct(cartProductModel)
            return
        }
        updateCartProduct(cartProductModel, nextCount)
    }

    private fun insertCartProduct(cartProductModel: CartProductModel) {
        cartRepository.insertCartProduct(cartProductModel.productModel.id, COUNT_UNIT) { cartId ->
            view.replaceProductModel(
                UnCheckableCartProductModel(
                    cartId,
                    cartProductModel.productModel,
                    COUNT_UNIT,
                ),
            )
            loadCartCount()
        }
    }

    override fun subCartProductCount(cartProductModel: CartProductModel) {
        val nextCount = cartProductModel.count - COUNT_UNIT
        updateCartProduct(cartProductModel, nextCount)
    }

    private fun updateCartProduct(
        cartProductModel: CartProductModel,
        nextCount: Int,
    ) {
        cartRepository.updateCartProductCount(cartProductModel.cartId, nextCount) {
            view.replaceProductModel(
                UnCheckableCartProductModel(
                    cartProductModel.cartId,
                    cartProductModel.productModel,
                    nextCount,
                ),
            )
            loadCartCount()
        }
    }

    private fun getRecentProductModels(callback: (List<ProductModel>) -> Unit) {
        recentProductRepository.getRecentProductsBySize(RECENT_PRODUCTS_SIZE) {
            callback(it.toPresentation())
        }
    }

    private fun List<Product>.toPresentation() = this.map { it.toPresentation() }

    companion object {
        private const val PRODUCTS_SIZE = 20
        private const val RECENT_PRODUCTS_SIZE = 10
        private const val COUNT_UNIT = 1
    }
}
