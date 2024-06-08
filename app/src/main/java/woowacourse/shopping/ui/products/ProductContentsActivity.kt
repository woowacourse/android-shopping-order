package woowacourse.shopping.ui.products

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
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.ProductRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.data.service.NetworkModule
import woowacourse.shopping.databinding.ActivityProductContentsBinding
import woowacourse.shopping.exception.handleError
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.detail.ProductDetailActivity
import woowacourse.shopping.ui.products.adapter.ProductAdapter
import woowacourse.shopping.ui.products.adapter.RecentProductAdapter
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModel
import woowacourse.shopping.ui.products.viewmodel.ProductContentsViewModelFactory

class ProductContentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductContentsBinding
    private var toast: Toast? = null
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recentProductAdapter: RecentProductAdapter
    private val viewModel: ProductContentsViewModel by viewModels {
        ProductContentsViewModelFactory(
            ProductRepositoryImpl(ProductRemoteDataSourceImpl(NetworkModule.productService)),
            RecentProductRepositoryImpl.get(
                RecentProductLocalDataSourceImpl(
                    RecentProductDatabase.database().recentProductDao(),
                ),
            ),
            CartRepositoryImpl(
                CartRemoteDataSourceImpl(NetworkModule.cartItemService),
                OrderRemoteDataSourceImpl(NetworkModule.orderService),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setProductAdapter()
        setRecentProductAdapter()
        initToolbar()
        observeProductItems()
        observeRecentProductItems()
        observeError()
        moveToProductDetailPage()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItems()
        viewModel.loadRecentProducts()
    }

    private fun observeError() {
        viewModel.error.observe(this) {
            val errMsg = handleError(it)
            toast = Toast.makeText(this, errMsg, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    private fun initToolbar() {
        binding.ivCart.setOnClickListener {
            CartActivity.startActivity(this)
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_contents)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setProductAdapter() {
        binding.rvProducts.itemAnimator = null
        productAdapter = ProductAdapter(viewModel, viewModel, viewModel)
        binding.rvProducts.adapter = productAdapter
    }

    private fun setRecentProductAdapter() {
        binding.rvRecentProducts.itemAnimator = null
        recentProductAdapter = RecentProductAdapter(viewModel)
        binding.rvRecentProducts.adapter = recentProductAdapter
    }

    private fun observeProductItems() {
        viewModel.productWithQuantity.observe(this) {
            if (it.isLoading) {
                val items = productAdapter.currentList.isLoading()
                productAdapter.submitList(items)
                return@observe
            }
            productAdapter.submitList(it.productWithQuantities)
        }
    }

    private fun observeRecentProductItems() {
        viewModel.recentProducts.observe(this) {
            recentProductAdapter.submitList(it)
        }
    }

    private fun moveToProductDetailPage() {
        viewModel.productDetailId.observe(this) {
            ProductDetailActivity.startActivity(this, it, true)
        }
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, ProductContentsActivity::class.java).run {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(this)
            }
    }
}
