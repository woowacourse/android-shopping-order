package woowacourse.shopping.presentation.ui.shoppingcart

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.presentation.base.BaseActivity
import woowacourse.shopping.presentation.ui.shoppingcart.cartselect.CartSelectFragment

class ShoppingCartActivity : BaseActivity<ActivityShoppingCartBinding>() {
    override val layoutResourceId: Int get() = R.layout.activity_shopping_cart

    override fun initCreateView() {
        replaceFragment(CartSelectFragment(), CartSelectFragment.TAG)
        setupBottomNavigationView()
        initActionBar()
    }

    private fun initActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cart_title)
        }
    }

    private fun setupBottomNavigationView() {
//        binding.bottomNavigationViewMain.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.fragment_home -> replaceFragment(HomeFragment(), HomeFragment.TAG)
//                R.id.fragment_setting -> replaceFragment(SettingFragment(), SettingFragment.TAG)
//                R.id.fragment_reservation_history ->
//                    replaceFragment(
//                        ReservationHistoryFragment(),
//                        ReservationHistoryFragment.TAG,
//                    )
//
//                else -> false
//            }
//        }
    }

    private fun replaceFragment(
        fragment: Fragment,
        tag: String,
    ): Boolean {
        val newFragment = supportFragmentManager.findFragmentByTag(tag)
        if (newFragment != null) {
            supportFragmentManager.popBackStack(tag, 0)
        } else {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_view_main, fragment, tag)
                addToBackStack(tag)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingCartActivity::class.java)
        }
    }
}
