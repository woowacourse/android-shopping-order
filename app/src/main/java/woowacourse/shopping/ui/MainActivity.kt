package woowacourse.shopping.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ui.cart.ShoppingCartFragment
import woowacourse.shopping.ui.order.OrderFragment
import woowacourse.shopping.ui.payment.PaymentFragment
import woowacourse.shopping.ui.productDetail.ProductDetailFragment
import woowacourse.shopping.ui.productDetail.ProductDetailFragment.Companion.PRODUCT_ID
import woowacourse.shopping.ui.productList.ProductListFragment

class MainActivity : AppCompatActivity(), FragmentNavigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if (savedInstanceState == null) {
            initFragment()
        }
    }

    private fun initFragment() {
        navigateToProductList()
    }

    override fun navigateToProductList() {
        supportFragmentManager.commit {
            replace(R.id.container, ProductListFragment::class.java, null, ProductListFragment.TAG)
        }
    }

    override fun navigateToShoppingCart() {
        supportFragmentManager.commit {
            replace(R.id.container, ShoppingCartFragment::class.java, null, ShoppingCartFragment.TAG)
            addToBackStack(ProductListFragment.TAG)
        }
    }

    override fun navigateToProductDetail(productId: Long) {
        removeBackStack()

        supportFragmentManager.commit {
            replace(
                R.id.container,
                ProductDetailFragment::class.java,
                Bundle().apply {
                    putLong(PRODUCT_ID, productId)
                },
                ProductDetailFragment.TAG,
            )
            addToBackStack(ProductListFragment.TAG)
        }
    }

    override fun navigateToOrder() {
        supportFragmentManager.commit {
            replace(
                R.id.container,
                OrderFragment::class.java,
                null,
                OrderFragment.TAG,
            )
        }
    }

    override fun navigateToPayment() {
        supportFragmentManager.commit {
            replace(R.id.container, PaymentFragment::class.java, null, PaymentFragment.TAG)
            addToBackStack(PaymentFragment.TAG)
        }
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    private fun removeBackStack() {
        val isFirstFragment = supportFragmentManager.backStackEntryCount == 0
        if (!isFirstFragment) {
            supportFragmentManager.popBackStackImmediate(null, 0)
        }
    }
}

interface FragmentNavigator {
    fun navigateToProductList()

    fun navigateToShoppingCart()

    fun navigateToProductDetail(productId: Long)

    fun navigateToOrder()

    fun navigateToPayment()

    fun popBackStack()
}
