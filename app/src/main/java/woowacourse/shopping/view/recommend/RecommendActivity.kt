package woowacourse.shopping.view.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.databinding.ActivityRecommnedBinding

class RecommendActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRecommnedBinding.inflate(layoutInflater) }
    private val viewModel: RecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter =
            RecommendProductsAdapter(
                object : RecommendProductItemActions {
                    override fun onPlusProductQuantity(item: RecommendProduct) {
                        viewModel.plusCartItemQuantity(item)
                    }

                    override fun onMinusProductQuantity(item: RecommendProduct) {
                        viewModel.minusCartItemQuantity(item)
                    }
                },
            )

        binding.lifecycleOwner = this
        binding.recommendCartItems.adapter = adapter
        viewModel.recommendedProducts.observe(this) { adapter.submitList(it) }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, RecommendActivity::class.java)
            return intent
        }
    }
}
