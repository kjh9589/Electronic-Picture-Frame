package com.teamnoyes.electronicpicutreframe

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.net.Uri

class MainActivity : AppCompatActivity() {
    // startActivityForResult is deprecated
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK){
            val selectedImageUri: Uri? = activityResult.data?.data
            if (selectedImageUri != null){
                if (imageUriList.size == 6){
                    Toast.makeText(this, "이미 사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                }
                imageUriList.add(selectedImageUri)
                imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
            }
            else{
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            return@registerForActivityResult
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted->
            if (isGranted) {
                navigatePhotos()
                // Permission is granted. Continue the action or workflow in your
                // app.
            }
            else {
                if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)){
                    showPermissionContextPopup()
                }
                Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }

    private val startPhotoFrameModeButton: Button by lazy {
        findViewById<Button>(R.id.startPhotoFrameModeButton)
    }

    private val imageViewList: List<ImageView> by lazy{
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.oneOneImageView))
            add(findViewById(R.id.oneTwoImageView))
            add(findViewById(R.id.oneThreeImageView))
            add(findViewById(R.id.twoOneImageView))
            add(findViewById(R.id.twoTwoImageView))
            add(findViewById(R.id.twoThreeImageView))
        }
    }

    private val imageUriList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPhotoButton()
        initStartPhotoFrameModeButton()
    }

    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
//            when {
//                ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                ) == PackageManager.PERMISSION_GRANTED -> {
//                    // 권한이 잘 부여되었을 때 갤러리에서 사진을 선택
//                    navigatePhotos()
//                }
//                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
//                    // 교육용 팝업을 확인 후 권한 팝업을 띄우는 기능
//                    showPermissionContextPopup()
//                }
//                else -> {
//                    requestPermissions(
//                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                        1000
//                    )
//                }
//            }
        }
    }


    private fun initStartPhotoFrameModeButton() {
        startPhotoFrameModeButton.setOnClickListener {
            if (imageUriList.isEmpty()){
                Toast.makeText(this, "보여줄 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)

            startActivity(intent)
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when(requestCode){
//            1000 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    // 권한이 부여됨
//                    navigatePhotos()
//                }
//                else{
//                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//            else -> {
//
//            }
//        }
//    }

    private fun navigatePhotos(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        requestActivity.launch(intent)
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
//                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ ->

            }
            .create()
            .show()
    }

}