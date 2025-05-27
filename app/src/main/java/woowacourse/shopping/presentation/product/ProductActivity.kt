package woowacourse.shopping.presentation.product

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.ResultState
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.presentation.cart.CartCounterClickListener
import woowacourse.shopping.presentation.productdetail.ProductDetailActivity

class ProductActivity :
    AppCompatActivity(),
    CartCounterClickListener,
    ItemClickListener {
    private lateinit var binding: ActivityProductBinding
    private var _toolbarBinding: ViewCartActionBinding? = null
    private val toolbarBinding get() = _toolbarBinding!!
    private val viewModel: ProductViewModel by viewModels { ProductViewModelFactory() }
    private val recentAdapter: RecentAdapter by lazy {
        RecentAdapter(this)
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(
            onClickLoadMore = ::handleLoadMoreClick,
            cartCounterClickListener = this,
            itemClickListener = this,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product)
        binding.lifecycleOwner = this

        showSampleData(true)

        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.fetchData()
        viewModel.fetchCartItemCount()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)

        val menuItem = menu?.findItem(R.id.action_cart)
        _toolbarBinding = ViewCartActionBinding.inflate(layoutInflater)
        menuItem?.actionView = toolbarBinding.root

        toolbarBinding.ivCart.setOnClickListener {
            navigateToCart()
        }

        viewModel.fetchCartItemCount()

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
            adapter = recentAdapter
        }
    }

    private fun createSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val isLastItem = position == productAdapter.itemCount - 1
                val shouldExpand = viewModel.showLoadMore.value == true
                return if (isLastItem && shouldExpand) 2 else 1
            }
        }
    }

    private fun observeViewModel() {
        viewModel.products.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    binding.root.postDelayed({
                        showSampleData(false)
                    }, 1_000L)

                    val showLoadMore = viewModel.showLoadMore.value == true
                    productAdapter.setData(result.data, showLoadMore)
                }

                is ResultState.Failure -> {
                    showToast(R.string.product_toast_load_failure)
                }
            }
        }

        viewModel.recentProducts.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    recentAdapter.submitList(result.data)
                }

                is ResultState.Failure -> {
                    showToast(R.string.product_toast_load_failure)
                }
            }
        }

        viewModel.cartItemCount.observe(this) { count ->
            _toolbarBinding?.tvCartCount?.apply {
                text = count.toString()
                visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }

        viewModel.showLoadMore.observe(this) { showLoadMore ->
            val productsState = viewModel.products.value
            if (productsState is ResultState.Success) {
                productAdapter.setData(productsState.data, showLoadMore)
            }
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showSampleData(isLoading: Boolean) {
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
        startActivity(intent)
    }

    override fun onClickProductItem(productId: Long) {
        val intent = ProductDetailActivity.newIntent(this, productId)
        startActivity(intent)
    }

    override fun onClickAddToCart(cartItem: CartItem) {
        viewModel.addToCart(cartItem)
    }

    override fun onClickMinus(id: Long) {
        viewModel.decreaseQuantity(id)
    }

    override fun onClickPlus(id: Long) {
        viewModel.increaseQuantity(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        _toolbarBinding = null
    }
}
