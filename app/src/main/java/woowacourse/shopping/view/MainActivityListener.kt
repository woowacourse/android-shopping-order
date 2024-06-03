package woowacourse.shopping.view

import androidx.fragment.app.Fragment

interface MainActivityListener {
    fun changeFragment(nextFragment: Fragment)

    fun popFragment()

    fun resetFragment()

    fun observeProductList(onProductsUpdated: (Map<Long, Int>) -> Unit)

    fun saveUpdateProduct(
        productId: Long,
        count: Int,
    )

    fun observeRecentlyProduct(onRecentlyProductReset: () -> Unit)

    fun saveUpdateRecentlyProduct()

    fun observeCartItem(onCartItemUpdated: () -> Unit)

    fun saveUpdateCartItem()
}
