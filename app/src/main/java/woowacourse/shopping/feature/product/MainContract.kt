package woowacourse.shopping.feature.product

import com.example.domain.cart.CartProduct
import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.ProductState
import woowacourse.shopping.model.RecentProductState

interface MainContract {

    interface View {
        fun setProducts(products: List<Product>)
        fun setRecentProducts(recentProducts: List<RecentProduct>)
        fun setCartProductCountBadge(count: Int)
        fun setCartProductCounts(cartProducts: List<CartProduct>)
        fun addProductItems(products: List<ProductState>)
        fun showProductDetail(productState: ProductState, recentProductState: RecentProductState?)
        fun showEmptyProducts()
        fun showProducts()
        fun showCartProductCountBadge()
        fun hideCartProductCount()
    }

    interface Presenter {
        fun loadRecentProducts()
        fun loadMoreProducts()
        fun loadCartSizeBadge()
        fun loadCartProductsQuantity()
        fun addRecentProduct(product: Product)
        fun showProductDetail(productState: ProductState)
        fun storeCartProduct(productState: ProductState)
        fun minusCartProductQuantity(cartProductState: CartProductState)
        fun plusCartProductQuantity(cartProductState: CartProductState)
    }
}
