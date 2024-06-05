package woowacourse.shopping.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ui.cart.ShoppingCartFragment
import woowacourse.shopping.ui.order.OrderFragment
import woowacourse.shopping.ui.order.OrderFragment.Companion.ORDER_INFORMATION
import woowacourse.shopping.ui.model.OrderInformation
import woowacourse.shopping.ui.product.ProductListFragment
import woowacourse.shopping.ui.productDetail.ProductDetailFragment
import woowacourse.shopping.ui.productDetail.ProductDetailFragment.Companion.PRODUCT_ID

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
        supportFragmentManager.commit {
            replace(R.id.container, ProductListFragment::class.java, null, ProductListFragment.TAG)
        }
    }

    override fun navigateToProductList() {
        supportFragmentManager.commit {
            replace(R.id.container, ProductListFragment::class.java, null, ProductListFragment.TAG)
            addToBackStack(ProductListFragment.TAG)
        }
    }

    override fun navigateToShoppingCart() {
        supportFragmentManager.commit {
            replace(R.id.container, ShoppingCartFragment::class.java, null, ShoppingCartFragment.TAG)
            addToBackStack(ShoppingCartFragment.TAG)
        }
    }

    override fun navigateToProductDetail(productId: Long) {
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            replace(
                R.id.container,
                ProductDetailFragment::class.java,
                Bundle().apply {
                    putLong(PRODUCT_ID, productId)
                },
                ProductDetailFragment.TAG,
            )
            addToBackStack(ProductDetailFragment.TAG)
        }
    }

    override fun navigateToOrder(orderInformation: OrderInformation) {
        supportFragmentManager.commit {
            replace(
                R.id.container,
                OrderFragment::class.java,
                Bundle().apply {
                    putSerializable(ORDER_INFORMATION, orderInformation)
                },
                OrderFragment.TAG,
            )
            addToBackStack(OrderFragment.TAG)
        }
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }
}

interface FragmentNavigator {
    fun navigateToProductList()

    fun navigateToShoppingCart()

    fun navigateToProductDetail(productId: Long)

    fun navigateToOrder(orderInformation: OrderInformation)

    fun popBackStack()
}
