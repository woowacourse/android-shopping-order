package woowacourse.shopping.presentation.ui.shoppingcart

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.presentation.base.BaseActivity
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.CartSelectFragment

class ShoppingCartActivity :
    BaseActivity<ActivityShoppingCartBinding>(R.layout.activity_shopping_cart) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_main)
        } else {
            finish()
        }
        return true
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingCartActivity::class.java)
        }
    }
}
