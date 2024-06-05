package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.ui.cart.recommendation.RecommendationFragment
import woowacourse.shopping.presentation.ui.cart.selection.SelectionFragment

class CartActivity : AppCompatActivity(), FragmentController {
    private lateinit var binding: ActivityCartBinding
    private val selectionFragment by lazy { SelectionFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickListener = this

        setupInitialFragment(savedInstanceState)
    }

    private fun setupInitialFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.cart_fragment, selectionFragment)
                .commit()
        }
    }

    override fun onBackButtonClicked() {
        when (val topFragment = supportFragmentManager.fragments.lastOrNull()) {
            is SelectionFragment -> {
                supportFragmentManager.beginTransaction()
                    .remove(topFragment)
                    .setReorderingAllowed(true)
                    .commit()
                finish()
            }

            is RecommendationFragment -> {
                selectionFragment.onShow()
                supportFragmentManager.beginTransaction()
                    .remove(topFragment)
                    .show(selectionFragment)
                    .setReorderingAllowed(true)
                    .commit()
            }

            else -> {
                finish()
            }
        }
    }

    override fun onOrderButtonClicked() {
        when (val topFragment = supportFragmentManager.fragments.lastOrNull()) {
            is SelectionFragment -> {
                supportFragmentManager.beginTransaction()
                    .hide(topFragment)
                    .add(R.id.cart_fragment, RecommendationFragment())
                    .setReorderingAllowed(true)
                    .commit()
            }

            is RecommendationFragment -> {
                finish()
            }

            else -> {
                finish()
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
