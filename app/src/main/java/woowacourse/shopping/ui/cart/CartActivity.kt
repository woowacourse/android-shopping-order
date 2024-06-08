package woowacourse.shopping.ui.cart

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.coupon.CouponActivity

class CartActivity : AppCompatActivity() {
    private val cartSelectionFragment: Fragment by lazy { CartSelectionFragment() }
    private val cartRecommendFragment: Fragment by lazy { CartRecommendFragment() }

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        CartViewModelFactory(
            RemoteProductRepository,
            RoomRecentProductRepository.getInstance(
                ShoppingCartDataBase.getInstance(
                    applicationContext,
                ).recentProductDao(),
            ),
            RemoteCartRepository,
        )
    }

    private val removeFragment: () -> Unit = {
        if (isVisibleCartSelectionFragment()) {
            finish()
        }
        removeLastFragment()
    }

    private val backPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                removeFragment()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (savedInstanceState == null) {
            addFragment(cartSelectionFragment)
        }

        initializeView()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view_cart, fragment)
            addToBackStack(null)
        }
    }

    private fun initializeView() {
        initializeToolbar()
        observeData()
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            removeFragment()
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun isVisibleCartSelectionFragment(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_cart)
        return fragment is CartSelectionFragment
    }

    private fun removeLastFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun observeData() {
        viewModel.changedCartEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            setResult(Activity.RESULT_OK)
        }
        viewModel.orderEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            if (isVisibleCartSelectionFragment()) {
                addFragment(cartRecommendFragment)
            } else {
                viewModel.navigateCoupon()
            }
        }
        viewModel.selectedCartItemIds.observe(this) {
            navigateToCouponView(it)
        }
        viewModel.cartErrorEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            showToastCartFailure()
        }
    }

    private fun showToastCartFailure() {
        Toast.makeText(this, R.string.common_error_retry, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCouponView(selectedCartItemIds: List<Int>) {
        val intent = CouponActivity.newIntent(this, selectedCartItemIds)
        startActivity(intent)
    }
}
