package woowacourse.shopping.presentation.ui.shoppingcart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.domain.mapper.toPresentation
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.base.BaseActivity
import woowacourse.shopping.presentation.model.CartsWrapper
import woowacourse.shopping.presentation.ui.payment.PaymentActivity
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.CartSelectFragment
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.OrderRecommendFragment

class ShoppingCartActivity :
    BaseActivity<ActivityShoppingCartBinding>(R.layout.activity_shopping_cart),
    ShoppingCartNavigateAction {
    override fun initCreateView() {
        initActionBar()
        initFragment()
    }

    private fun initActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cart_title)
        }
    }

    private fun initFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view_main, CartSelectFragment(), CartSelectFragment.TAG)
            addToBackStack(CartSelectFragment.TAG)
        }
    }

    override fun navigateToOrderRecommend(carts: List<Cart>) {
        val bundle = Bundle()
        bundle.putSerializable(
            OrderRecommendFragment.PUT_EXTRA_CART_IDS_KEY,
            CartsWrapper(carts.map { it.toPresentation() }),
        )

        val orderRecommendFragment = OrderRecommendFragment()
        orderRecommendFragment.arguments = bundle
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view_main, orderRecommendFragment)
            addToBackStack(OrderRecommendFragment.TAG)
        }
    }

    override fun navigateToPayment(cartsWrapper: CartsWrapper) {
        val intent = PaymentActivity.getIntent(this, cartsWrapper)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main)
        } else {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingCartActivity::class.java)
        }
    }
}
