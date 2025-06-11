package woowacourse.shopping.presentation.cart

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.cart.event.CartEventHandlerImpl
import woowacourse.shopping.presentation.recommend.RecommendActivity

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModel.FACTORY
    }

    private lateinit var cartAdapter: CartAdapter

    private val cartLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val checkedIds = result.data?.getLongArrayExtra("checked_product_ids")?.toList() ?: emptyList()
                viewModel.restoreCheckedProducts(checkedIds)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

        setupWindowInsets()
        setupToolbar()
        initBinding()
        initRecyclerView()
        observeCartViewModel()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_cart_action_bar)
        }
    }

    private fun initRecyclerView() {
        val handler = CartEventHandlerImpl(viewModel)
        cartAdapter =
            CartAdapter(
                cartHandler = handler,
                handler = handler,
            )
        binding.recyclerViewCart.adapter = cartAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeCartViewModel() {
        viewModel.pagingData.observe(this) { pagingData ->
            val items = mutableListOf<CartAdapterItem>()

            items.addAll(pagingData.products.map { CartAdapterItem.Product(it) })

            if (pagingData.hasNext || pagingData.hasPrevious) {
                items.add(
                    CartAdapterItem.PaginationButton(
                        hasPrevious = pagingData.hasPrevious,
                        hasNext = pagingData.hasNext,
                    ),
                )
            }

            cartAdapter.submitList(items)
        }

        viewModel.product.observe(this) { productUiModel ->
            cartAdapter.updateProduct(CartAdapterItem.Product(productUiModel))
        }

        viewModel.navigateToRecommendEvent.observe(this) { orderInfo ->
            val intent =
                RecommendActivity.newIntent(
                    this@CartActivity,
                    ArrayList(orderInfo.checkedItems),
                )
            cartLauncher.launch(intent)
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
