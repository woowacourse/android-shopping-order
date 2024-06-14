package woowacourse.shopping.presentation.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.common.observeEvent
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.detail.ProductDetailActivity
import woowacourse.shopping.presentation.purchase.PurchaseActivity

class CartActivity : AppCompatActivity() {
    private lateinit var cartSelectionFragment: Fragment
    private lateinit var cartRecommendFragment: Fragment

    private val binding by lazy { ActivityCartBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CartViewModel> {
        (application as ShoppingApplication).getCartViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = CartFragmentFactory(viewModel)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        cartSelectionFragment =
            supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                CartSelectionFragment::class.java.name,
            )
        cartRecommendFragment =
            supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                CartRecommendFragment::class.java.name,
            )

        changeFragment(cartSelectionFragment)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.actionHandler = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeToolbar()
        initializeCartAdapter()
        viewModel.changedCartEvent.observe(this) {
            setResult(Activity.RESULT_OK)
        }
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view_cart, fragment)
            addToBackStack(null)
        }
    }

    private fun initializeCartAdapter() {
        viewModel.navigateAction.observeEvent(this) { cartNavigationAction ->
            when (cartNavigationAction) {
                is CartNavigateAction.RecommendNavigateAction -> {
                    val fragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container_view_cart)
                    if (fragment is CartSelectionFragment) {
                        changeFragment(cartRecommendFragment)
                    }
                }

                is CartNavigateAction.ProductDetailNavigateAction -> {
                    val intent =
                        ProductDetailActivity.getIntent(
                            this,
                            productId = cartNavigationAction.productId,
                        )
                    startActivity(intent)
                }
                is CartNavigateAction.PurchaseProductNavigateAction -> {
                    PurchaseActivity.startActivity(this, cartNavigationAction.cartItems)
                }
            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
