package com.kontrakanprojects.appdelivery.view.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentAdminProfileBinding
import com.kontrakanprojects.appdelivery.model.profile.ResultsItem
import com.kontrakanprojects.appdelivery.utils.showMessage
import www.sanju.motiontoast.MotionToast

class AdminProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AdminProfileViewModel>()

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var etUiUpdated: TextInputEditText
    private var idAdmin = 0
    private var valid = true

    companion object {
        private const val OLD_PASSWORD_IS_REQUIRED = "Password Lama Harus Di Isi"
        private const val NEW_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi"
        private const val NEWAGAIN_PASSWORD_IS_REQUIRED = "Password Baru Harus Di Isi Ulang"
        private const val WRONG_OLD_PASSWORD = "Password Lama Tidak Sesuai!"
        private const val WRONG_NEW_PASSWORD_AGAIN = "Password Baru Tidak Sesuai!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idAdmin = AdminProfileFragmentArgs.fromBundle(arguments as Bundle).idAdmin
        observe(idAdmin)
        setToolbarTitle()

        with(binding) {
            etNamaLengkapAdmin.setOnClickListener(this@AdminProfileFragment)
            etUsernameAdmin.setOnClickListener(this@AdminProfileFragment)
            etPasswordAdmin.setOnClickListener(this@AdminProfileFragment)
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            when (v?.id) {
                R.id.et_nama_lengkap_admin -> {
                    val itemProfile = etNamaLengkapAdmin.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleName)
                    etUiUpdated = etNamaLengkapAdmin
                    showBottomSheet(itemProfile, titleSheet, "nama_lengkap", true)
                }
                R.id.et_username_admin -> {
                    val itemProfile = etUsernameAdmin.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleUsername)
                    etUiUpdated = etUsernameAdmin
                    showBottomSheet(itemProfile, titleSheet, "username", true)
                }
                R.id.et_password_admin -> {
                    val itemProfile = etPasswordAdmin.text.toString().trim()
                    etUiUpdated = etPasswordAdmin
                    showBottomSheet(
                        itemProfile, param = "password", isEditProfile = true,
                        isEditPassword = true)
                }
            }
        }
    }

    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean,
        isEditPassword: Boolean = false,
    ) {
        bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)

        bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.bottom_sheet_pick, activity?.findViewById(R.id.bottomSheetContainer)
        )

        initLayoutBottomSheet(isEditProfile, isEditPassword)

        // save
        saveBottomSheet(itemProfile,
            titleSheet,
            param,
            isEditProfile,
            isEditPassword,
            bottomSheetDialog)

        // set ke view
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun initLayoutBottomSheet(isEditProfile: Boolean, isEditPassword: Boolean) {
        if (isEditProfile) {
            bottomSheetView = if (isEditPassword) { // change password in profile
                LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofilepass,
                    activity?.findViewById(R.id.bottomSheetEditProfilePass)
                )
            } else { // change all data profile but no password
                LayoutInflater.from(requireContext()).inflate(
                    R.layout.bottom_sheet_editprofile,
                    activity?.findViewById(R.id.bottomSheetEditProfile)
                )
            }
        } else { // ubah foto profile
            bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                R.layout.bottom_sheet_pick,
                activity?.findViewById(R.id.bottomSheetContainer)
            )
        }
    }

    private fun saveBottomSheet(
        itemProfile: String?,
        titleSheet: String?,
        param: String? = null,
        isEditProfile: Boolean,
        isEditPassword: Boolean = false,
        bottomSheetDialog: BottomSheetDialog,
    ) {
        if (isEditProfile) {
            // ubah head bottom sheet selain password
            // set data ke bottomsheet
            var edtInput: TextInputLayout? = null
            if (!isEditPassword) { // init bukan edit password
                val tvTitle: TextView = bottomSheetView.findViewById(R.id.titleInput)
                edtInput = bottomSheetView.findViewById(R.id.tiEditProfile)
                tvTitle.text = titleSheet

                // watcher text
                edtInput.editText?.addTextChangedListener { s ->
                    edtInput.error = if (s.isNullOrEmpty()) {
                        NOT_NULL
                    } else {
                        null
                    }
                }
                edtInput.editText?.setText(itemProfile)
            } else { // init password
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)

                edtOldPassword.editText?.addTextChangedListener { s ->
                    edtOldPassword.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPassword.editText?.addTextChangedListener { s ->
                    edtNewPassword.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPasswordAgain.editText?.addTextChangedListener { s ->
                    edtNewPasswordAgain.error = if (s?.length!! < 5) {
                        MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }
            }

            // save ke viewmodel
            save(edtInput, itemProfile, param, isEditPassword)

            // cancel bottomsheet
            val btnCancel: Button = bottomSheetView.findViewById(R.id.btnCancel)
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun save(
        edtInput: TextInputLayout?, itemProfile: String?, param: String?,
        isEditPassword: Boolean = false,
    ) {
        // save data
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            val parameters = HashMap<String, String>()
            var getInput = ""

            if (isEditPassword) { // cek jika edit password arahkan logic kesini
                // init
                val edtOldPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiOldPassword)
                val edtNewPassword: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPassword)
                val edtNewPasswordAgain: TextInputLayout =
                    bottomSheetView.findViewById(R.id.tiNewPasswordAgain)

                val oldPassword = edtOldPassword.editText?.text.toString().trim()
                val newPassword = edtNewPassword.editText?.text.toString().trim()
                val newPasswordAgain = edtNewPasswordAgain.editText?.text.toString().trim()

                // cek kondisi field pada password
                checkValueBottomSheet(oldPassword, edtOldPassword,
                    OLD_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(newPassword, edtNewPassword,
                    NEW_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(newPasswordAgain,
                    edtNewPasswordAgain,
                    NEWAGAIN_PASSWORD_IS_REQUIRED)

                if (valid) {
                    // kirim data password baru ke viewmodel
                    // apakah password lama sesuai dengan password di edittext jika iya teruskan

                    if (oldPassword == itemProfile) {
                        if (newPassword == newPasswordAgain) { // jika password match
                            // get input password
                            getInput = newPassword // teruskan ke variabel getinput untuk di submit
                        } else {
                            edtNewPasswordAgain.error =
                                WRONG_NEW_PASSWORD_AGAIN
                            return@setOnClickListener
                        }
                    } else { // password lama tidak cocok
                        edtOldPassword.error = WRONG_OLD_PASSWORD
                        return@setOnClickListener
                    }
                }
            } else { // ambil inputan jika bukan edit password
                val inputData = edtInput?.editText?.text.toString().trim()

                checkValueBottomSheet(inputData, edtInput!!, NOT_NULL)

                if (valid) {
                    getInput = inputData
                }
            }

            // set ke hashmap
            parameters[param!!] = getInput

            if (valid) {
                // save dan observe (hide btn save)
                loadingInBottomSheet(true)
                editAdmin(getInput, parameters)
            }
        }
    }

    private fun editAdmin(
        newInputData: String? = null, newData: HashMap<String, String>,
    ) {
        viewModel.editAdmin(idAdmin, newData).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    etUiUpdated.setText(newInputData)
                    bottomSheetDialog.dismiss()
                } else {
                    showMessage(requireActivity(), getString(R.string.failed),
                        response.message!!, MotionToast.TOAST_ERROR)

                    bottomSheetView.findViewById<ProgressBar>(R.id.progressBar).visibility =
                        View.GONE
                    bottomSheetView.findViewById<Button>(R.id.btnSave).visibility =
                        View.VISIBLE
                }
            } else {
                showMessage(requireActivity(),
                    getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun observe(idAdmin: Int) {
        viewModel.detailAdmin(idAdmin).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results?.get(0)

                    if (result != null) {
                        prepare(result)
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed),
                        response.message!!, MotionToast.TOAST_ERROR)
                }
            } else {
                showMessage(requireActivity(),
                    getString(R.string.failed),
                    style = MotionToast.TOAST_ERROR)
            }
        })
    }

    private fun prepare(result: ResultsItem) {
        with(binding) {
            etNamaLengkapAdmin.setText(result.namaLengkap)
            etUsernameAdmin.setText(result.username)
            etPasswordAdmin.setText(result.password)
        }
    }

    private fun checkValue(value: String?, editText: TextInputEditText) {
        if (value.isNullOrEmpty()) {
            editText.error = NOT_NULL
            valid = false
            return
        }
    }

    // loading in bottomsheet
    private fun loadingInBottomSheet(isLoading: Boolean) {
        val progressBarSheet: ProgressBar = bottomSheetView.findViewById(R.id.progressBar)
        val btnSave: Button = bottomSheetView.findViewById(R.id.btnSave)

        if (isLoading) {
            btnSave.visibility = View.INVISIBLE
            progressBarSheet.visibility = View.VISIBLE
        } else {
            btnSave.visibility = View.GONE
            progressBarSheet.visibility = View.GONE
        }
    }

    private fun checkValueBottomSheet(
        value: String?,
        edtInput: TextInputLayout,
        messageError: String,
    ) {
        if (value.isNullOrEmpty()) {
            edtInput.error = messageError
            valid = false
            return
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Profil Admin"
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}