package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

    private val viewModel: RecommendViewModel by viewModels {
        RecommendViewModel.FACTORY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setUpBinding()
        setOrderInfo()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@RecommendActivity
        }
    }

    private fun setOrderInfo() {
        val price = intent.getIntExtra(TOTAL_PRICE_KEY, 0)
        val count = intent.getIntExtra(TOTAL_COUNT_KEY, 0)
        viewModel.setOrderInfo(price, count)
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