package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.home.HomeActivity
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.CartFragment
import woowacourse.shopping.ui.order.recommend.RecommendFragment
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.order.viewmodel.OrderViewModelFactory
import woowacourse.shopping.ui.payment.PaymentActivity

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val cartFragment by lazy { CartFragment.newInstance() }
    private val recommendFragment by lazy { RecommendFragment.newInstance() }
    private val orderViewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(cartRepository = CartRepositoryImpl(remoteCartDataSource))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpDataBinding()
        observeViewModel()

        if (savedInstanceState == null) {
            addFragment(cartFragment)
        }
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = orderViewModel
    }

    private fun observeViewModel() {
        orderViewModel.orderNavigationActions.observe(this) { orderNavigationActions ->
            orderNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is OrderNavigationActions.NavigateToBack -> {
                        finish()
                        navigateToHome()
                    }

                    is OrderNavigationActions.NavigateToRecommend -> addFragment(recommendFragment)
                    is OrderNavigationActions.NavigateToPayment -> {
                        val cartItemIds =
                            orderViewModel.cartViewItems.value?.filter { cartViewItem -> cartViewItem.isChecked }
                                ?.map { it.cartItem.cartItemId }
                                ?: return@observe
                        navigateToPayment(cartItemIds)
                    }
                }
            }
        }

        orderViewModel.orderNotifyingActions.observe(this) { orderNotifyingActions ->
            orderNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is OrderNotifyingActions.NotifyCanNotOrder -> notifyCanNotOrder()
                    is OrderNotifyingActions.NotifyError -> showError(getString(R.string.unknown_error))
                }
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_cart, fragment)
            addToBackStack(null)
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        startActivity(HomeActivity.createIntent(this))
    }

    private fun navigateToPayment(cartItemIds: List<Int>) {
        startActivity(PaymentActivity.createIntent(this, cartItemIds))
    }

    private fun notifyCanNotOrder() {
        Toast.makeText(this, getString(R.string.can_not_order), Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
