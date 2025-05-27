package woowacourse.shopping.ui.catalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCatalogBinding
import woowacourse.shopping.databinding.LayoutCatalogCartQuantityBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.catalog.adapter.history.HistoryProductAdapter
import woowacourse.shopping.ui.catalog.adapter.product.CatalogAdapter
import woowacourse.shopping.ui.catalog.adapter.product.CatalogAdapter.OnClickHandler
import woowacourse.shopping.ui.catalog.adapter.product.CatalogLayoutManager
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.model.ActivityResult
import woowacourse.shopping.ui.productdetail.ProductDetailActivity

class CatalogActivity : DataBindingActivity<ActivityCatalogBinding>(R.layout.activity_catalog) {
    private val viewModel: CatalogViewModel by viewModels { CatalogViewModel.Factory }
    private val catalogAdapter: CatalogAdapter = CatalogAdapter(createAdapterOnClickHandler())
    private val historyProductAdapter: HistoryProductAdapter = HistoryProductAdapter { id -> navigateToProductDetail(id) }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewBinding()
        initObservers()
        initActivityResultLauncher()
        viewModel.loadCartProducts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHistoryProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_catalog, menu)

        val binding =
            DataBindingUtil.inflate<LayoutCatalogCartQuantityBinding>(
                LayoutInflater.from(this),
                R.layout.layout_catalog_cart_quantity,
                null,
                false,
            )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        menu?.findItem(R.id.itemCart)?.actionView = binding.root
        binding.root.setOnClickListener { navigateToCart() }

        return true
    }

    private fun createAdapterOnClickHandler() =
        object : OnClickHandler {
            override fun onProductClick(id: Int) {
                navigateToProductDetail(id)
            }

            override fun onIncreaseClick(id: Int) {
                viewModel.increaseCartProduct(id)
            }

            override fun onDecreaseClick(id: Int) {
                viewModel.decreaseCartProduct(id)
            }

            override fun onLoadMoreClick() {
                viewModel.loadCartProducts()
            }
        }

    private fun navigateToCart() {
        val intent = CartActivity.newIntent(this)
        activityResultLauncher.launch(intent)
    }

    private fun navigateToProductDetail(
        id: Int,
        isRecentHistoryProductShown: Boolean = true,
    ) {
        val intent = ProductDetailActivity.newIntent(this, id, isRecentHistoryProductShown)
        activityResultLauncher.launch(intent)
    }

    private fun initViewBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initProductsView()
        initHistoryProductsView()
    }

    private fun initProductsView() {
        binding.productItemsContainer.adapter = catalogAdapter
        binding.productItemsContainer.layoutManager = CatalogLayoutManager(this, catalogAdapter)
        binding.productItemsContainer.itemAnimator = null
    }

    private fun initHistoryProductsView() {
        binding.productsHistoryItemsContainer.adapter = historyProductAdapter
        binding.productsHistoryItemsContainer.itemAnimator = null
    }

    private fun initObservers() {
        viewModel.catalogProducts.observe(this) { products ->
            catalogAdapter.submitItems(products.products, products.hasMore)
        }

        viewModel.historyProducts.observe(this) { products ->
            historyProductAdapter.submitItems(products)
        }
    }

    private fun initActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                when (result.resultCode) {
                    ActivityResult.PRODUCT_DETAIL_HISTORY_PRODUCT_CLICKED.code ->
                        navigateToProductDetail(
                            result.data?.getIntExtra(ActivityResult.PRODUCT_DETAIL_HISTORY_PRODUCT_CLICKED.key, 0) ?: 0,
                            false,
                        )

                    ActivityResult.PRODUCT_DETAIL_CART_UPDATED.code ->
                        viewModel.loadCartProduct(
                            result.data?.getIntExtra(ActivityResult.PRODUCT_DETAIL_CART_UPDATED.key, 0) ?: 0,
                        )

                    ActivityResult.CART_PRODUCT_EDITED.code ->
                        viewModel.loadCartProducts(
                            result.data?.getIntegerArrayListExtra(ActivityResult.CART_PRODUCT_EDITED.key)?.toList() ?: emptyList(),
                        )
                }
            }
    }
}
