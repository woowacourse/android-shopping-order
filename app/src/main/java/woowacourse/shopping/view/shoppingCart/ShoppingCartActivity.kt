package woowacourse.shopping.view.shoppingCart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.shoppingCartRecommend.ShoppingCartRecommendActivity

class ShoppingCartActivity :
    AppCompatActivity(),
    ShoppingCartProductAdapter.ShoppingCartListener,
    ShoppingCartClickListener {
    private val viewModel: ShoppingCartViewModel by viewModels()

    private val binding: ActivityShoppingCartBinding by lazy {
        ActivityShoppingCartBinding.inflate(layoutInflater)
    }
    private val shoppingCartProductAdapter by lazy {
        ShoppingCartProductAdapter(this)
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            when (result.resultCode) {
                ResultFrom.RECOMMEND_PRODUCT_BACK.RESULT_OK -> viewModel.reload()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shoppingCartRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initDataBinding()
        setupAdapter()
        setupObservers()
    }

    private fun initDataBinding() {
        binding.adapter = shoppingCartProductAdapter
        binding.viewModel = this.viewModel
        binding.lifecycleOwner = this@ShoppingCartActivity
        binding.shoppingCartListener = this@ShoppingCartActivity
    }

    private fun setupAdapter() {
        binding.shoppingCartProducts.apply {
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int,
                    ) {
                        super.onScrolled(recyclerView, dx, dy)

                        val totalItemCount: Int =
                            recyclerView.adapter?.itemCount ?: throw IllegalStateException()
                        val visibleLastItemPosition: Int =
                            (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                        if (visibleLastItemPosition in totalItemCount - 1..totalItemCount) {
                            viewModel.plusPage()
                        }
                    }
                },
            )
        }
    }

    private fun setupObservers() {
        viewModel.shoppingCart.observe(this) { shoppingCart ->
            shoppingCartProductAdapter.submitList(shoppingCart)
        }

        viewModel.orderEvent.observe(this) { event ->
            when (event) {
                OrderEvent.PROCEED -> {
                    activityResultLauncher.launch(
                        ShoppingCartRecommendActivity.newIntent(
                            this,
                            viewModel.shoppingCartProductsToOrder.toTypedArray(),
                        ),
                    )
                }

                OrderEvent.ABORT -> Unit
            }
        }
    }

    override fun onRemoveButton(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        viewModel.removeShoppingCartProduct(shoppingCartProductItem)
    }

    override fun onProductSelectedButton(
        shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem,
        isSelected: Boolean,
    ) {
        viewModel.selectShoppingCartProduct(shoppingCartProductItem, isSelected)
    }

    override fun onPlusShoppingCartClick(quantityObservable: QuantityObservable) {
        viewModel.increaseQuantity(quantityObservable as ShoppingCartItem.ShoppingCartProductItem)
    }

    override fun onMinusShoppingCartClick(quantityObservable: QuantityObservable) {
        viewModel.decreaseQuantity(quantityObservable as ShoppingCartItem.ShoppingCartProductItem)
    }

    override fun onBackButtonClick() {
        val intent =
            Intent().apply {
                putExtra("updateProducts", viewModel.hasUpdatedProducts.value)
            }
        setResult(ResultFrom.SHOPPING_CART_BACK.RESULT_OK, intent)
        finish()
    }

    override fun onAllSelectedButtonClick() {
        viewModel.selectAllShoppingCartProducts()
    }

    override fun onOrderButtonClick() {
        viewModel.checkoutIfPossible()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ShoppingCartActivity::class.java)
    }
}
