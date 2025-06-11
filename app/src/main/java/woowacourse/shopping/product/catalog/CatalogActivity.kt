package woowacourse.shopping.product.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.cart.ButtonEvent
import woowacourse.shopping.cart.CartActivity
import woowacourse.shopping.databinding.ActivityCatalogBinding
import woowacourse.shopping.databinding.MenuCartLayoutBinding
import woowacourse.shopping.product.detail.DetailActivity

class CatalogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCatalogBinding
    private val viewModel: CatalogViewModel by viewModels { CatalogViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog)
        binding.lifecycleOwner = this
        applyWindowInsets()

        setProductAdapter()
        setProductsRecyclerViewLayoutManager()
        setRecentlyViewedProductAdapter()
        observeCatalogProducts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCatalogUntilCurrentPage()
        viewModel.loadCartItemSize()
        viewModel.loadRecentlyViewedProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu_item, menu)
        setupCartMenuItem(menu)
        return true
    }

    private fun setupCartMenuItem(menu: Menu?) {
        val menuItem = menu?.findItem(R.id.action_cart) ?: return

        val binding =
            DataBindingUtil
                .inflate<MenuCartLayoutBinding>(
                    LayoutInflater.from(this),
                    R.layout.menu_cart_layout,
                    null,
                    false,
                ).apply {
                    layoutCartMenu.setOnClickListener {
                        startActivity(CartActivity.newIntent(this@CatalogActivity))
                    }
                    vm = viewModel
                    lifecycleOwner = this@CatalogActivity
                }

        menuItem.actionView = binding.root
    }

    private fun setProductAdapter() {
        val adapter =
            ProductAdapter(
                productActionListener =
                    object : ProductActionListener {
                        override fun onProductClick(product: ProductUiModel) {
                            val intent = DetailActivity.newIntent(this@CatalogActivity, product)
                            startActivity(intent)
                        }

                        override fun onLoadButtonClick() = viewModel.loadNextCatalogProducts()
                    },
                quantityControlListener =
                    object : QuantityControlListener {
                        override fun onClick(
                            buttonEvent: ButtonEvent,
                            product: ProductUiModel,
                        ) = when (buttonEvent) {
                            ButtonEvent.INCREASE -> viewModel.increaseQuantity(product)
                            ButtonEvent.DECREASE -> viewModel.decreaseQuantity(product)
                        }

                        override fun onAdd(product: ProductUiModel) = viewModel.increaseQuantity(product)
                    },
            )

        binding.recyclerViewProducts.adapter = adapter
    }

    private fun setProductsRecyclerViewLayoutManager() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    spanSizeByPosition(
                        position,
                        binding.recyclerViewProducts.adapter?.itemCount ?: 0,
                    )
            }
        binding.recyclerViewProducts.layoutManager = gridLayoutManager
    }

    private fun setRecentlyViewedProductAdapter() {
        val adapter =
            RecentlyViewedProductAdapter {
                val intent = DetailActivity.newIntent(this@CatalogActivity, it)
                startActivity(intent)
            }
        binding.recyclerViewRecentlyViewedProducts.adapter = adapter
    }

    private fun observeCatalogProducts() {
        val productsAdapter = binding.recyclerViewProducts.adapter as ProductAdapter
        val recentProductsAdapter: RecentlyViewedProductAdapter =
            binding.recyclerViewRecentlyViewedProducts.adapter as RecentlyViewedProductAdapter

        viewModel.loadedCatalogItems.observe(this) {
            productsAdapter.submitList(it)
        }
        viewModel.updatedItem.observe(this, productsAdapter::updateItem)
        viewModel.recentlyViewedProducts.observe(this, recentProductsAdapter::submitList)
        viewModel.loadingState.observe(this) { changeShimmerState(it.isLoading) }
        viewModel.errorMessage.observe(this) { showToast(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
    }

    private fun spanSizeByPosition(
        position: Int,
        itemCount: Int,
    ): Int = if (position == itemCount - 1 && itemCount % 20 == 1) 2 else 1

    private fun changeShimmerState(isLoading: Boolean) {
        when (isLoading) {
            true -> {
                binding.shimmerFrameLayoutProducts.startShimmer()
                binding.shimmerFrameLayoutProducts.visibility = View.VISIBLE
            }

            false -> {
                binding.shimmerFrameLayoutProducts.stopShimmer()
                binding.shimmerFrameLayoutProducts.visibility = View.GONE
            }
        }
    }
}
