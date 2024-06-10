package woowacourse.shopping.presentation.ui.shopping

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.model.ProductListItem
import woowacourse.shopping.presentation.ui.model.UpdatedProductData
import woowacourse.shopping.presentation.ui.shopping.adapter.ProductListAdapter
import woowacourse.shopping.presentation.ui.shopping.adapter.ShoppingViewType
import woowacourse.shopping.presentation.util.EventObserver

class ShoppingActivity : BindingActivity<ActivityShoppingBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_shopping

    private val viewModel: ShoppingViewModel by viewModels { ShoppingViewModel.Companion.Factory() }

    private val adapter: ProductListAdapter by lazy {
        ProductListAdapter(viewModel)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                activityResult.data?.let {
                    updateSingleProductQuantity(it)
                    updateMultipleProductsQuantities(it)
                }
            }
        }

    private fun updateSingleProductQuantity(intent: Intent) {
        val modifiedProductId = intent.getLongExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, -1L)
        val newQuantity = intent.getIntExtra(ProductDetailActivity.EXTRA_NEW_PRODUCT_QUANTITY, -1)

        viewModel.fetchRecentProducts()
        if (modifiedProductId != -1L && newQuantity != -1) {
            viewModel.updateProductQuantity(modifiedProductId, newQuantity)
        }
    }

    private fun updateMultipleProductsQuantities(intent: Intent) {
        val updatedProducts: List<UpdatedProductData>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(EXTRA_UPDATED_PRODUCTS, UpdatedProductData::class.java)?.toList()
            } else {
                intent.getParcelableArrayExtra(EXTRA_UPDATED_PRODUCTS)?.filterIsInstance<UpdatedProductData>()
            }
        updatedProducts?.forEach { (productId, newQuantity) ->
            viewModel.updateProductQuantity(productId, newQuantity)
        }
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        initAdapter()
        observeRecentProductUpdates()
        observeProductUpdates()
        observeErrorEventUpdates()
        observeMoveEvent()
    }

    private fun initAdapter() {
        val layoutManager = GridLayoutManager(this, GRIDLAYOUT_COL)
        layoutManager.spanSizeLookup = getSpanManager()
        binding.rvShopping.layoutManager = layoutManager
        binding.rvShopping.adapter = adapter
    }

    private fun getSpanManager() =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewTypeValue = adapter.getItemViewType(position)
                return when (ShoppingViewType.of(viewTypeValue)) {
                    ShoppingViewType.RecentProduct -> ShoppingViewType.RecentProduct.span
                    ShoppingViewType.Product -> ShoppingViewType.Product.span
                    ShoppingViewType.LoadMore -> ShoppingViewType.LoadMore.span
                }
            }
        }

    private fun observeRecentProductUpdates() {
        viewModel.recentProducts.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    adapter.updateRecentProductItems(
                        ProductListItem.RecentProductItems(state.data),
                    )
                }

                is UiState.Loading -> {}
            }
        }
    }

    private fun observeProductUpdates() {
        viewModel.shoppingProducts.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    val products = state.data.map { ProductListItem.ShoppingProductItem(it) }
                    adapter.updateProductItems(products)
                }

                UiState.Loading -> {}
            }
        }
    }

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            this,
            EventObserver {
                showToast(it.messageResId)
            },
        )
    }

    private fun observeMoveEvent() {
        viewModel.moveEvent.observe(
            this,
            EventObserver {
                when (it) {
                    is FromShoppingToScreen.ProductDetail -> {
                        ProductDetailActivity.startWithResultLauncher(
                            this,
                            activityResultLauncher,
                            it.productId,
                        )
                    }

                    is FromShoppingToScreen.Cart ->
                        CartActivity.startWithResultLauncher(
                            this,
                            cartItemQuantity = viewModel.cartItemQuantity.value ?: 0,
                            activityResultLauncher,
                        )
                }
            },
        )
    }

    companion object {
        const val GRIDLAYOUT_COL = 2
        private const val EXTRA_PRODUCT_ID = "productId"
        private const val EXTRA_NEW_PRODUCT_QUANTITY = "productQuantity"
        private const val EXTRA_UPDATED_PRODUCTS = "updatedProductsData"

        fun startWithNewProductQuantity(
            context: Context,
            productId: Long = -1L,
            quantity: Int = -1,
        ) {
            if (context is Activity) {
                Intent(context, ShoppingActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(EXTRA_PRODUCT_ID, productId)
                    putExtra(EXTRA_NEW_PRODUCT_QUANTITY, quantity)
                    context.setResult(Activity.RESULT_OK, this)
                }
            } else {
                throw IllegalAccessError("해당 메서드는 액티비티에서 호출해야 합니다")
            }
        }

        fun startWithNewProductQuantities(
            context: Context,
            updatedItems: List<UpdatedProductData>,
        ) {
            if (context is Activity) {
                val intent =
                    Intent(context, ShoppingActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra(EXTRA_UPDATED_PRODUCTS, updatedItems.toTypedArray())
                    }
                context.setResult(Activity.RESULT_OK, intent)
            } else {
                throw IllegalAccessError("해당 메서드는 액티비티에서 호출해야 합니다")
            }
        }

        fun createIntent(context: Context): Intent {
            return Intent(context, ShoppingActivity::class.java)
        }
    }
}
