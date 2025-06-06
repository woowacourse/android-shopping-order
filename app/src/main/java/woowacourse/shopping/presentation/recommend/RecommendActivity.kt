package woowacourse.shopping.presentation.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityRecommendBinding
import woowacourse.shopping.presentation.product.catalog.CatalogActivity
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemSuccess
import woowacourse.shopping.presentation.util.IntentCompat

class RecommendActivity : AppCompatActivity() {
    private lateinit var recommendAdapter: RecommendAdapter
    private val checkedItems: List<ProductUiModel> by lazy { requireCheckedItems() }

    private val binding: ActivityRecommendBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_recommend)
    }

    private val recommendViewModel: RecommendViewModel by viewModels {
        RecommendViewModel.provideFactory(checkedItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setupToolbar()
        setAdapter()
        setUpBinding()
        observeViewModel()
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
            recyclerViewRecommendProducts.adapter = recommendAdapter
        }
    }

    private fun setAdapter() {
        recommendAdapter =
            RecommendAdapter(
                handler = RecommendEventHandlerImpl(recommendViewModel),
                onQuantityClick = { product ->
                    recommendViewModel.toggleQuantity(product)
                },
            )
    }

    private fun requireCheckedItems(): List<ProductUiModel> {
        return IntentCompat.getParcelableArrayListExtra(
            intent,
            CHECKED_PRODUCTS_KEY,
        ) ?: emptyList()
    }

    private fun observeViewModel() {
        recommendViewModel.updatedProduct.observe(this) { product ->
            recommendAdapter.updateProduct(product)
        }

        recommendViewModel.orderEvent.observe(this) { state ->
            when (state) {
                is OrderItemSuccess -> {
                    navigateToMain()
                    showToast(R.string.message_order_success)
                }

                is OrderEvent.OrderItemFailure -> {
                    showToast(R.string.message_order_fail)
                }
            }
        }

        recommendViewModel.items.observe(this) { items ->
            recommendAdapter.submitList(items)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val CHECKED_PRODUCTS_KEY = "CheckedProducts"

        fun newIntent(
            context: Context,
            checkedItems: ArrayList<ProductUiModel>,
        ): Intent {
            return Intent(context, RecommendActivity::class.java).apply {
                putParcelableArrayListExtra(CHECKED_PRODUCTS_KEY, checkedItems)
            }
        }
    }
}
