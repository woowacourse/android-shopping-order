package woowacourse.shopping.presentation.product

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityProductBinding
import woowacourse.shopping.databinding.ViewCartActionBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.productdetail.ProductDetailActivity

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private var _toolbarBinding: ViewCartActionBinding? = null
    private val toolbarBinding get() = _toolbarBinding!!
    private val viewModel: ProductViewModel by viewModels { ProductViewModelFactory() }
    private val recentProductAdapter: RecentProductAdapter by lazy {
        RecentProductAdapter(viewModel)
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(
            onClickLoadMore = ::handleLoadMoreClick,
            cartCounterClickListener = viewModel,
            itemClickListener = viewModel,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product)
        binding.lifecycleOwner = this

        showSkeleton(true)

        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)

        val menuItem = menu?.findItem(R.id.action_cart)
        _toolbarBinding = ViewCartActionBinding.inflate(layoutInflater)
        menuItem?.actionView = toolbarBinding.root

        toolbarBinding.ivCart.setOnClickListener {
            navigateToCart()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_cart -> {
                navigateToCart()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clProduct) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbProduct)
    }

    private fun initAdapter() {
        binding.rvProducts.apply {
            layoutManager =
                GridLayoutManager(context, 2).apply {
                    spanSizeLookup = createSpanSizeLookup()
                }
            itemAnimator = null
            adapter = productAdapter
        }
        binding.rvRecentProducts.apply {
            itemAnimator = null
            adapter = recentProductAdapter
        }
    }

    private fun createSpanSizeLookup(): GridLayoutManager.SpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                when (productAdapter.getItemViewType(position)) {
                    R.layout.item_product -> 1
                    else -> 2
                }
        }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showSkeleton(true)
                }

                is ResultState.Success -> {
                    showSkeleton(false)
                }

                is ResultState.Failure -> {
                    showSkeleton(true)
                }
            }
        }

        viewModel.products.observe(this) { productItemTypes ->
            productAdapter.submitList(productItemTypes)
        }

        viewModel.recentProducts.observe(this) { recentProducts ->
            recentProductAdapter.submitList(recentProducts)
        }

        viewModel.cartItemCount.observe(this) { count ->
            _toolbarBinding?.tvCartCount?.apply {
                text = count.toString()
                visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }

        viewModel.navigateTo.observe(this) { productId ->
            val intent =
                ProductDetailActivity.newIntent(this, productId = productId)
            productDetailLauncher.launch(intent)
        }
    }

    private val productDetailLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedProductId = result.data?.getLongExtra(Extra.KEY_PRODUCT_ID, 0L) ?: 0L
                val updateAddQuantity =
                    result.data?.getIntExtra(Extra.KEY_PRODUCT_ADD_QUANTITY, 0) ?: 0

                viewModel.updateCartItem(updatedProductId, updateAddQuantity)
            }
        }

    private fun showSkeleton(isLoading: Boolean) {
        if (isLoading) {
            binding.rvProducts.visibility = View.GONE
            binding.rvRecentProducts.visibility = View.GONE
            binding.tvRecentProduct.visibility = View.GONE
            binding.divider.visibility = View.GONE
        } else {
            binding.rvProducts.visibility = View.VISIBLE
            binding.rvRecentProducts.visibility = View.VISIBLE
            binding.tvRecentProduct.visibility = View.VISIBLE
            binding.divider.visibility = View.VISIBLE

            binding.shimmerLayoutProduct.visibility = View.GONE
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun handleLoadMoreClick() {
        viewModel.loadMore()
    }

    private fun navigateToCart() {
        val intent = CartActivity.newIntent(this)
        cartLauncher.launch(intent)
    }

    private val cartLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val isUpdated = result.data?.getBooleanExtra(Extra.KEY_CART_IS_UPDATE, true) ?: true
                if (isUpdated) {
                    viewModel.fetchData()
                    viewModel.fetchCartItemCount()
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _toolbarBinding = null
    }
}
