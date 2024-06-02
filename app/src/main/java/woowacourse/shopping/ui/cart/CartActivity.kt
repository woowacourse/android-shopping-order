package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.service.NetworkModule
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter
import woowacourse.shopping.ui.cart.adapter.RecommendProductAdapter
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory
import woowacourse.shopping.ui.products.toUiModel

class CartActivity : AppCompatActivity() {
    private var toast: Toast? = null
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private lateinit var recommendProductAdapter: RecommendProductAdapter
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            ProductRepositoryImpl(ProductRemoteDataSourceImpl(NetworkModule.productService)),
            CartRepositoryImpl(CartRemoteDataSourceImpl(NetworkModule.cartItemService)),
            RecentProductRepositoryImpl.get(
                RecentProductLocalDataSourceImpl(
                    RecentProductDatabase.database().recentProductDao(),
                ),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initToolbar()
        setCartAdapter()
        setRecommendProductAdapter()
        observeCartItems()
        observeRecommendProducts()
        observeError()
    }

    private fun observeRecommendProducts() {
        viewModel.products.observe(this) {
            recommendProductAdapter.submitList(it.map { it.toUiModel() })
        }
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            toast = Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun initToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeCartItems() {
        viewModel.cart.observe(this) {
            adapter.submitList(it.cartItems)
        }
    }

    private fun setCartAdapter() {
        binding.rvCart.itemAnimator = null
        adapter = CartAdapter(viewModel, viewModel)
        binding.rvCart.adapter = adapter
    }

    private fun setRecommendProductAdapter() {
        binding.rvRecommend.rvRecommendProduct.itemAnimator = null
        recommendProductAdapter = RecommendProductAdapter(viewModel)
        binding.rvRecommend.rvRecommendProduct.adapter = recommendProductAdapter
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, CartActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
