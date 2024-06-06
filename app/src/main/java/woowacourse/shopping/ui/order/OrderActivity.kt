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
import woowacourse.shopping.app.ShoppingApplication.Companion.productDataSourceImpl
import woowacourse.shopping.app.ShoppingApplication.Companion.recentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.order.action.OrderNavigationActions
import woowacourse.shopping.ui.order.action.OrderNotifyingActions
import woowacourse.shopping.ui.order.cart.CartFragment
import woowacourse.shopping.ui.order.recommend.RecommendFragment
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.order.viewmodel.OrderViewModelFactory

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(
            cartRepository = CartRepositoryImpl(cartDataSourceImpl),
            orderRepository = OrderRepositoryImpl(orderDataSourceImpl),
            recentProductRepository = RecentProductRepositoryImpl(recentProductDatabase),
            productRepository = ProductRepositoryImpl(productDataSourceImpl),
        )
    }
    private val cartFragment by lazy { CartFragment.newInstance() }
    private val recommendFragment by lazy { RecommendFragment.newInstance() }

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
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.orderNavigationActions.observe(this) { orderNavigationActions ->
            orderNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is OrderNavigationActions.NavigateToBack -> finish()
                    is OrderNavigationActions.NavigateToRecommend -> addFragment(recommendFragment)
                }
            }
        }

        viewModel.orderNotifyingActions.observe(this) { orderNotifyingActions ->
            orderNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is OrderNotifyingActions.NotifyCartCompleted -> {
                        notifyOrderCompleted()
                        finish()
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

    private fun notifyOrderCompleted() {
        Toast.makeText(this, ORDER_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ORDER_COMPLETED_MESSAGE = "상품 주문이 완료되었습니다!"

        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
