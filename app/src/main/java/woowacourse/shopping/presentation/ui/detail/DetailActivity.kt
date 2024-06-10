package woowacourse.shopping.presentation.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteShoppingRepositoryImpl
import woowacourse.shopping.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val productId: Long by lazy { intent.getLongExtra(PRODUCT_ID, INVALID_PRODUCT_ID) }
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(
            cartRepository = RemoteCartRepositoryImpl(),
            shoppingRepository = RemoteShoppingRepositoryImpl(),
            recentProductRepository = RecentProductRepositoryImpl(applicationContext),
            productId = productId,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViewModel()
        observeViewModel()
        binding.lifecycleOwner = this
    }

    private fun setUpViewModel() {
        binding.viewModel = viewModel
        binding.countHandler = viewModel
    }

    private fun observeViewModel() {
        viewModel.isAddCartSuccess.observe(this) {
            it.getContentIfNotHandled()?.let {
                navigateToShopping()
            }
        }

        viewModel.moveBack.observe(this) {
            it.getContentIfNotHandled()?.let {
                finish()
            }
        }

        viewModel.navigateToDetail.observe(this) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun navigateToShopping() {
        popUpToast()
        finish()
    }

    private fun popUpToast() {
        Toast.makeText(this, getString(R.string.item_added_to_cart), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val PRODUCT_ID = "product_id"
        const val INVALID_PRODUCT_ID = -1L

        fun createIntent(
            context: Context,
            productId: Long,
        ): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
            }
        }
    }
}
