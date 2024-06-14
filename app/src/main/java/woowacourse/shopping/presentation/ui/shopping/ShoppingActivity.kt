package woowacourse.shopping.presentation.ui.shopping

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.base.ViewModelFactory
import woowacourse.shopping.presentation.common.EventObserver
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.shopping.adapter.RecentAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewType
import woowacourse.shopping.presentation.ui.shopping.model.ShoppingNavigation
import woowacourse.shopping.utils.getParcelableExtraCompat

class ShoppingActivity : BindingActivity<ActivityShoppingBinding>(R.layout.activity_shopping) {
    private val viewModel: ShoppingViewModel by viewModels { ViewModelFactory() }
    private val shoppingAdapter: ShoppingAdapter by lazy { ShoppingAdapter(viewModel) }
    private val recentAdapter: RecentAdapter by lazy { RecentAdapter(viewModel) }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun initStartView() {
        initTitle()
        initAdapter()
        initObserver()
        initLauncher()
    }

    private fun initTitle() {
        supportActionBar?.hide()
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, GRIDLAYOUT_COL)
        layoutManager.spanSizeLookup = spanManager
        binding.rvShopping.layoutManager = layoutManager
        binding.rvShopping.adapter = shoppingAdapter
        binding.rvRecents.adapter = recentAdapter
    }

    private fun initObserver() {
        binding.shoppingActionHandler = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.uiState.observe(this) {
            shoppingAdapter.submitList(it.cartProducts)
            recentAdapter.submitList(it.recentProduct) {
                binding.rvRecents.scrollToPosition(0)
            }
        }
        viewModel.errorHandler.observe(
            this,
            EventObserver {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            },
        )
        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is ShoppingNavigation.ToDetail -> {
                        resultLauncher.launch(
                            ProductDetailActivity.createIntent(this, it.cartProduct),
                        )
                    }
                    is ShoppingNavigation.ToCart -> {
                        resultLauncher.launch(
                            CartActivity.createIntent(this),
                        )
                    }
                }
            },
        )
    }

    private fun initLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getParcelableExtraCompat<UpdateUiModel>(
                        EXTRA_UPDATED_PRODUCT,
                    )
                        ?.let {
                            viewModel.updateCartProducts(it)
                        }
                }
                viewModel.loadAllRecent()
                viewModel.loadCartItemCounts()
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        restoreData(intent)
    }

    private fun restoreData(intent: Intent?) {
        intent?.getParcelableExtraCompat<UpdateUiModel>(
            EXTRA_UPDATED_PRODUCT,
        )
            ?.let {
                viewModel.updateCartProducts(it)
            }
    }

    private val spanManager =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (shoppingAdapter.getItemViewType(position) == ShoppingViewType.Product.value) {
                    ShoppingViewType.Product.span
                } else {
                    ShoppingViewType.LoadMore.span
                }
            }
        }

    companion object {
        const val GRIDLAYOUT_COL = 2
        const val EXTRA_UPDATED_PRODUCT = "updatedProduct"

        fun createIntent(
            context: Context,
            updateUiModel: UpdateUiModel,
        ): Intent {
            return Intent(context, ShoppingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(EXTRA_UPDATED_PRODUCT, updateUiModel)
            }
        }
    }
}
