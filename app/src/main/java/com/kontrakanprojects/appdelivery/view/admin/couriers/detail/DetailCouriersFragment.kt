package com.kontrakanprojects.appdelivery.view.admin.couriers.detail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.FragmentDetailCouriersBinding
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.network.ApiConfig
import com.kontrakanprojects.appdelivery.utils.createPartFromString
import com.kontrakanprojects.appdelivery.utils.reqFileImage
import com.kontrakanprojects.appdelivery.utils.reqFileImageEmpty
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.CouriersViewModel
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

    private var resultDetailCourier: ResultKurir? = null

    private var idKurir = 0
    private var request = 0
    private var valid = true

    private lateinit var bottomSheetView: View

    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private var gambarPath: String? = null
    private var resultUriCrop: Uri? = null

    private val TAG = DetailCouriersFragment::class.simpleName

    companion object {
        const val REQUEST_ADD = 100
        const val REQUEST_EDIT = 200
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_CAPTURE_IMAGE = 222
        private const val REQUEST_CODE_SELECT_IMAGE = 333
        private const val ALERT_DIALOG_DELETE = 10
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
                    btnSaveProfileKurir.visibility = View.GONE
                }
            }

            setToolbarTitle(titleToolbar)

            btnPickImage.setOnClickListener(this@DetailCouriersFragment)
            btnSaveProfileKurir.setOnClickListener(this@DetailCouriersFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_pick_image -> {
                permission()
                showBottomSheet(isEditProfile = false)
            }
            R.id.btn_save_profile_kurir -> {
                prepareAdd()
            }
        }
    }

    private fun init() {
        viewModel.detailKurir(idKurir).observe(viewLifecycleOwner, { response ->
//            isLoading(false)
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
                    .load(ApiConfig.URL + result.fotoProfil)
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

    private fun prepareAdd() {
        with(binding) {
            val namaLengkap = etNamaLengkapKurir.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val alamat = etAlamatLengkapKurir.text.toString().trim()

            checkValue(namaLengkap, etNamaLengkapKurir)
            checkValue(username, etUsername)
            checkValue(password, etPassword)
            checkValue(alamat, etAlamatLengkapKurir)
            val image: MultipartBody.Part = if (gambarPath != null) {
                reqFileImage(gambarPath, "foto_profile")
            } else {
                reqFileImageEmpty("foto_profile")
            }

            if (valid) {
                val params = hashMapOf(
                    "nama_lengkap" to createPartFromString(namaLengkap),
                    "username" to createPartFromString(username),
                    "password" to createPartFromString(password),
                    "alamat" to createPartFromString(alamat)
                )

                lateinit var imagesParams: MultipartBody.Part
                if (gambarPath != null) {
                    imagesParams = reqFileImage(gambarPath, "foto_profil")
                }

                addKurir(params, imagesParams)

                Log.d(TAG, "prepareAdd: $params")
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

    private fun prepareEdit() {
        // TODO: 07/06/2021
    }

    // gambar
    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean,
        isEditPassword: Boolean = false,
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)

        bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
            R.layout.bottom_sheet_pick, activity?.findViewById(R.id.bottomSheetContainer)
        )

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

        initLayoutBottomSheet(isEditProfile, isEditPassword)
        saveBottomSheet(itemProfile, titleSheet, isEditProfile, isEditPassword, bottomSheetDialog)

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
        isEditProfile: Boolean,
        isEditPassword: Boolean,
        bottomSheetDialog: BottomSheetDialog,
    ) {
        if (isEditProfile) {
            // ubah head bottom sheet selain password
            // set data ke bottomsheet
            var edtInput: TextInputLayout? = null
            if (!isEditPassword) {
                val tvTitle: TextView = bottomSheetView.findViewById(R.id.titleInput)
                edtInput = bottomSheetView.findViewById(R.id.tiEditProfile)
                tvTitle.text = titleSheet
                edtInput.editText?.setText(itemProfile)
            } else {
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

//            save(edtInput, itemProfile, param, isEditKelas, isEditPassword)

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
//                changeFotoProfile()

                // hapus value camera, pickimages and result.gambar yang ada di model
                gambarPath = null
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

//                val resultUriCrop = data.let { UCrop.getOutput(it) }

                Glide.with(requireActivity())
                    .load(gambarPath)
                    .into(binding.ivCourierPhoto)

                if (requestCode == REQUEST_EDIT) {
                    // ubah data ke server
//                    changeFotoProfile(gambarPath)
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

//    private fun changeFotoProfile(result: Uri? = null) {
//        with(binding) {
//            pbLoadingPicture.visibility = View.VISIBLE
//            if (result != null) {
//                val selectedImageFile = File(getPathFromUri(result)!!)
//                val reqFile =
//                    selectedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                val body = MultipartBody.Part.createFormData(
//                    "foto_profile", selectedImageFile.name, reqFile
//                )
//
//                viewModel.changeFotoProfile(idSiswa, body).observe(viewLifecycleOwner, { response ->
//                    pbLoadingPicture.visibility = View.GONE
//                    if (response != null) {
//                        if (response.status == 200) {
////                        observeDetailSiswa()
//                            bottomSheetDialog.dismiss()
//
//                            Glide.with(requireContext())
//                                .load(result)
//                                .into(imgSiswa)
//                        } else {
//                            showMessage(
//                                requireActivity(),
//                                "Gagal",
//                                response.message,
//                                MotionToast.TOAST_ERROR
//                            )
//                        }
//                    } else {
//                        showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
//                    }
//                })
//            } else {
//                val reqFile = ""
//                    .toRequestBody("image/*".toMediaTypeOrNull())
//                val body = MultipartBody.Part
//                    .createFormData("foto_profile", "", reqFile)
//
//                viewModel.changeFotoProfile(idSiswa, body).observe(viewLifecycleOwner, { response ->
//                    pbLoadingPicture.visibility = View.GONE
//                    if (response != null) {
//                        if (response.status == 200) {
//                            binding.imgSiswa.setImageResource(R.drawable.no_profile_images)
//                            bottomSheetDialog.dismiss()
//
//                            resultDetailSiswa.fotoProfile = null
//                        } else {
//                            showMessage(
//                                requireActivity(),
//                                "Gagal",
//                                response.message,
//                                MotionToast.TOAST_ERROR
//                            )
//                        }
//                    } else {
//                        showMessage(requireActivity(), "Gagal", style = MotionToast.TOAST_ERROR)
//                    }
//                })
//            }
//        }
//    }

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

    private fun addKurir(params: HashMap<String, RequestBody>, imagesParams: MultipartBody.Part) {
        viewModel.addKurir(params, imagesParams).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
                    findNavController().navigateUp()
                    showMessage(requireActivity(), getString(R.string.success), response.message,
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

    private fun editKurir(idKurir: Int, params: HashMap<String, Any>) {
        viewModel.editKurir(idKurir, params).observe(viewLifecycleOwner, { response ->
            if (response != null) {
                if (response.status == 200) {
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

    private fun deleteKurir(idKurir: Int) {
//        with(binding) {
        viewModel.deleteKurir(idKurir).observe(viewLifecycleOwner, { response ->
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
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (request == REQUEST_EDIT) {
            inflater.inflate(R.menu.delete, menu)
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