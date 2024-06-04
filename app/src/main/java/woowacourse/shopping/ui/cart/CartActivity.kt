package woowacourse.shopping.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.databinding.ActivityCartBinding

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
            RemoteOrderRepository,
        )
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
            if (isVisibleCartSelectionFragment()) {
                finish()
            }
            removeLastFragment()
        }
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
                viewModel.createOrder()
            }
        }
        viewModel.isSuccessCreateOrder.observe(this) {
            val isSuccessCreateOrder = it.getContentIfNotHandled() ?: return@observe
            if (isSuccessCreateOrder) {
                showDialogSuccessCreateOrder()
            } else {
                showToastFailureCreateOrder()
            }
        }
    }

    private fun showDialogSuccessCreateOrder() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_order_success_title))
            .setMessage(getString(R.string.create_order_success))
            .setPositiveButton(getString(R.string.common_confirm)) { _, _ ->
                navigateToProductsView()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToProductsView() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToastFailureCreateOrder() {
        Toast.makeText(this, R.string.create_order_failure, Toast.LENGTH_SHORT).show()
    }
}
