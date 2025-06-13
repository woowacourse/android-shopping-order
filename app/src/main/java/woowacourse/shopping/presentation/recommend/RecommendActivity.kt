package woowacourse.shopping.presentation.recommend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.order.OrderActivity
import woowacourse.shopping.presentation.productdetail.ProductDetailActivity

class RecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendBinding
    private val viewModel: RecommendViewModel by viewModels { RecommendViewModelFactory() }
    private val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(itemClickListener = viewModel, counterClickListener = viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val productsIds =
            intent.getLongArrayExtra(Extra.KEY_SELECT_PRODUCT_IDS) ?: longArrayOf()
        viewModel.fetchSelectedInfo(productsIds)

        setOnBackPressedCallback()
        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent =
                        Intent().apply {
                            putExtra(Extra.KEY_RECOMMEND_IS_UPDATE, viewModel.isUpdated)
                        }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            },
        )
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
                val intent =
                    Intent().apply {
                        putExtra(Extra.KEY_RECOMMEND_IS_UPDATE, viewModel.isUpdated)
                    }
                setResult(Activity.RESULT_OK, intent)
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

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }

        viewModel.navigateToDetail.observe(this) { productId ->
            val intent =
                ProductDetailActivity.newIntent(this, productId = productId)
            startActivity(intent)
        }

        viewModel.navigateToOrder.observe(this) { orderProductIds ->
            val intent = OrderActivity.newIntent(this, orderProductIds)
            startActivity(intent)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newIntent(
            context: Context,
            selectedProductIds: LongArray,
        ): Intent =
            Intent(context, RecommendActivity::class.java).apply {
                putExtra(Extra.KEY_SELECT_PRODUCT_IDS, selectedProductIds)
            }
    }
}
