package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.RepositoryInjector
import woowacourse.shopping.ui.RetrofitInjector
import woowacourse.shopping.ui.model.OrderModel
import woowacourse.shopping.ui.order.OrderProductAdapter
import woowacourse.shopping.ui.shopping.ShoppingActivity

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private val extraPurpose: String by lazy { intent?.getStringExtra(EXTRA_KEY_PURPOSE) ?: "" }
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initPresenter()

        setupView()
    }

    private fun initBinding() {
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    private fun initPresenter() {
        val retrofit = RetrofitInjector.inject(this)
        val memberRepository = RepositoryInjector.injectMemberRepository(retrofit)

        presenter = OrderDetailPresenter(this, memberRepository)
    }

    private fun setupView() {
        val id = intent.getIntExtra(EXTRA_KEY_ID, 0)
        presenter.loadDetail(id)
    }

    override fun showDetail(order: OrderModel) {
        binding.rvOrderProduct.adapter = OrderProductAdapter(order.products)
        binding.order = order
    }

    override fun notifyFailure(message: String) {
        Toaster.showToast(this, message)
    }

    override fun finish() {
        when (OrderDetailPurpose.getPurpose(extraPurpose)) {
            OrderDetailPurpose.SHOW_ORDER_COMPLETE -> startShoppingActivity()
            OrderDetailPurpose.SHOW_ORDER_DETAIL -> {}
        }
        super.finish()
    }

    private fun startShoppingActivity() {
        val intent = ShoppingActivity.createIntent(this)
        intent.flags = FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    companion object {
        private const val EXTRA_KEY_ID = "id"
        private const val EXTRA_KEY_PURPOSE = "purpose"

        fun createIntent(context: Context, id: Int, purpose: String): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ID, id)
            intent.putExtra(EXTRA_KEY_PURPOSE, purpose)
            return intent
        }
    }
}