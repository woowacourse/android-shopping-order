package woowacourse.shopping.feature.goods

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityGoodsBinding
import woowacourse.shopping.databinding.MenuCartActionViewBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.goods.adapter.GoodsAdapter
import woowacourse.shopping.feature.goods.adapter.GoodsClickListener
import woowacourse.shopping.feature.goods.adapter.GoodsSpanSizeLookup
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity

class GoodsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsBinding
    private lateinit var adapter: GoodsAdapter
    private val viewModel: GoodsViewModel by viewModels {
        val app = (application as ShoppingApplication)
        GoodsViewModelFactory(
            app.historyRepository,
            app.productRepository,
            app.cartRepository,
        )
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.syncData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupAdapter()
        setupRecyclerView()
        observeViewModel()
        observeCartInsertResult()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_cart, menu)
        val menuItem = menu.findItem(R.id.nav_cart)
        val binding = MenuCartActionViewBinding.inflate(layoutInflater)
        menuItem.actionView = binding.root

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.root.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_cart -> {
                val intent = CartActivity.newIntent(this)
                activityResultLauncher.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = GoodsSpanSizeLookup(adapter)
        binding.rvGoods.layoutManager = gridLayoutManager
        binding.rvGoods.adapter = adapter
        binding.rvGoods.itemAnimator = null
    }

    private fun observeViewModel() {
        viewModel.goodsItems.observe(this) { goods ->
            adapter.addItems(goods)
        }
        viewModel.navigateToCart.observe(this) { cart ->
            navigate(cart)
        }
    }

    private fun observeCartInsertResult() {
        viewModel.isSuccess.observe(this) { cart ->
            Toast
                .makeText(
                    this,
                    R.string.goods_detail_cart_insert_success_toast_message,
                    Toast.LENGTH_SHORT,
                ).show()
        }
        viewModel.isFail.observe(this) {
            Toast
                .makeText(
                    this,
                    R.string.goods_detail_cart_insert_fail_toast_message,
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    private fun setupAdapter() {
        adapter =
            GoodsAdapter(
                object : GoodsClickListener {
                    override fun onClickGoods(productId: Int) {
                        navigate(productId)
                    }

                    override fun onClickHistory(productId: Int) {
                        viewModel.findCartFromHistory(productId)
                    }

                    override fun addToCart(productId: Int) {
                        viewModel.addToCart(productId)
                    }

                    override fun increaseQuantity(cart: GoodsProduct) {
                        viewModel.increaseQuantity(cart)
                    }

                    override fun decreaseQuantity(cart: GoodsProduct) {
                        viewModel.decreaseQuantity(cart)
                    }

                    override fun loadMore() {
                        viewModel.addPage()
                    }
                },
            )
    }

    private fun navigate(productId: Int) {
        val intent = GoodsDetailsActivity.newIntent(this, productId.toLong())
        activityResultLauncher.launch(intent)
    }
}
