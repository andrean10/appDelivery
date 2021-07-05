package com.kontrakanprojects.appdelivery.view.admin.couriers.detail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailCouriersBinding
import com.kontrakanprojects.appdelivery.db.User
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.utils.createPartFromString
import com.kontrakanprojects.appdelivery.utils.reqFileImage
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.CouriersViewModel
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import com.yalantis.ucrop.UCrop
import okhttp3.MultipartBody
import okhttp3.RequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailCouriersFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailCouriersBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CouriersViewModel>()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var etUiUpdated: TextInputEditText

    private lateinit var user: User
    private var resultDetailCourier: ResultKurir? = null

    private var idKurir = 0
    private var request = 0
    private var valid = true

    private lateinit var bottomSheetView: View

    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private var gambarPath: String? = null

    private val TAG = DetailCouriersFragment::class.simpleName

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
        private const val REQUEST_CODE_PERMISSIONS = 111
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
        // Inflate the layout for requireActivity() fragment
        _binding = FragmentDetailCouriersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailCouriersFragmentArgs.fromBundle(arguments as Bundle)
        idKurir = args.idKurir
        request = args.idRequest

        lateinit var titleToolbar: String
        with(binding) {
            when (request) {
                REQUEST_ADD -> {
                    titleToolbar = "Tambah Data Kurir"
                }
                REQUEST_EDIT -> {
                    titleToolbar = "Edit Data Kurir"
                    init()
                    etNamaLengkapKurir.isFocusable = false
                    etNamaLengkapKurir.isClickable = false
                    etUsername.isFocusable = false
                    etUsername.isClickable = false
                    etPassword.isFocusable = false
                    etPassword.isClickable = false
                    etAlamatLengkapKurir.isFocusable = false
                    etAlamatLengkapKurir.isClickable = false
                    btnSaveProfileKurir.visibility = View.GONE

                    val idRole = UserPreference(requireContext()).getUser().idRole
                    if (idRole == ChooseLoginFragment.ROLE_ADMIN) {
                        etUsername.setOnClickListener(this@DetailCouriersFragment)
                    }
                    etNamaLengkapKurir.setOnClickListener(this@DetailCouriersFragment)
                    etPassword.setOnClickListener(this@DetailCouriersFragment)
                    etAlamatLengkapKurir.setOnClickListener(this@DetailCouriersFragment)
                }
            }

            setToolbarTitle(titleToolbar)

            btnPickImage.setOnClickListener(this@DetailCouriersFragment)
            btnSaveProfileKurir.setOnClickListener(this@DetailCouriersFragment)
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            when (v?.id) {
                R.id.btn_pick_image -> {
                    permission()
                    showBottomSheet(isEditProfile = false)
                }
                R.id.et_nama_lengkap_kurir -> {
                    val itemProfile = etNamaLengkapKurir.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleName)
                    etUiUpdated = etNamaLengkapKurir
                    showBottomSheet(itemProfile, titleSheet, "nama_lengkap", true)
                }
                R.id.et_username -> {
                    val itemProfile = etUsername.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleUsername)
                    etUiUpdated = etUsername
                    showBottomSheet(itemProfile, titleSheet, "username", true)
                }
                R.id.et_password -> {
                    val itemProfile = etPassword.text.toString().trim()
                    etUiUpdated = etPassword
                    showBottomSheet(
                        itemProfile, param = "password", isEditProfile = true,
                        isEditPassword = true
                    )
                }
                R.id.et_alamat_lengkap_kurir -> {
                    val itemProfile = etAlamatLengkapKurir.text.toString().trim()
                    val titleSheet = resources.getString(R.string.titleAlamat)
                    etUiUpdated = etAlamatLengkapKurir
                    showBottomSheet(itemProfile, titleSheet, "alamat", true)
                }
                R.id.btn_save_profile_kurir -> {
                    prepareAdd()
                }
            }
        }
    }

    private fun init() {
        isLoading(true)
        user = UserPreference(requireContext()).getUser()

        viewModel.detailKurir(idKurir).observe(viewLifecycleOwner, { response ->
            isLoading(false)
            if (response != null) {
                if (response.status == 200) {
                    val result = response.results
                    resultDetailCourier = result?.get(0)
                    if (resultDetailCourier != null) {
                        prepare(resultDetailCourier)
                    }
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

    private fun prepare(result: ResultKurir?) {
        if (result != null) {
            with(binding) {
                Glide.with(requireActivity())
                    .load(ApiConfig.IMG_URL + result.fotoProfil)
                    .listener(listenerImage)
                    .placeholder(R.drawable.no_profile_images)
                    .error(R.drawable.no_profile_images)
                    .into(ivCourierPhoto)

                etNamaLengkapKurir.setText(result.namaLengkap)
                etUsername.setText(result.username)
                etPassword.setText(result.password)
                etAlamatLengkapKurir.setText(result.alamat)
            }
        }
    }

    private val listenerImage = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBarImage.visibility = View.GONE
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            binding.progressBarImage.visibility = View.GONE
            return false
        }
    }

    private fun prepareAdd() {
        with(binding) {
            progressBarSave.visibility = View.VISIBLE

            val namaLengkap = etNamaLengkapKurir.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val alamat = etAlamatLengkapKurir.text.toString().trim()

            checkValue(namaLengkap, etNamaLengkapKurir)
            checkValue(username, etUsername)
            checkValue(password, etPassword)
            checkValue(alamat, etAlamatLengkapKurir)

            if (valid) {
                val params = hashMapOf(
                    "nama_lengkap" to createPartFromString(namaLengkap),
                    "username" to createPartFromString(username),
                    "password" to createPartFromString(password),
                    "alamat" to createPartFromString(alamat)
                )

                var imagesParams: MultipartBody.Part? = null
                if (gambarPath != null) {
                    imagesParams = reqFileImage(gambarPath, "foto_profil")
                }

                addKurir(params, imagesParams)
            } else {
                progressBarSave.visibility = View.GONE
            }
        }
    }

    private fun checkValue(value: String?, editText: TextInputEditText) {
        if (value.isNullOrEmpty()) {
            editText.error = NOT_NULL
            valid = false
            return
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

    // gambar
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

        // untuk edit profile bukan foto
        saveBottomSheet(itemProfile,
            titleSheet,
            param,
            isEditProfile,
            isEditPassword,
            bottomSheetDialog!!)

        // set ke view
        bottomSheetDialog!!.setContentView(bottomSheetView)
        bottomSheetDialog!!.show()
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
        } else { // jika memilih upload gambar
            val chooseImage: ImageView = bottomSheetView.findViewById(R.id.imgCamera)
            chooseImage.setOnClickListener {
                dispatchCaptureImageIntent()
                bottomSheetDialog.dismiss()
            }

            val chooseGaleri: ImageView = bottomSheetView.findViewById(R.id.imgGaleri)
            chooseGaleri.setOnClickListener {
                selectImageIntent()
                bottomSheetDialog.dismiss()
            }

            checkPictureBottomSheet()
        }
    }

    private fun checkPictureBottomSheet() {
        // cek jika ada gambar visibilitas delete di bottom sheet munculkan
        if (isHasImage()) {
            // delete
            bottomSheetView.findViewById<ImageView>(R.id.imgDelete).visibility = View.VISIBLE

            bottomSheetView.findViewById<ImageView>(R.id.imgDelete).setOnClickListener {
                // hapus data ke server
                editPhotoProfile()

                // hapus value camera, pickimages and result.gambar yang ada di model
                gambarPath = null
                resultDetailCourier?.fotoProfil = null
            }
        }
    }

    // logic menampilkan kamera
    private fun dispatchCaptureImageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                showMessage(requireActivity(),
                    getString(R.string.failed),
                    e.message!!,
                    MotionToast.TOAST_ERROR)
            }

            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.kontrakanprojects.appdelivery.fileprovider",
                    imageFile!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                resultLauncherCaptureImage.launch(intent)
            }
        }
    }

    private var resultLauncherCaptureImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    imageUri?.let { startCrop(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "onActivityResult: ${e.message}")
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        e.message.toString(),
                        MotionToast.TOAST_ERROR
                    )
                }
            }
        }

    private var resultLauncherPickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data.also { dataIntent ->
                    dataIntent?.data?.also { uri ->
                        startCrop(uri)
                    }
                }
