package woowacourse.shopping.presentation.detail

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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.common.observeEvent
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.presentation.cart.CartActivity

class ProductDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProductDetailBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<ProductDetailViewModel> {
        (application as ShoppingApplication).getProductDetailViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.actionHandler = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeToolbar()
        viewModel.putOnCartEvent.observeEvent(this) { isSuccess ->
            when (isSuccess) {
                true -> showAddCartSuccessDialog()
                false -> showAddCartFailureToast()
            }
        }
    }

    private fun initializeToolbar() {
        binding.toolbarDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_exit -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(PRODUCT_ID_KEY, viewModel.productUiModel.value?.product?.id)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
            false
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

    private fun showErrorSnackBar() {
        Snackbar
            .make(binding.root, getString(R.string.common_error), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.common_confirm)) { finish() }
            .show()
    }

    companion object {
        const val PRODUCT_ID_KEY = "product_id_key"

        fun getIntent(
            context: Context,
            productId: Int,
        ) = Intent(context, ProductDetailActivity::class.java)
            .putExtra(PRODUCT_ID_KEY, productId)
    }
}
