package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.CartActivity.OnClickHandler
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.model.ActivityResult
import woowacourse.shopping.ui.model.CartProductUiState
import woowacourse.shopping.ui.payment.PaymentActivity

class CartActivity : DataBindingActivity<ActivityCartBinding>(R.layout.activity_cart) {
    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFragmentContainer(savedInstanceState)
        initSupportActionBar()
        initViewBinding()
        initObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun initFragmentContainer(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.cartContainer, CartProductFragment())
            }
        }
    }

    private fun initSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.cart_title)
    }

    private fun initViewBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.onClickHandler =
            OnClickHandler {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.cartContainer)

                when (currentFragment) {
                    is CartProductFragment -> {
                        supportFragmentManager.commit {
                            replace(R.id.cartContainer, CartRecommendFragment())
                        }
                        binding.cartCheckAllButton.visibility = View.GONE
                        binding.cartCheckAllButtonText.visibility = View.GONE
                    }

                    is CartRecommendFragment -> {
                        val intent = PaymentActivity.newIntent(this, viewModel.getSelectedProductIds().toLongArray())
                        startActivity(intent)
                    }
                }
            }
    }

    private fun initObservers() {
        viewModel.uiState.observe(this) { uiState ->
            handleCartProducts(uiState)
            handleErrorMessage(uiState)
        }
    }

    private fun handleCartProducts(uiState: CartProductUiState) {
        setResult(
            ActivityResult.CART_PRODUCT_EDITED.code,
            Intent().apply {
                putExtra(
                    ActivityResult.CART_PRODUCT_EDITED.key,
                    uiState.editedProductIds.toLongArray(),
                )
            },
        )
    }

    private fun handleErrorMessage(uiState: CartProductUiState) {
        uiState.connectionErrorMessage?.let {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    fun interface OnClickHandler {
        fun onOrderClick()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
