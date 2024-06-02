package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.view.cart.viewmodel.CartViewModel
import woowacourse.shopping.view.cart.viewmodel.CartViewModelFactory

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            cartRepository = CartRepositoryImpl(remoteCartDataSource),
            orderRepository = OrderRepositoryImpl(remoteOrderDataSource),
            recentProductRepository = RecentProductRepositoryImpl(recentProductDatabase),
            productRepository = ProductRepositoryImpl(remoteProductDataSource),
        )
    }
    private val cartFragment by lazy { CartFragment() }
    private val recommendFragment by lazy { RecommendFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
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

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
