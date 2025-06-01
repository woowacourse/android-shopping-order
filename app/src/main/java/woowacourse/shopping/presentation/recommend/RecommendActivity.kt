package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding

class RecommendActivity : AppCompatActivity() {
    private val binding: ActivityRecommendBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_recommend)
    }

    private val price: Int by lazy { intent.getIntExtra(TOTAL_PRICE_KEY, 0) }
    private val count: Int by lazy { intent.getIntExtra(TOTAL_COUNT_KEY, 0) }

    private val recommendViewModel: RecommendViewModel by viewModels {
        RecommendViewModel.provideFactory(price, count)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setupToolbar()
        setUpBinding()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_cart_action_bar)
        }
    }

    private fun setUpBinding() {
        binding.apply {
            viewModel = recommendViewModel
            lifecycleOwner = this@RecommendActivity
        }
    }

    companion object {
        private const val TOTAL_PRICE_KEY = "Price"
        private const val TOTAL_COUNT_KEY = "Count"

        fun newIntent(
            context: Context,
            price: Int,
            count: Int,
        ): Intent {
            return Intent(context, RecommendActivity::class.java).apply {
                putExtra(TOTAL_PRICE_KEY, price)
                putExtra(TOTAL_COUNT_KEY, count)
            }
        }
    }
}