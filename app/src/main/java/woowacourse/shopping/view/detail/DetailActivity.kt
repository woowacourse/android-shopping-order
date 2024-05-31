package woowacourse.shopping.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication.Companion.recentProductDatabase
import woowacourse.shopping.ShoppingApplication.Companion.remoteCartDataSource
import woowacourse.shopping.ShoppingApplication.Companion.remoteProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityDetailBinding
import woowacourse.shopping.view.cart.CartActivity
import woowacourse.shopping.view.state.UiState

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
            recentProductRepository = RecentProductRepositoryImpl(recentProductDatabase),
            productId = productId,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpDataBinding()
        observeViewModel()
        viewModel.saveRecentProduct(isMostRecentProductClicked)
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
            if (state is UiState.Error) {
                showError(
                    state.exception.message ?: getString(R.string.unknown_error),
                )
            }
        }

        viewModel.navigateToCart.observe(this) {
            it.getContentIfNotHandled()?.let {
                putCartItem()
            }
        }

        viewModel.navigateToRecentDetail.observe(this) {
            it.getContentIfNotHandled()?.let {
                navigateToDetail()
            }
        }

        viewModel.isFinishButtonClicked.observe(this) {
            it.getContentIfNotHandled()?.let {
                finish()
            }
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
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

    private fun putCartItem() {
        Toast.makeText(this, PUR_CART_MESSAGE, Toast.LENGTH_SHORT).show()
        startActivity(CartActivity.createIntent(context = this))
    }

    companion object {
        private const val PUR_CART_MESSAGE = "장바구니에 상품이 추가되었습니다!"
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
