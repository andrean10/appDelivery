package com.kontrakanprojects.appdelivery.view.admin.barang.detail

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailBarangBinding
import com.kontrakanprojects.appdelivery.model.barang.ResultDetailBarang
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.barang.BarangViewModel
import com.kontrakanprojects.appdelivery.view.admin.barang.managebarang.ManageBarangFragment
import www.sanju.motiontoast.MotionToast

class DetailBarangFragment : Fragment() {

    private var _binding: FragmentDetailBarangBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<BarangViewModel>()
    private var idBarang = 0

    private val TAG = DetailBarangFragment::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        idBarang = DetailBarangFragmentArgs.fromBundle(arguments as Bundle).idBarang
        init()
    }

    private fun init() {
        isLoading(true)
        viewModel.detailBarang(idBarang).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    prepare(result?.get(0))
                } else {
                    showMessage(requireActivity(),
                        "Not Found",
                        response.message,
                        MotionToast.TOAST_ERROR)
                }
            } else { // failed mengambil data
                showMessage(requireActivity(), "Failed", style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepare(result: ResultDetailBarang?, resultKurir: ResultKurir? = null) {
        if (result != null) {
            with(binding) {
                when (result.statusBarang) {
                    "1" -> {
                        tvStatusPackage.text = getString(R.string.rb_001)
                        tv09.visibility = View.GONE
                        ivImageDeliver.visibility = View.GONE
                    }
                    "2" -> {
                        tvStatusPackage.text = getString(R.string.rb_002)
                        tv09.visibility = View.GONE
                        ivImageDeliver.visibility = View.GONE
                    }
                    "3" -> {
                        tvStatusPackage.text = getString(R.string.rb_003)
                        tv09.visibility = View.GONE
                        ivImageDeliver.visibility = View.GONE
                    }
                    "4" -> {
                        tvStatusPackage.text = getString(R.string.rb_004)
                        tv09.visibility = View.GONE
                        ivImageDeliver.visibility = View.GONE
                    }
                    "5" -> {
                        tvStatusPackage.text = getString(R.string.rb_005)
                        tv09.visibility = View.GONE
                        ivImageDeliver.visibility = View.GONE
                    }
                    "6" -> {
                        tvStatusPackage.text = getString(R.string.rb_006)
                        Glide.with(requireContext())
                            .load(ApiConfig.IMG_URL + result.fotoDiterima)
                            .placeholder(R.drawable.no_profile_images)
                            .error(R.drawable.no_profile_images)
                            .into(ivImageDeliver)
                    }
                }

                tvCodeCostumer.text = result.kodePelanggan.toString()
                tvNameCostumer.text = result.penerima
                tvNumberPhone.text = result.nomorHp
                tvAddressCostumer.text = result.alamat
                courierSend.text = result.namaLengkap
                tvDistance.text = getString(R.string.km, result.distance)
                tvDetailPackage.text = result.detailBarang
                tvStatusEstimination.text = result.estiminasi + " Hari"
            }
        }
    }

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // cek jika dah login tampilkan, jika belum jangan tampilkan
        if (UserPreference(requireContext()).getLogin().isLoginValid) {
            inflater.inflate(R.menu.edit, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.edit -> {
                val toManageBarang =
                    DetailBarangFragmentDirections.actionDetailBarangFragmentToManageBarangFragment()
                toManageBarang.idBarang = idBarang
                toManageBarang.idRequest = ManageBarangFragment.REQUEST_EDIT
                findNavController().navigate(toManageBarang)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Detail Barang"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}