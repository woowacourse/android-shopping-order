package woowacourse.shopping.ui.detailedProduct

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.local.CartDefaultLocalDataSource
import woowacourse.shopping.data.local.product.ProductSqliteDataSource
import woowacourse.shopping.data.local.recentProduct.RecentSqliteProductDataSource
import woowacourse.shopping.data.remote.CartRetrofitDataSource
import woowacourse.shopping.data.remote.ProductRetrofitDataSource
import woowacourse.shopping.data.repository.CartDefaultRepository
import woowacourse.shopping.data.repository.ProductDefaultRepository
import woowacourse.shopping.data.repository.RecentDefaultRepository
import woowacourse.shopping.databinding.ActivityDetailedProductBinding
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.ui.cart.CartActivity
import woowacourse.shopping.ui.cart.cartDialog.CartDialog
import woowacourse.shopping.utils.SharedPreferenceUtils

class DetailedProductActivity : AppCompatActivity(), DetailedProductContract.View {
    private lateinit var binding: ActivityDetailedProductBinding
    private lateinit var presenter: DetailedProductContract.Presenter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initToolbar()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed_product)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun initPresenter() {
        presenter = DetailedProductPresenter(
            this,
            SharedPreferenceUtils(this),
            productRepository = ProductDefaultRepository(
                localDataSource = ProductSqliteDataSource(this),
                remoteDataSource = ProductRetrofitDataSource()
            ),
            cartRepository = CartDefaultRepository(
                localDataSource = CartDefaultLocalDataSource(),
                remoteDataSource = CartRetrofitDataSource()
            ),
            recentRepository = RecentDefaultRepository(
                localDataSource = RecentSqliteProductDataSource(this)
            ),
            intent.getIntExtra(KEY_PRODUCT_ID, -1)
        )
    }

    private fun initView() {
        binding.presenter = presenter
        presenter.fetchLastProduct()
        presenter.fetchProductDetail()
        presenter.addProductToRecent()
    }

    override fun setProductDetail(product: ProductUIModel, lastProduct: ProductUIModel?) {
        binding.product = product
        binding.lastProduct = lastProduct
    }

    override fun navigateToCart() {
        startActivity(CartActivity.getIntent(this))
    }

    override fun navigateToDetailedProduct(productId: Int) {
        startActivity(
            getIntent(this, productId).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }

    override fun showCartDialog(product: ProductUIModel) {
        CartDialog(this, product.name, product.price) { count ->
            presenter.addProductToCart(count)
        }.apply {
            val density = resources.displayMetrics.density * 1.2
            window?.setLayout((314 * density).toInt(), (150 * density).toInt())
            show()
        }
    }

    companion object {
        private const val KEY_PRODUCT_ID = "KEY_PRODUCT_ID"

        fun getIntent(context: Context, productId: Int): Intent {
            return Intent(context, DetailedProductActivity::class.java).apply {
                putExtra(KEY_PRODUCT_ID, productId)
            }
        }
    }
}