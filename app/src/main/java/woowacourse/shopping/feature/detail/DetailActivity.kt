package woowacourse.shopping.feature.detail

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.local.CartRepositoryImpl
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.ProductRepositoryImpl
import woowacourse.shopping.data.service.cart.CartRemoteService
import woowacourse.shopping.data.service.product.ProductRemoteService
import woowacourse.shopping.data.sql.recent.RecentDao
import woowacourse.shopping.databinding.ActivityDetailBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.detail.dialog.CounterDialog
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel
import woowacourse.shopping.util.getParcelableCompat

class DetailActivity : AppCompatActivity(), DetailContract.View {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var presenter: DetailContract.Presenter

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            presenter.exit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this

        val productId = intent.getLongExtra(PRODUCT_KEY, -1)
        val recentProductUiModel =
            intent.getParcelableCompat<RecentProductUiModel>(RECENT_PRODUCT_KEY)

        initPresenter(productId, recentProductUiModel)

        onBackPressedDispatcher.addCallback(this, callback)
        setFragmentResultListener()
    }

    private fun initPresenter(productId: Long, recentProductUiModel: RecentProductUiModel?) {
        presenter = DetailPresenter(
            this,
            ProductRepositoryImpl(ProductRemoteService()),
            RecentProductRepositoryImpl(RecentDao(this)),
            CartRepositoryImpl(CartRemoteService()),
            productId,
            recentProductUiModel,
        )
        binding.presenter = presenter
        presenter.initPresenter()
    }

    private fun setFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(
            CounterDialog.CHANGE_COUNTER_APPLY_KEY,
            this,
        ) { _, bundle ->
            val changeCount = bundle.getInt(CounterDialog.COUNT_KEY, -1)
            presenter.updateProductCount(changeCount)
        }
    }

    override fun failedLoadProductInfo() {
        runOnUiThread {
            Toast.makeText(
                this,
                getString(R.string.failed_load_product_detail_info),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    override fun showCartScreen() = startActivity(CartActivity.getIntent(this))
    override fun hideRecentScreen() {
        binding.recentInfoBoxView.visibility = View.INVISIBLE
    }

    override fun setRecentScreen(title: String, money: String) {
        binding.recentProductTitle.text = title
        binding.recentProductPrice.text = getString(R.string.price_format, money)
    }

    override fun showRecentProductDetailScreen(recentProductUiModel: RecentProductUiModel) {
        startActivity(
            getIntent(
                this,
                recentProductUiModel.product.id,
                recentProductUiModel,
            ).apply { addFlags(FLAG_ACTIVITY_CLEAR_TOP) },
        )
    }

    override fun exitDetailScreen() {
        runOnUiThread { finish() }
    }

    override fun showSelectCartProductCountScreen(product: ProductUiModel, cartId: Long?) {
        val counterDialog = CounterDialog.newInstance(product, cartId)
        counterDialog.show(supportFragmentManager, COUNTER_DIALOG_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_cancel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cancel_action -> {
                presenter.exit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val PRODUCT_KEY = "PRODUCT_KEY"
        private const val RECENT_PRODUCT_KEY = "RECENT_PRODUCT_KEY"
        private const val COUNTER_DIALOG_TAG = "counter_dialog_tag"

        fun getIntent(
            context: Context,
            productId: Long,
            recentProductUiModel: RecentProductUiModel?,
        ): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(PRODUCT_KEY, productId)
                recentProductUiModel?.let { putExtra(RECENT_PRODUCT_KEY, it) }
            }
        }
    }
}