//                if (result.data?.data != null)
            }
        }

    private fun selectImageIntent() {
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            resultLauncherPickImage.launch(Intent.createChooser(intent, "Pilih 1 Gambar"))
        }
    }

    @Throws(Exception::class)
    private fun createImageFile(): File? {
        val fileName =
            "IMAGE_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val directory = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(fileName, ".jpg", directory)
        val currentImagePath = imageFile.absolutePath
        return imageFile
    }

    private fun getPathImage(contentUri: Uri): String? {
        val filePath: String?
        val cursor = activity?.contentResolver?.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                gambarPath = getPathImage(UCrop.getOutput(data!!)!!)

                if (request == REQUEST_EDIT) {
                    // ubah data gambar ke server
                    editPhotoProfile(gambarPath)
                } else {
                    Glide.with(requireActivity())
                        .load(gambarPath)
                        .into(binding.ivCourierPhoto)
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(data!!)
                Toast.makeText(requireContext(), cropError?.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onActivityResult: ${cropError?.message}")
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code bukan UCrop")
        }
    }

    private fun isHasImage(): Boolean {
        if (resultDetailCourier != null) {
            return !resultDetailCourier!!.fotoProfil.isNullOrEmpty() || !gambarPath.isNullOrEmpty()
        }
        return false
    }

    private fun startCrop(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(createImageFile()))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(640, 640)
        uCrop.withOptions(getCropOptions())
        uCrop.start(requireActivity(), this)
    }

    private fun getCropOptions(): UCrop.Options {
        return UCrop.Options().apply {
            setCompressionQuality(100)
            setHideBottomControls(false)
            setToolbarTitle("Crop Image")
        }
    }

    // permission camera, write file, read file , and image
    private fun permission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dispatchCaptureImageIntent()
            } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                showMessage(
                    requireActivity(), "Failed", "Not All Permission Granted!",
                    MotionToast.TOAST_WARNING
                )
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
                checkValueBottomSheet(oldPassword, edtOldPassword, OLD_PASSWORD_IS_REQUIRED)
                checkValueBottomSheet(newPassword, edtNewPassword, NEW_PASSWORD_IS_REQUIRED)
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
                            edtNewPasswordAgain.error = WRONG_NEW_PASSWORD_AGAIN
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
                editKurir(getInput, parameters)
            }
        }
    }

    private fun editKurir(
        newInputData: String? = null, newData: HashMap<String, String>,
    ) {
        with(binding) {
            progressBarSave.visibility = View.VISIBLE
            viewModel.editKurir(idKurir, newData).observe(viewLifecycleOwner, { response ->
                progressBarSave.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        etUiUpdated.setText(newInputData)
                        bottomSheetDialog?.dismiss()
                    } else {
                        showMessage(
                            requireActivity(),
                            getString(R.string.failed),
                            response.message,
                            MotionToast.TOAST_ERROR
                        )

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
    }

    private fun addKurir(params: HashMap<String, RequestBody>, imagesParams: MultipartBody.Part?) {
        with(binding) {
            progressBarSave.visibility = View.VISIBLE
            viewModel.addKurir(params, imagesParams).observe(viewLifecycleOwner, { response ->
                progressBarSave.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) {
                        progressBarSave.visibility = View.GONE

                        findNavController().navigateUp()
                        showMessage(requireActivity(),
                            getString(R.string.success),
                            response.message,
                            MotionToast.TOAST_SUCCESS)
                    } else {
                        showMessage(requireActivity(), getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR)
                    }
                } else {
                    showMessage(requireActivity(), getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR)
                }
            })
        }
    }

    private fun editPhotoProfile(gambarPath: String? = null) {
        with(binding) {
            progressBarImage.visibility = View.VISIBLE

            if (gambarPath != null) { // ubah gambar
                val imageParams = reqFileImage(gambarPath, "foto_profil")

                viewModel.editPhotoProfile(idKurir, imageParams)
                    .observe(viewLifecycleOwner, { response ->
                        progressBarImage.visibility = View.GONE
                        progressBarSave.visibility = View.GONE
                        if (response != null) {
                            if (response.status == 200) {
                                Glide.with(requireContext())
                                    .load(gambarPath)
                                    .into(ivCourierPhoto)

                                bottomSheetDialog?.dismiss()
                            } else {
                                showMessage(
                                    requireActivity(),
                                    getString(R.string.failed),
                                    response.message,
                                    MotionToast.TOAST_ERROR
                                )
                            }
                        } else {
                            showMessage(requireActivity(),
                                getString(R.string.failed),
                                style = MotionToast.TOAST_ERROR)
                        }
                    })
            } else { // hapus gambar
                viewModel.deletePhotoProfile(idKurir).observe(viewLifecycleOwner, { response ->
                    progressBarImage.visibility = View.GONE
                    if (response != null) {
                        if (response.status == 200) {
                            ivCourierPhoto.setImageResource(R.drawable.no_profile_images)
                            bottomSheetDialog?.dismiss()

                            resultDetailCourier?.fotoProfil = null
                        } else {
                            showMessage(
                                requireActivity(),
                                getString(R.string.failed),
                                response.message,
                                MotionToast.TOAST_ERROR
                            )
                        }
                    } else {
                        showMessage(requireActivity(),
                            getString(R.string.failed),
                            style = MotionToast.TOAST_ERROR)
                    }
                })
            }
        }
    }

    private fun deleteKurir(idKurir: Int) {
        with(binding) {
            progressBarSave.visibility = View.VISIBLE
            viewModel.deleteKurir(idKurir).observe(viewLifecycleOwner, { response ->
                binding.progressBarSave.visibility = View.GONE
                if (response != null) {
                    if (response.status == 200) { // berhasil hapus siswa
                        findNavController().navigateUp()
                        showMessage(
                            requireActivity(), getString(R.string.success),
                            response.message, MotionToast.TOAST_SUCCESS
                        )
                    } else {
                        showMessage(
                            requireActivity(), getString(R.string.failed), response.message,
                            MotionToast.TOAST_ERROR
                        )
                    }
                } else {
                    showMessage(
                        requireActivity(),
                        getString(R.string.failed),
                        style = MotionToast.TOAST_ERROR
                    )
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (request == REQUEST_EDIT) {
            if (user.idRole == ChooseLoginFragment.ROLE_ADMIN) {
                inflater.inflate(R.menu.delete, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.delete -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_kurir_title))
            .setMessage(getString(R.string.delete_siswa_body))
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                deleteKurir(idKurir)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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

    private fun isLoading(status: Boolean) {
        with(binding) {
            if (status) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun setToolbarTitle(titleToolbar: String) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.topAppBar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = titleToolbar
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}