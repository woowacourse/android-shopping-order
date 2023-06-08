package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.ui.RepositoryInjector
import woowacourse.shopping.ui.RetrofitInjector
import woowacourse.shopping.ui.model.CartProductModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.orderdetail.OrderDetailPurpose

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initPresenter()

        setupToolbar()

        setupView()
    }

    private fun initBinding() {
        binding = ActivityOrderBinding.inflate(layoutInflater)
    }

    private fun initPresenter() {
        val retrofit = RetrofitInjector.inject(this)
        val cartRepository = RepositoryInjector.injectCartRepository(retrofit)
        val memberRepository = RepositoryInjector.injectMemberRepository(retrofit)
        val orderRepository = RepositoryInjector.injectOrderRepository(retrofit)

        presenter = OrderPresenter(this, cartRepository, memberRepository, orderRepository)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.orderToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
        val ids = intent.getIntegerArrayListExtra(EXTRA_KEY_IDS) ?: return finish()
        presenter.loadProducts(ids.toList())
        presenter.loadPoints()

        binding.usePoints.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.usePoints.isFocused && s != null) {
                    presenter.usePoints(s.toString().toIntOrNull() ?: 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.btnUseAllPoints.setOnClickListener { presenter.useAllPoints() }

        updateDiscountPrice(0)

        binding.orderButton.setOnClickListener { presenter.order() }
    }

    override fun showProducts(products: List<CartProductModel>) {
        binding.rvOrderProduct.adapter = OrderProductAdapter(products)
    }

    override fun showOriginalPrice(price: Int) {
        binding.originalPrice.text = getString(R.string.product_price, price)
    }

    override fun showPoints(points: Int) {
        binding.pointsAvailable.text = getString(R.string.points, points)
    }

    override fun updatePointsUsed(points: Int) {
        if (points == 0) return
        binding.usePoints.clearFocus()
        binding.usePoints.setText(points.toString())
        binding.usePoints.setSelection(binding.usePoints.length())
        binding.usePoints.requestFocus()
    }

    override fun updateDiscountPrice(price: Int) {
        binding.discountPrice.text = getString(R.string.discount_price, price)
    }

    override fun updateFinalPrice(price: Int) {
        binding.finalPrice.text = getString(R.string.product_price, price)
    }

    override fun notifyPointsExceeded() {
        AlertDialog.Builder(this)
            .setMessage("사용 가능 포인트를 초과할 수 없습니다.")
            .setPositiveButton("확인", null)
            .show()
    }

    override fun showOrderDetail(id: Int) {
        val intent =
            OrderDetailActivity.createIntent(this, id, OrderDetailPurpose.SHOW_ORDER_COMPLETE.name)
        startActivity(intent)
    }

    override fun notifyFailure(message: String) {
        Toaster.showToast(this, message)
    }

    companion object {
        private const val EXTRA_KEY_IDS = "ids"

        fun createIntent(context: Context, ids: List<Int>): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putIntegerArrayListExtra(EXTRA_KEY_IDS, ArrayList(ids))
            return intent
        }
    }
}