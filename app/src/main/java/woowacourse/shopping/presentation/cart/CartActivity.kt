package woowacourse.shopping.presentation.cart

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)

        setupWindowInsets()
        setupToolbar()
        initBinding()
        initRecyclerView()
        observeCartProducts()
        observePagination()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.buttonOrder.setOnClickListener {
            val intent =
                RecommendActivity.newIntent(
                    this@CartActivity,
                    viewModel.totalOrderPrice.value!!,
                    viewModel.checkedProductCount.value!!
                )
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_cart_action_bar)
        }
    }

    private fun initRecyclerView() {
        val handler = CartEventHandlerImpl(viewModel)
        binding.recyclerViewCart.adapter =
            CartAdapter(
                cartProducts = emptyList(),
                cartHandler = handler,
                quantityHandler = handler,
            )
    }

    private fun observeCartProducts() {
        val adapter = getCartAdapter()

        viewModel.pagingData.observe(this) {
            binding.frCartShimmer.stopShimmer()
            binding.frCartShimmer.visibility = View.GONE
            binding.recyclerViewCart.visibility = View.VISIBLE

            adapter.setData(it.products, it)
        }

        viewModel.product.observe(this) {
            adapter.updateProduct(it)
        }
    }

    private fun observePagination() {
        viewModel.pageEvent.observe(this) {
            val adapter = getCartAdapter()
            val paginationPos = adapter.itemCount - 1
            adapter.notifyItemChanged(paginationPos)
        }
    }

    private fun getCartAdapter(): CartAdapter = binding.recyclerViewCart.adapter as CartAdapter

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }
}
