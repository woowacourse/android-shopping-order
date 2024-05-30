package woowacourse.shopping.ui.detail

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.recentproduct.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.ui.detail.viewmodel.ProductDetailViewModel
import woowacourse.shopping.ui.detail.viewmodel.ProductDetailViewModelFactory

class ProductDetailActivity :
    AppCompatActivity(),
    MostRecentProductClickListener {
    private lateinit var binding: ActivityProductDetailBinding
    private var toast: Toast? = null
    private val productId by lazy { productId() }
    private val viewModel: ProductDetailViewModel by viewModels {
        ProductDetailViewModelFactory(
            productId,
            ProductRepositoryImpl(),
            RecentProductRepositoryImpl.get(RecentProductDatabase.database().recentProductDao()),
            CartRepositoryImpl(),
        )
    }
    private val lastSeenProductState by lazy { lastSeenProductState() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initToolbar()
        setOnListener()
        observeErrorMessage()
        addRecentProduct()
        observeAddCart()
    }

    override fun onClickMostRecentProduct() {
        viewModel.mostRecentProduct.observe(this) {
            moveToMostRecentProductDetail(this, it.id, false)
        }
    }

    private fun observeAddCart() {
        viewModel.addCartComplete.observe(this) {
            toast?.cancel()
            toast = Toast.makeText(this, getString(R.string.add_cart_complete), Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    private fun addRecentProduct() {
        viewModel.addToRecentProduct(lastSeenProductState)
    }

    private fun initToolbar() {
        binding.toolbarDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_close -> finish()
            }
            false
        }
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setOnListener() {
        binding.mostRecentProductClickListener = this
    }

    private fun observeErrorMessage() {
        viewModel.errorMsg.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.isNotEmpty()) {
                    toast = Toast.makeText(this, it, Toast.LENGTH_SHORT)
                    toast?.show()
                }
            }
        }
    }

    private fun productId() =
        intent.getLongExtra(
            ProductDetailKey.EXTRA_PRODUCT_KEY,
            EXTRA_DEFAULT_VALUE,
        )

    private fun lastSeenProductState() =
        intent.getBooleanExtra(
            ProductDetailKey.EXTRA_LAST_SEEN_PRODUCT_KEY,
            false,
        )

    companion object {
        private const val EXTRA_DEFAULT_VALUE = -1L

        fun startActivity(
            context: Context,
            productId: Long,
            lastSeenProductState: Boolean,
        ) = Intent(context, ProductDetailActivity::class.java).run {
            putExtra(ProductDetailKey.EXTRA_PRODUCT_KEY, productId)
            putExtra(ProductDetailKey.EXTRA_LAST_SEEN_PRODUCT_KEY, lastSeenProductState)
            context.startActivity(this)
        }

        fun moveToMostRecentProductDetail(
            context: Context,
            productId: Long,
            lastSeenProductState: Boolean,
        ) = Intent(context, ProductDetailActivity::class.java).run {
            putExtra(ProductDetailKey.EXTRA_PRODUCT_KEY, productId)
            putExtra(ProductDetailKey.EXTRA_LAST_SEEN_PRODUCT_KEY, lastSeenProductState)
            setFlags(FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(this)
        }
    }
}