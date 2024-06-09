package woowacourse.shopping.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityDetailBinding
import woowacourse.shopping.view.home.HomeActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val productId: Int by lazy { intent.getIntExtra(PRODUCT_ID, INVALID_PRODUCT_ID) }
    private val isMostRecentProductClicked: Boolean by lazy {
        intent.getBooleanExtra(
            IS_MOST_RECENT_PRODUCT_CLICKED,
            DEFAULT_IS_MOST_RECENT_PRODUCT_CLICKED,
        )
    }
    private val viewModel: DetailViewModel by viewModels {
        (application as ShoppingApplication).detailViewModelFactory(productId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpDataBinding()
        observeViewModel()
        initializeOnBackPressedCallback()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.productDetailUiState.observe(this) {
            if (!it.isLoading && isMostRecentProductClicked) {
                binding.clRecentViewedProducts.visibility = View.GONE
            }
        }

        viewModel.detailUiEvent.observe(this) {
            when (val event = it.getContentIfNotHandled() ?: return@observe) {
                is DetailUiEvent.ProductAddedToCart -> showToastMessage(getString(R.string.detail_message_add_to_cart))
                is DetailUiEvent.NavigateToRecentProduct -> navigateToDetail(event.productId)
                is DetailUiEvent.NavigateBack -> navigateBackToHome()
                is DetailUiEvent.Error -> showToastMessage(getString(R.string.unknown_error))
            }
            viewModel.saveRecentProduct()
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initializeOnBackPressedCallback() {
        val onBackPressedCallBack =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = navigateBackToHome()
            }
        onBackPressedDispatcher.addCallback(onBackPressedCallBack)
    }

    private fun navigateToDetail(productId: Int) {
        startActivity(
            createIntent(
                this,
                productId,
                true,
            ),
        )
    }

    private fun navigateBackToHome() {
        val itemIds = viewModel.alteredProductIds.toIntArray()
        setResult(
            RESULT_OK,
            HomeActivity.createIntent(this, itemIds),
        )
        finish()
    }

    companion object {
        const val PRODUCT_ID = "product_id"
        const val INVALID_PRODUCT_ID = -1
        private const val IS_MOST_RECENT_PRODUCT_CLICKED = "is_most_recent_product_clicked"
        private const val DEFAULT_IS_MOST_RECENT_PRODUCT_CLICKED = false

        fun createIntent(
            context: Context,
            productId: Int,
            isMostRecentProductClicked: Boolean = DEFAULT_IS_MOST_RECENT_PRODUCT_CLICKED,
        ): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
                putExtra(IS_MOST_RECENT_PRODUCT_CLICKED, isMostRecentProductClicked)
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            }
        }
    }
}
