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
import woowacourse.shopping.ui.detail.action.DetailNotifyingActions
import woowacourse.shopping.ui.detail.viewmodel.DetailViewModel
import woowacourse.shopping.ui.detail.viewmodel.DetailViewModelFactory
import woowacourse.shopping.ui.home.HomeActivity
import woowacourse.shopping.ui.home.HomeActivity.Companion.HOME_ORIGIN
import woowacourse.shopping.ui.order.OrderActivity
import woowacourse.shopping.ui.order.cart.CartFragment.Companion.CART_ORIGIN
import woowacourse.shopping.ui.order.recommend.RecommendFragment.Companion.RECOMMEND_ORIGIN
import woowacourse.shopping.ui.state.UiState

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val productId: Int by lazy { intent.getIntExtra(PRODUCT_ID, INVALID_PRODUCT_ID) }
    private val origin: String by lazy { intent.getStringExtra(ORIGIN) ?: INVALID_ORIGIN }
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(
            cartRepository = CartRepositoryImpl(remoteCartDataSource),
            productRepository = ProductRepositoryImpl(remoteProductDataSource),
            recentProductRepository = RecentProductRepositoryImpl(localRecentDataSource),
            productId = productId,
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
        viewModel.updateRecentProductVisible(origin == DETAIL_ORIGIN)
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        viewModel.product.observe(this) {
            viewModel.saveRecentProduct(origin == DETAIL_ORIGIN)
        }

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
                    is NavigateToBack -> {
                        when (origin) {
                            HOME_ORIGIN -> {
                                finish()
                                navigateToHome()
                            }

                            CART_ORIGIN -> {
                                finish()
                                navigateToCart()
                            }

                            DETAIL_ORIGIN, RECOMMEND_ORIGIN -> {
                                finish()
                            }
                        }
                    }
                }
            }
        }

        viewModel.detailNotifyingActions.observe(this) { detailNotifyingActions ->
            detailNotifyingActions.getContentIfNotHandled()?.let { action ->
                when (action) {
                    is DetailNotifyingActions.NotifyPutInCartItem -> notifyPutInCartItem()
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

    private fun navigateToHome() {
        startActivity(HomeActivity.createIntent(this))
    }

    private fun navigateToCart() {
        startActivity(OrderActivity.createIntent(this))
    }

    private fun navigateToDetail() {
        val recentProduct = viewModel.mostRecentProduct.value ?: return
        startActivity(
            createIntent(
                this,
                recentProduct.productId,
                DETAIL_ORIGIN,
            ),
        )
    }

    companion object {
        private const val PRODUCT_ID = "product_id"
        private const val INVALID_PRODUCT_ID = -1
        private const val ORIGIN = "origin"
        private const val INVALID_ORIGIN = ""
        const val DETAIL_ORIGIN = "detail"

        fun createIntent(
            context: Context,
            productId: Int,
            origin: String,
        ): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
                putExtra(ORIGIN, origin)
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            }
        }
    }
}
