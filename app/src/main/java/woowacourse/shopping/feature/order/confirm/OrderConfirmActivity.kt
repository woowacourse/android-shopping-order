package woowacourse.shopping.feature.order.confirm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.domain.model.MoneySalePolicy
import woowacourse.shopping.R
import woowacourse.shopping.data.preferences.UserPreference
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderConfirmBinding
import woowacourse.shopping.model.MoneySaleUiModel
import woowacourse.shopping.module.ApiModule
import woowacourse.shopping.util.getSerializableExtraCompat
import woowacourse.shopping.util.keyError
import woowacourse.shopping.util.showToastNetworkError
import woowacourse.shopping.util.showToastShort
import woowacourse.shopping.util.toMoneyFormat

class OrderConfirmActivity : AppCompatActivity(), OrderConfirmContract.View {
    private lateinit var binding: ActivityOrderConfirmBinding
    private lateinit var presenter: OrderConfirmPresenter
    private val orderConfirmAdapter: OrderConfirmAdapter by lazy { OrderConfirmAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_confirm)
        val cartIds = intent.getSerializableExtraCompat<ArrayList<Long>>(CART_ID_KEY)
            ?: return keyError(CART_ID_KEY)

        binding.orderProductRecyclerView.adapter = orderConfirmAdapter
        initPresenter(cartIds)
        initObserve()
        presenter.loadSelectedCarts()
        supportActionBar?.title = getString(R.string.order_confirm_bar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPresenter(cartIds: List<Long>) {
        val apiModule = ApiModule.getInstance(UserPreference)
        presenter =
            OrderConfirmPresenter(
                this,
                MoneySalePolicy(),
                CartRepositoryImpl(apiModule.createCartService()),
                OrderRepositoryImpl(apiModule.createOrderService()),
                cartIds
            )
        binding.presenter = presenter
    }

    private fun initObserve() {
        presenter.cartProducts.observe(this) { orderConfirmAdapter.setOrderProducts(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showFailedLoadCartInfo() {
        showToastShort(R.string.failed_load_cart_info)
    }

    override fun setSaleInfo(moneySaleUiModel: MoneySaleUiModel) {
        binding.saleCategoryTextView.text =
            getString(R.string.sale_boundary_condition, moneySaleUiModel.saleBoundary)
        binding.saleAmountTextView.text =
            getString(R.string.sale_apply_amount, moneySaleUiModel.saleAmount)
    }

    override fun setPayInfo(originMoney: Int, saleApplyMoney: Int) {
        binding.payOriginPriceTextView.text =
            getString(R.string.price_format, originMoney.toMoneyFormat())
        binding.paySaleAmountTextView.text =
            getString(R.string.sale_price_format, saleApplyMoney.toMoneyFormat())
    }

    override fun setFinalPayInfo(saleApplyMoney: Int) {
        binding.finalPayAmountPrice.text =
            getString(R.string.price_format, saleApplyMoney.toMoneyFormat())
    }

    override fun showSaleInfo() {
        binding.saleInfoLayout.visibility = View.VISIBLE
    }

    override fun showNoneSaleInfo() {
        binding.noneSaleLayout.visibility = View.VISIBLE
    }

    override fun showOrderSuccess(cartIds: List<Long>) {
        val resultIntent = Intent()
        resultIntent.putExtra(ORDER_CART_ID_KEY, ArrayList(cartIds))
        setResult(Activity.RESULT_OK, resultIntent)
    }

    override fun showOrderFailed() {
        showToastShort(R.string.order_failed_message)
    }

    override fun showNetworkError() {
        showToastNetworkError()
    }

    override fun exitScreen() = finish()

    companion object {
        private const val CART_ID_KEY = "cart_id_key"
        const val ORDER_CART_ID_KEY = "order_cart_id_key"
        fun getIntent(context: Context, cartIds: List<Long>): Intent {
            return Intent(context, OrderConfirmActivity::class.java).apply {
                putExtra(CART_ID_KEY, ArrayList(cartIds))
            }
        }
    }
}
