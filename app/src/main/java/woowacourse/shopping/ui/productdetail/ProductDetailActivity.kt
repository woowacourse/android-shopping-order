package woowacourse.shopping.ui.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartItemRemoteService
import woowacourse.shopping.data.cart.CartItemRepositoryImpl
import woowacourse.shopping.data.database.DbHelper
import woowacourse.shopping.data.product.ProductRemoteService
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductMemoryDao
import woowacourse.shopping.data.recentlyviewedproduct.RecentlyViewedProductRepositoryImpl
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState
import woowacourse.shopping.utils.PRICE_FORMAT
import woowacourse.shopping.utils.ServerConfiguration
import woowacourse.shopping.utils.customview.AddToCartDialog

class ProductDetailActivity : AppCompatActivity(), ProductDetailContract.View {
    private val binding: ActivityProductDetailBinding by lazy {
        ActivityProductDetailBinding.inflate(layoutInflater)
    }
    private val presenter: ProductDetailContract.Presenter by lazy {
        ProductDetailPresenter(
            this, ProductRepositoryImpl(ProductRemoteService(ServerConfiguration.host)),
            CartItemRepositoryImpl(
                CartItemRemoteService(ServerConfiguration.host)
            ),
            RecentlyViewedProductRepositoryImpl(
                RecentlyViewedProductMemoryDao(
                    DbHelper.getDbInstance(this)
                ),
                ProductRemoteService(ServerConfiguration.host)
            )
        )
    }
    private val lastViewedProductViewHolder: LastViewedProductViewHolder by lazy {
        LastViewedProductViewHolder(binding) { startActivityFromProductDetailActivity(this, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        presenter.onLoadProduct(intent.getLongExtra(PRODUCT_ID, -1))
        if (intent.getBooleanExtra(FROM_PRODUCT_DETAIL_ACTIVITY, false).not()) {
            initLastViewedProduct()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarProductDetail)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initLastViewedProduct() {
        presenter.onLoadLastViewedProduct()
    }

    override fun setProduct(product: ProductDetailUIState) {
        runOnUiThread {
            Glide.with(this).load(product.imageUrl).into(binding.ivProductDetail)

            binding.tvProductDetailName.text = product.name
            binding.tvProductDetailPrice.text =
                getString(R.string.product_price).format(PRICE_FORMAT.format(product.price))

            if (product.isInCart) {
                binding.btnProductDetailAdd.isEnabled = false
                binding.btnProductDetailAdd.setBackgroundColor(getColor(R.color.grey_aaa))
            }

            binding.btnProductDetailAdd.setOnClickListener {
                AddToCartDialog(product) { productId, count ->
                    presenter.onAddProductToCart(productId, count)
                }.show(supportFragmentManager, TAG_ADD_TO_CART_DIALOG)
            }
        }
    }

    override fun setLastViewedProduct(product: LastViewedProductUIState?) {
        runOnUiThread {
            lastViewedProductViewHolder.bind(product)
        }
    }

    override fun showCartView() {
        finish()
        CartActivity.startActivity(this, true)
    }

    companion object {
        private const val PRODUCT_ID = "PRODUCT_ID"
        private const val FROM_PRODUCT_DETAIL_ACTIVITY = "FROM_PRODUCT_DETAIL_ACTIVITY"
        private const val TAG_ADD_TO_CART_DIALOG = "TAG_ADD_TO_CART_DIALOG"

        fun startActivity(context: Context, productId: Long) {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
            }
            context.startActivity(intent)
        }

        fun startActivityFromProductDetailActivity(context: Context, productId: Long) {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
                putExtra(FROM_PRODUCT_DETAIL_ACTIVITY, true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }
}
