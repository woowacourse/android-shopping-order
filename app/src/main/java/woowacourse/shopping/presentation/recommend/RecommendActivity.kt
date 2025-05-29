package woowacourse.shopping.presentation.recommend

import android.app.Activity
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
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.product.ItemClickListener

class RecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendBinding
    private val viewModel: RecommendViewModel by viewModels { RecommendViewModelFactory() }
    private val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(viewModel as ItemClickListener, viewModel as CartCounterClickListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clRecommend) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbRecommend)
        binding.tbRecommend.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initAdapter() {
        binding.rvRecommend.apply {
            adapter = recommendAdapter
            itemAnimator = null
        }
    }

    private fun observeViewModel() {
        viewModel.recommendProducts.observe(this) {
            recommendAdapter.submitList(it)
        }
    }


    companion object {
        fun newIntent(context: Context): Intent = Intent(context, RecommendActivity::class.java)
    }
}
