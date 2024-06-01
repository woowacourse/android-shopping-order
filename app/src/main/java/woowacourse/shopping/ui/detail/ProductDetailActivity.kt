package woowacourse.shopping.ui.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.products.ProductsActivity

class ProductDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProductDetailBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ProductDetailViewModel> {
        ProductDetailViewModelFactory(
            productId(),
            RemoteProductRepository,
            RoomRecentProductRepository.getInstance(ShoppingCartDataBase.getInstance(applicationContext).recentProductDao()),
            RemoteCartRepository,
            isNavigatedFromDetailView(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.onClickLastRecentProductListener =
            OnClickLastRecentProductListener { productId ->
                val intent = newIntent(this@ProductDetailActivity, productId, lastSeenProductVisible = true)
                startActivity(intent)
                finish()
            }
        initializeView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProductDetail()
    }

    private fun initializeView() {
        initializeToolbar()
        initializeAddCartButton()
        initializeProductLoadError()
        setRequireActivityResult()
    }

    private fun initializeToolbar() {
        binding.toolbarDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_exit -> finish()
            }
            false
        }
    }

    private fun initializeAddCartButton() {
        binding.btnProductDetailAddCart.setOnClickListener {
            viewModel.addCartProduct()
        }

        viewModel.isSuccessAddCart.observe(this) { isSuccessEvent ->
            val isSuccess = isSuccessEvent.getContentIfNotHandled() ?: return@observe
            if (isSuccess) {
                showAddCartSuccessDialog()
            } else {
                showAddCartFailureToast()
            }
        }
    }

    private fun showAddCartSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_cart_success_title))
            .setMessage(getString(R.string.add_cart_success))
            .setPositiveButton(getString(R.string.common_move)) { _, _ ->
                navigateToCartView()
            }
            .setNegativeButton(getString(R.string.common_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun showAddCartFailureToast() {
        Toast.makeText(this, R.string.add_cart_failure, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCartView() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun initializeProductLoadError() {
        viewModel.productLoadError.observe(this) { errorEvent ->
            errorEvent.getContentIfNotHandled() ?: return@observe
            showErrorSnackBar()
        }
    }

    private fun showErrorSnackBar() {
        Snackbar
            .make(binding.root, getString(R.string.common_error_previous_view), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.common_confirm)) { finish() }
            .show()
    }

    private fun productId(): Int = intent.getIntExtra(PRODUCT_ID_KEY, PRODUCT_ID_DEFAULT_VALUE)

    private fun isNavigatedFromDetailView(): Boolean {
        return intent.getBooleanExtra(
            LAST_SEEN_PRODUCT_VISIBLE_KEY,
            LAST_SEEN_PRODUCT_VISIBLE_DEFAULT_VALUE,
        )
    }

    private fun setRequireActivityResult() {
        val resultIntent = Intent().putExtra(ProductsActivity.PRODUCT_ID_KEY, productId())
        setResult(Activity.RESULT_OK, resultIntent)
    }

    companion object {
        private const val PRODUCT_ID_KEY = "product_id_key"
        private const val PRODUCT_ID_DEFAULT_VALUE = -1
        private const val LAST_SEEN_PRODUCT_VISIBLE_KEY = "last_seen_product_visible"
        private const val LAST_SEEN_PRODUCT_VISIBLE_DEFAULT_VALUE = false

        fun newIntent(
            context: Context,
            productId: Int,
            lastSeenProductVisible: Boolean = false,
        ): Intent {
            return Intent(context, ProductDetailActivity::class.java)
                .putExtra(PRODUCT_ID_KEY, productId)
                .putExtra(LAST_SEEN_PRODUCT_VISIBLE_KEY, lastSeenProductVisible)
        }
    }
}
