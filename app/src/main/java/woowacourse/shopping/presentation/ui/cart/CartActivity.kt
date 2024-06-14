package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.base.ViewModelFactory
import woowacourse.shopping.presentation.common.EventObserver
import woowacourse.shopping.presentation.ui.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.ui.cart.model.CartNavigation
import woowacourse.shopping.presentation.ui.curation.CurationActivity
import woowacourse.shopping.presentation.ui.payment.PaymentActivity
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity

class CartActivity : BindingActivity<ActivityCartBinding>(R.layout.activity_cart) {
    private val viewModel: CartViewModel by viewModels { ViewModelFactory() }
    private val cartAdapter: CartAdapter by lazy { CartAdapter(viewModel) }

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun initStartView() {
        initTitle()
        initAdapter()
        initData()
        initObserver()
        initBackPressed()
    }

    private fun initAdapter() {
        binding.rvCarts.adapter = cartAdapter
    }

    private fun initBackPressed() {
        onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Intent().apply {
                        putExtra(
                            ShoppingActivity.EXTRA_UPDATED_PRODUCT,
                            viewModel.updateUiModel,
                        )
                    }.run {
                        setResult(RESULT_OK, this)
                        finish()
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initData() {
        viewModel.findCartByOffset()
    }

    private fun initObserver() {
        binding.cartActionHandler = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.uiState.observe(this) {
            cartAdapter.submitList(it.cartProductUiModels)
        }
        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CartNavigation.ToPayment -> {
                        PaymentActivity.createIntent(this, it.paymentUiModel).apply { startActivity(this) }
                    }
                }
            },
        )
    }

    private fun initTitle() {
        title = getString(R.string.cart_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_curation -> {
                Intent(this, CurationActivity::class.java).apply {
                    startActivity(this)
                }
            }

            else -> {
                Intent().apply {
                    putExtra(
                        ShoppingActivity.EXTRA_UPDATED_PRODUCT,
                        viewModel.updateUiModel,
                    )
                }.run {
                    setResult(RESULT_OK, this)
                    finish()
                }
            }
        }
        return true
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
