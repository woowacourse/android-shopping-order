package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.cart.recommendation.CartProductRecommendationFragment
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment

class ShoppingCartActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingCartBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val app = application as ShoppingApplication
        ViewModelProvider(
            this,
            ShoppingCartViewModelFactory(
                app.cartProductRepository,
                app.recentProductRepository,
                app.productRepository,
            ),
        )[ShoppingCartViewModel::class.java]
    }
    private var currentFragment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = ShoppingCartFragmentFactory(viewModel)
        super.onCreate(savedInstanceState)
        setUpView()
        supportActionBar?.title = ACTION_BAR_TITLE

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                addToBackStack(null)
                add(
                    R.id.fragment,
                    CartProductSelectionFragment::class.java,
                    null,
                    SELECTION_FRAGMENT_TAG,
                )
                currentFragment = SELECTION_FRAGMENT_TAG
            }
        }

        initBindings()
    }

    private fun setUpView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initBindings() {
        binding.handler = viewModel
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        binding.btnOrder.setOnClickListener {
            if (currentFragment == SELECTION_FRAGMENT_TAG) {
                binding.llSelectAllProducts.visibility = View.INVISIBLE
                supportFragmentManager.commit {
                    addToBackStack(null)
                    add(
                        R.id.fragment,
                        CartProductRecommendationFragment::class.java,
                        null,
                        RECOMMENDATION_FRAGMENT_TAG,
                    )
                    currentFragment = RECOMMENDATION_FRAGMENT_TAG
                }
            }
        }
    }

    companion object {
        private const val SELECTION_FRAGMENT_TAG = "selection"
        private const val RECOMMENDATION_FRAGMENT_TAG = "recommendation"

        fun newIntent(context: Context): Intent = Intent(context, ShoppingCartActivity::class.java)

        private const val ACTION_BAR_TITLE = "Cart"
    }
}
