package woowacourse.shopping.presentation.product

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import woowacourse.shopping.databinding.ActivityProductBinding
import woowacourse.shopping.databinding.ViewCartActionBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.UiState
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
        binding.vm = viewModel

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
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    showSkeleton(true)
                }

                is UiState.Success -> {
                    showSkeleton(false)
                }

                is UiState.Failure -> {
                    showSkeleton(true)
                    showToast(state.throwable?.message)
                }
            }
        }

        viewModel.products.observe(this) { products ->
            val showLoadMore = viewModel.showLoadMore.value == true
            val list = products.toProductListItems(showLoadMore)
            productAdapter.submitList(list)
        }

        viewModel.cartItemCount.observe(this) { count ->
            _toolbarBinding?.tvCartCount?.apply {
                text = count.toString()
                visibility = if (count > 0) View.VISIBLE else View.GONE
            }
        }

        viewModel.showLoadMore.observe(this) { showLoadMore ->
            val productsState = viewModel.products.value
            val list = productsState?.toProductListItems(showLoadMore)
            productAdapter.submitList(list)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(getString(resId))
        }
    }

    private fun showSkeleton(isLoading: Boolean) {
        if (isLoading) {
            binding.group.visibility = View.GONE
        } else {
            binding.group.visibility = View.VISIBLE
            binding.shimmerLayoutProduct.visibility = View.GONE
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleLoadMoreClick() {
        viewModel.loadMore()
    }

    private fun navigateToCart() {
        val intent = CartActivity.newIntent(this)
        startActivity(intent)
    }

    private fun List<CartItem>.toProductListItems(showLoadMore: Boolean): List<ProductListItem> =
        this.map { ProductListItem.Product(it) } +
            if (showLoadMore) listOf(ProductListItem.LoadMore) else emptyList()

    override fun onClickProductItem(productId: Long) {
        val intent =
            ProductDetailActivity.newIntent(this, productId = productId)
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
