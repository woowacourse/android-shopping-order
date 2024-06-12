package woowacourse.shopping.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication.Companion.localRecentDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.app.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityDetailBinding
import woowacourse.shopping.ui.detail.action.DetailNavigationActions.NavigateToBack
import woowacourse.shopping.ui.detail.action.DetailNavigationActions.NavigateToRecentDetail
import woowacourse.shopping.ui.detail.action.DetailNotifyingActions.NotifyError
import woowacourse.shopping.ui.detail.action.DetailNotifyingActions.NotifyPutInCartItem
import woowacourse.shopping.ui.detail.viewmodel.DetailViewModel
import woowacourse.shopping.ui.detail.viewmodel.DetailViewModelFactory
import woowacourse.shopping.ui.state.UiState

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
        DetailViewModelFactory(
            cartRepository = CartRepositoryImpl(remoteCartDataSource),
            productRepository = ProductRepositoryImpl(remoteProductDataSource),
            recentProductRepository = RecentProductRepositoryImpl(localRecentDataSource),
            productId = productId,
            isMostRecentProductClicked = isMostRecentProductClicked,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpDataBinding()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.updateRecentProductVisible(isMostRecentProductClicked)
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.detailUiState.observe(this) { state ->
            when (state) {
                is UiState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )

                else -> return@observe
            }
        }

        viewModel.detailNavigationActions.observe(this) { detailNavigationActions ->
            detailNavigationActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is NavigateToRecentDetail -> navigateToDetail()
                    is NavigateToBack -> finish()
                }
            }
        }

        viewModel.detailNotifyingActions.observe(this) { detailNotifyingActions ->
            detailNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is NotifyPutInCartItem -> notifyPutInCartItem()
                    is NotifyError -> showError(getString(R.string.unknown_error))
                }
            }
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun notifyPutInCartItem() {
        Toast.makeText(this, getString(R.string.put_in_cart), Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail() {
        val recentProduct = viewModel.mostRecentProduct.value ?: return
        startActivity(
            createIntent(
                this,
                recentProduct.productId,
                true,
            ),
        )
    }

    companion object {
        private const val PRODUCT_ID = "product_id"
        private const val INVALID_PRODUCT_ID = -1
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
