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
import woowacourse.shopping.app.ShoppingApplication.Companion.cartDataSourceImpl
import woowacourse.shopping.app.ShoppingApplication.Companion.orderDataSourceImpl
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
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
        OrderViewModelFactory(
            orderRepository = OrderRepositoryImpl(orderDataSourceImpl),
            cartRepository = CartRepositoryImpl(cartDataSourceImpl),
        )
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
                    is OrderNavigationActions.NavigateToPayment -> navigateToPayment()
                }
            }
        }

        orderViewModel.orderNotifyingActions.observe(this) { orderNotifyingActions ->
            orderNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is OrderNotifyingActions.NotifyOrderCompleted -> {
                        finish()
                        startActivity(HomeActivity.createIntent(this))
                        notifyOrderCompleted()
                    }

                    is OrderNotifyingActions.NotifyCanNotOrder -> {
                        notifyCanNotOrder()
                    }
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

    private fun navigateToHome() {
        startActivity(HomeActivity.createIntent(this))
    }

    private fun navigateToPayment() {
        startActivity(PaymentActivity.createIntent(this))
    }

    private fun notifyOrderCompleted() {
        Toast.makeText(this, ORDER_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    private fun notifyCanNotOrder() {
        Toast.makeText(this, CAN_NOT_ORDER_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ORDER_COMPLETED_MESSAGE = "상품 주문이 완료되었습니다!"
        private const val CAN_NOT_ORDER_MESSAGE = "최소 1개 이상의 상품을 주문해주세요!"

        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
