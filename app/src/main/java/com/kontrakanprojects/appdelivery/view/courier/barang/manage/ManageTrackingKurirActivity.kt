package com.kontrakanprojects.appdelivery.view.courier.barang.manage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.databinding.ActivityManageTrackingKurirBinding
import com.kontrakanprojects.appdelivery.db.LatLong
import com.kontrakanprojects.appdelivery.model.kurir.ResultKurir
import com.kontrakanprojects.appdelivery.utils.createPartFromString
import com.kontrakanprojects.appdelivery.utils.reqFileImage
import com.kontrakanprojects.appdelivery.utils.showMessage
import com.kontrakanprojects.appdelivery.view.admin.couriers.detail.DetailCouriersFragment
import com.kontrakanprojects.appdelivery.view.courier.viewmodel.BarangKurirViewModel
import com.yalantis.ucrop.UCrop
import okhttp3.MultipartBody
import okhttp3.RequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ManageTrackingKurirActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityManageTrackingKurirBinding
    private val viewModel by viewModels<BarangKurirViewModel>()
    private var bottomSheetDialog: BottomSheetDialog? = null

    private lateinit var bottomSheetView: View

    private var idBarang = 0
    private var statusBarang = 0
    private var request = 0

    private var longitude: String? = null
    private var latitude: String? = null
    private var isValid = true

    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private var gambarPath: String? = null

    private var resultDetailCourier: ResultKurir? = null

    companion object {
        const val EXTRA_ID_BARANG = "extra_id"
        const val EXTRA_STATUS_BARANG = "extra_status_barang"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
        private const val NOT_NULL = "Field tidak boleh kosong!"
    }

    private val TAG = ManageTrackingKurirActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTrackingKurirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarTitle()

        idBarang = intent.getIntExtra(EXTRA_ID_BARANG, 0)
        statusBarang = intent.getIntExtra(EXTRA_STATUS_BARANG, 0)

        prepareEdit()

        with(binding) {
            btnSavePackage.setOnClickListener { validation() }
            btnChooseLocation.setOnClickListener { moveMaps() }
            btnChooseImageDeliver.setOnClickListener { imageDeliver() }
        }
    }

    private fun imageDeliver() {
        permission()
        showBottomSheet(isEditProfile = false)
    }

    private fun moveMaps() {
        startActivityForResult(Intent(this, MapsActivity::class.java), MapsActivity.REQUEST_MAPS)
    }

    private fun validation() {
        with(binding) {
            // cek nilai yang dikirimkan
            // cek radio button
            when {
                rbTrack02.isChecked -> {
                    statusBarang = 2
                }
                rbTrack03.isChecked -> {
                    statusBarang = 3
                }
                rbTrack04.isChecked -> {
                    statusBarang = 4
                }
                rbTrack05.isChecked -> {
                    statusBarang = 5
                }
                rbTrack06.isChecked -> {
                    statusBarang = 6
                }
                else -> {
                    isValid = false
                }
            }

            // cek location
            when {
                longitude.isNullOrEmpty() -> {
                    tvFailedLocation.visibility = View.VISIBLE
                    isValid = false
                }
                latitude.isNullOrEmpty() -> {
                    tvFailedLocation.visibility = View.VISIBLE
                    isValid = false
                }
            }

            if (isValid) {
                val params = getUserDetail()

                var imagesParams: MultipartBody.Part? = null
                if (gambarPath != null) {
                    imagesParams = reqFileImage(gambarPath, "foto_diterima")
                }

                Log.d(TAG, "validation: $params ")
                Log.d(TAG, "validation: $imagesParams")
                addTrackingBarang(params, imagesParams)
                tvFailedLocation.visibility = View.GONE
            } else {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.failed),
                    "Data masih ada yang kosong!", MotionToast.TOAST_WARNING)
            }
        }
    }

    private fun addTrackingBarang(params: HashMap<String, RequestBody>, imageParams: MultipartBody.Part?) {
        isLoading(true)
        viewModel.addTracking(params, imageParams).observe(this@ManageTrackingKurirActivity, { response ->
            isLoading(false)
            if (response.status == 200) {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.success),
                    "Berhasil mengubah status barang", MotionToast.TOAST_SUCCESS)
                finish()
            } else {
                showMessage(this@ManageTrackingKurirActivity, getString(R.string.success),
                    response.message, MotionToast.TOAST_ERROR)
                finish()
            }
        })
    }


    private fun prepareEdit() {
        with(binding) {
            when (statusBarang) {
                1 -> {
                    rbTrack06.visibility = View.GONE
                    btnChooseImageDeliver.visibility = View.GONE
                }
                2 -> {
                    rbTrack02.visibility = View.GONE
                    rbTrack06.visibility = View.GONE
                    btnChooseImageDeliver.visibility = View.GONE
                }
                3 -> {
                    rbTrack02.visibility = View.GONE
                    rbTrack03.visibility = View.GONE
                    rbTrack06.visibility = View.GONE
                    btnChooseImageDeliver.visibility = View.GONE
                }
                4 -> {
                    rbTrack02.visibility = View.GONE
                    rbTrack03.visibility = View.GONE
                    rbTrack04.visibility = View.GONE
                    rbTrack06.visibility = View.GONE
                    btnChooseImageDeliver.visibility = View.GONE
                }
                5 -> {
                    rbTrack02.visibility = View.GONE
                    rbTrack03.visibility = View.GONE
                    rbTrack04.visibility = View.GONE
                    rbTrack05.visibility = View.GONE
                    rbTrack06.visibility = View.VISIBLE
                    btnChooseImageDeliver.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MapsActivity.REQUEST_MAPS && resultCode == RESULT_OK && data != null) {
            val latLong = data.getParcelableExtra<LatLong>(MapsActivity.RESULT_LATLONG)
            if (latLong != null) {
                longitude = latLong.longitude.toString()
                latitude = latLong.latitude.toString()

                binding.tvLocationLatlong.text = getString(R.string.latlong, latitude, longitude)
            }
        }

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                gambarPath = getPathImage(UCrop.getOutput(data!!)!!)

                Glide.with(this)
                    .load(gambarPath)
                    .into(binding.ivImageDeliver)
                
                binding.btnChooseImageDeliver.visibility = View.GONE
                
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(data!!)
                Toast.makeText(this, cropError?.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onActivityResult: ${cropError?.message}")
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code bukan UCrop")
        }
    }



    private fun isLoading(state: Boolean) {
        with(binding) {
            if (state) {
                pbSave.visibility = View.VISIBLE
                btnSavePackage.visibility = View.GONE
            } else {
                pbSave.visibility = View.GONE
                btnSavePackage.visibility = View.VISIBLE
            }
        }
    }

    private fun getUserDetail(): HashMap<String, RequestBody> {
        return hashMapOf(
            "id_barang" to createPartFromString(idBarang.toString()),
            "detail_barang" to createPartFromString(statusBarang.toString()) ,
            "longitude" to createPartFromString(longitude.toString()),
            "latitude" to createPartFromString(latitude.toString())
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setToolbarTitle() {
        setSupportActionBar(binding.topAppBar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Ubah Status Barang"
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onClick(v: View?) {
        with(binding){
            when (v?.id) {
                R.id.btn_choose_image_deliver -> {
                    //
                }
                else -> Log.d(TAG, "onClick: HALO")
            }
        }
    }

    // permission camera, write file, read file , and image
    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), ManageTrackingKurirActivity.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // gambar
    private fun showBottomSheet(
        itemProfile: String? = null, titleSheet: String? = null,
        param: String? = null, isEditProfile: Boolean,
        isEditPassword: Boolean = false,
    ) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.bottom_sheet_pick, this.findViewById(R.id.bottomSheetContainer)
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
                LayoutInflater.from(this).inflate(
                    R.layout.bottom_sheet_editprofilepass,
                    this.findViewById(R.id.bottomSheetEditProfilePass)
                )
            } else { // change all data profile but no password
                LayoutInflater.from(this).inflate(
                    R.layout.bottom_sheet_editprofile,
                    this.findViewById(R.id.bottomSheetEditProfile)
                )
            }
        } else { // ubah foto profile
            bottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.bottom_sheet_pick,
                this.findViewById(R.id.bottomSheetContainer)
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
                        ManageTrackingKurirActivity.NOT_NULL
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
                        ManageTrackingKurirActivity.MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPassword.editText?.addTextChangedListener { s ->
                    edtNewPassword.error = if (s?.length!! < 5) {
                        ManageTrackingKurirActivity.MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }

                edtNewPasswordAgain.editText?.addTextChangedListener { s ->
                    edtNewPasswordAgain.error = if (s?.length!! < 5) {
                        ManageTrackingKurirActivity.MIN_COUNTER_LENGTH_PASS
                    } else {
                        null
                    }
                }
            }

            // save ke viewmodel
            //save(edtInput, itemProfile, param, isEditPassword)

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
                // editPhotoProfile()

                // hapus value camera, pickimages and result.gambar yang ada di model
                gambarPath = null
                resultDetailCourier?.fotoProfil = null
            }
        }
    }



    private fun isHasImage(): Boolean {
        if (resultDetailCourier != null) {
            return !resultDetailCourier!!.fotoProfil.isNullOrEmpty() || !gambarPath.isNullOrEmpty()
        }
        return false
    }

    private fun selectImageIntent() {
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        if (intent.resolveActivity(this.packageManager!!) != null) {
            resultLauncherPickImage.launch(Intent.createChooser(intent, "Pilih 1 Gambar"))
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

    // logic menampilkan kamera
    private fun dispatchCaptureImageIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.packageManager!!) != null) {
            try {
                imageFile = createImageFile()
            } catch (e: IOException) {
                showMessage(this,
                    getString(R.string.failed),
                    e.message!!,
                    MotionToast.TOAST_ERROR)
            }

            if (imageFile != null) {
                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.kontrakanprojects.appdelivery.fileprovider",
                    imageFile!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                resultLauncherCaptureImage.launch(intent)
            }
        }
    }

    @Throws(Exception::class)
    private fun createImageFile(): File? {
        val fileName =
            "IMAGE_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
        val directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(fileName, ".jpg", directory)
        val currentImagePath = imageFile.absolutePath
        return imageFile
    }

    private fun getPathImage(contentUri: Uri): String? {
        val filePath: String?
        val cursor = this.contentResolver?.query(contentUri, null, null, null, null)
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

    private var resultLauncherCaptureImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    imageUri?.let { startCrop(it) }
                } catch (e: Exception) {
                    Log.e(TAG, "onActivityResult: ${e.message}")
                    showMessage(
                        this,
                        getString(R.string.failed),
                        e.message.toString(),
                        MotionToast.TOAST_ERROR
                    )
                }
            }
        }

    private fun startCrop(uri: Uri) {
        val uCrop = UCrop.of(uri, Uri.fromFile(createImageFile()))
        uCrop.withAspectRatio(1F, 1F)
        uCrop.withMaxResultSize(640, 640)
        uCrop.withOptions(getCropOptions())
        uCrop.start(this)
    }

    private fun getCropOptions(): UCrop.Options {
        return UCrop.Options().apply {
            setCompressionQuality(100)
            setHideBottomControls(false)
            setToolbarTitle("Crop Image")
        }
    }
}