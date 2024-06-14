package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.curation.CurationActivity
import woowacourse.shopping.presentation.ui.shopping.NavigateUiState

class CartActivity : BindingActivity<ActivityCartBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_cart

    private val viewModel: CartViewModel by viewModels { ViewModelFactory() }
    private val cartAdapter: CartAdapter by lazy { CartAdapter(viewModel) }

    override fun initStartView() {
        initTitle()
        binding.rvCarts.adapter = cartAdapter
        binding.cartActionHandler = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initData()
        initObserver()
    }

    override fun onResume() {
        if (intent.getBooleanExtra(EXTRA_BACK_FROM_CURATION, false)) {
            initData()
        }
        super.onResume()
    }

    private fun initData() {
        viewModel.findCartByOffset()
    }

    private fun initObserver() {
        viewModel.carts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                    binding.layoutShimmer.isVisible = true
                }

                is UiState.Success -> {
                    binding.layoutShimmer.isVisible = false
                    binding.tvOrderCount.text =
                        it.data.filter {
                            it.isChecked
                        }.sumOf { it.cartProduct.quantity }.toString()
                    binding.tvPrice.text =
                        getString(
                            R.string.won,
                            it.data.sumOf {
                                it.cartProduct.quantity * it.cartProduct.price
                            },
                        )
                    cartAdapter.submitList(it.data)
                }
            }
        }

        viewModel.errorHandler.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.eventHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CartEvent.Update -> {
                        Toast.makeText(this, "주문이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )

        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is NavigateUiState.ToOrder -> {
                        CurationActivity.createIntent(this, it.orderIds).apply {
                            startActivity(this)
                        }
                    }

                    else -> {
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
                setResult(RESULT_OK)
                finish()
            }
        }
        return true
    }

    companion object {
        const val EXTRA_BACK_FROM_CURATION = "backFromCuration"

        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
