package woowacourse.shopping.shopping

import woowacourse.shopping.common.model.ProductModel
import woowacourse.shopping.common.model.RecentProductModel
import woowacourse.shopping.common.model.ShoppingProductModel

interface ShoppingContract {
    interface Presenter {
        fun reloadProducts()

        fun updateRecentProducts()

        fun setCartQuantity()

        fun openProduct(productModel: ProductModel)

        fun openCart()

        fun loadProductsInSize()

        fun decreaseCartProductAmount(shoppingProductModel: ShoppingProductModel)

        fun increaseCartProductAmount(shoppingProductModel: ShoppingProductModel)
    }

    interface View {
        fun afterLoad()

        fun updateProducts(productModels: List<ShoppingProductModel>)

        fun addProducts(productModels: List<ShoppingProductModel>)

        fun updateRecentProducts(recentProductModels: List<RecentProductModel>)

        fun showProductDetail(productModel: ProductModel, recentProductModel: ProductModel?)

        fun showCart()

        fun updateCartQuantity(amount: Int)

        fun updateShoppingProduct(shoppingProductModel: ShoppingProductModel)

        fun notifyLoadFailed()
    }
}
