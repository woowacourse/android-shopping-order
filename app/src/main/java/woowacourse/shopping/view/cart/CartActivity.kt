package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.view.cart.list.CartFragment
import woowacourse.shopping.view.cart.list.CartListUiEvent
import woowacourse.shopping.view.cart.recommend.RecommendFragment
import woowacourse.shopping.view.cart.recommend.RecommendListUiEvent
import woowacourse.shopping.view.detail.DetailActivity
import woowacourse.shopping.view.home.HomeActivity
import woowacourse.shopping.view.order.OrderActivity

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        (application as ShoppingApplication).cartViewModelFactory
    }
    private val cartFragment by lazy { CartFragment() }
    private val recommendFragment by lazy { RecommendFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            replaceFragment(cartFragment)
        }
        setUpDataBinding()
        observeViewModel()
        initializeOnBackPressedCallback()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_cart, fragment)
        }
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeOnBackPressedCallback() {
        val onBackPressedCallBack =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = navigateBackToHome()
            }
        onBackPressedDispatcher.addCallback(onBackPressedCallBack)
    }

    private fun observeViewModel() {
        viewModel.cartListUiEvent.observe(this) {
            val event = it.getContentIfNotHandled() ?: return@observe
            when (event) {
                is CartListUiEvent.NavigateToRecommendList -> replaceFragment(recommendFragment)
                is CartListUiEvent.NavigateToProductDetail -> {
                    startActivity(
                        DetailActivity.createIntent(
                            this,
                            event.productId,
                            event.lastlyViewed,
                        ),
                    )
                }

                is CartListUiEvent.NavigateBack -> navigateBackToHome()
            }
        }

        viewModel.recommendListUiEvent.observe(this) {
            val event = it.getContentIfNotHandled() ?: return@observe
            when (event) {
                is RecommendListUiEvent.NavigateBackToCartList -> replaceFragment(cartFragment)

                is RecommendListUiEvent.NavigateToProductDetail -> {
                    startActivity(
                        DetailActivity.createIntent(
                            this,
                            event.productId,
                            event.lastlyViewed,
                        ),
                    )
                }

                is RecommendListUiEvent.NavigateBackToHome -> finish()
                is RecommendListUiEvent.NavigateToOrder -> {
                    startActivity(OrderActivity.createIntent(this, event.cartItems))
                }
            }
        }

        viewModel.navigateBackToHome.observe(this) {
            if (it.getContentIfNotHandled() != null) finish()
        }
    }

    private fun navigateBackToHome() {
        val itemIds = viewModel.alteredProductIds.toIntArray()
        setResult(
            RESULT_OK,
            HomeActivity.createIntent(this, itemIds),
        )
        finish()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
