package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartTempBinding
import woowacourse.shopping.presentation.ui.cart.recommendation.RecommendationFragment
import woowacourse.shopping.presentation.ui.cart.selection.SelectionFragment

class CartActivity : AppCompatActivity(), CartClickListener {
    private lateinit var binding: ActivityCartTempBinding
    private val selectionFragment by lazy { SelectionFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickListener = this

        setupInitialFragment()
    }

    private fun setupInitialFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.cart_fragment, selectionFragment)
            .commit()
    }

    override fun onBackButtonClick() {
        val topFragment = supportFragmentManager.fragments.lastOrNull()
        when (topFragment) {
            is SelectionFragment -> {
                supportFragmentManager.beginTransaction()
                    .remove(selectionFragment)
                    .setReorderingAllowed(true)
                    .commit()
                finish()
            }

            is RecommendationFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.cart_fragment, selectionFragment)
                    .setReorderingAllowed(true)
                    .commit()
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
