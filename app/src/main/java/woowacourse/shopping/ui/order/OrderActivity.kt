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
import woowacourse.shopping.ShoppingApplication.Companion.recentProductDatabase
import woowacourse.shopping.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.ShoppingApplication.Companion.remoteOrderDataSource
import woowacourse.shopping.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.order.viewmodel.OrderViewModel
import woowacourse.shopping.ui.order.viewmodel.OrderViewModelFactory

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(
            cartRepository = CartRepositoryImpl(remoteCartDataSource),
            orderRepository = OrderRepositoryImpl(remoteOrderDataSource),
            recentProductRepository = RecentProductRepositoryImpl(recentProductDatabase),
            productRepository = ProductRepositoryImpl(remoteProductDataSource),
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
        addFragment()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.isBackButtonClicked.observe(this) { back ->
            back.getContentIfNotHandled()?.let {
                finish()
            }
        }
        viewModel.navigateToBack.observe(this) { navigateToBack ->
            navigateToBack.getContentIfNotHandled()?.let {
                finish()
            }
        }
        viewModel.navigateToRecommend.observe(this) { navigateToRecommend ->
            navigateToRecommend.getContentIfNotHandled()?.let {
                replaceFragment(recommendFragment)
            }
        }

        viewModel.notifyOrderCompleted.observe(this) { notifyOrderCompleted ->
            notifyOrderCompleted.getContentIfNotHandled()?.let {
                alertOrderCompleted()
                finish()
            }
        }
    }

    private fun addFragment() {
        supportFragmentManager.commit {
            replace(R.id.fragment_cart, cartFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_cart, fragment)
        }
    }

    private fun alertOrderCompleted() {
        Toast.makeText(this, ORDER_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ORDER_COMPLETED_MESSAGE = "상품 주문이 완료되었습니다!"

        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
