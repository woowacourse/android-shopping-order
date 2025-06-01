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
import woowacourse.shopping.view.productDetail.ProductDetailActivity

class RecommendActivity :
    AppCompatActivity(),
    RecommendProductItemActions {
    private val binding by lazy { ActivityRecommnedBinding.inflate(layoutInflater) }
    private val viewModel: RecommendViewModel by viewModels()
    private val recommendProductsAdapter by lazy {
        RecommendProductsAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.lifecycleOwner = this
        binding.recommendCartItems.adapter = recommendProductsAdapter

        initBinding()
    }

    private fun initBinding() {
        viewModel.recommendProducts.observe(this) { recommendProducts ->
            recommendProductsAdapter.asdf(recommendProducts)
        }
    }

    override fun onSelectProduct(item: RecommendProduct) {
        val intent = ProductDetailActivity.newIntent(this, item.productId)
        startActivity(intent)
    }

    override fun onPlusProductQuantity(item: RecommendProduct) {
        // TODO: ViewModel 함수 사용
    }

    override fun onMinusProductQuantity(item: RecommendProduct) {
        // TODO: ViewModel 함수 사용
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, RecommendActivity::class.java)
            return intent
        }
    }
}
