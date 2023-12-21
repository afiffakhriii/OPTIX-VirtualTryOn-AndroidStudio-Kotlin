package com.afif.optix.ui.activity.scan

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afif.optix.MainActivity
import com.afif.optix.databinding.ActivityScanPictureBinding
import com.afif.optix.ml.FaceModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ScanPictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanPictureBinding
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var tvOutput: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var waitTextView: TextView
    private val GALLERY_REQUEST_CODE = 123
    private var selectedImageBitmap: Bitmap? = null
    private var predictionCompleted = false

    // Load labels from the labels.txt file
    private val labels: List<String> by lazy {
        try {
            resources.assets.open("labels.txt").bufferedReader().readLines()
        } catch (e: Exception) {
            Log.e("ScanPictureActivity", "Error loading labels: ${e.message}")
            emptyList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanPictureBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.scanBtn.visibility = View.GONE
        binding.btnNextToMenu.visibility = View.GONE
        binding.predictTxt.visibility = View.GONE

        binding.btnNextToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        fadeInAnimation(binding.imgScan, 6000)

        imageView = binding.imgScan
        button = binding.cameraBtn
        tvOutput = binding.scanResult
        loadingProgressBar = binding.loading
        waitTextView = binding.waitText
        val buttonLoad = binding.galleryBtn
        val buttonScan = binding.scanBtn

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                takePicturePreview.launch(null)
            } else {
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }

        buttonLoad.setOnClickListener {
            openGallery()
        }

        buttonScan.setOnClickListener {
            if (selectedImageBitmap != null) {
                loadingProgressBar.visibility = View.VISIBLE
                waitTextView.visibility = View.VISIBLE

                binding.cameraBtn.visibility = View.GONE
                binding.galleryBtn.visibility = View.GONE
                binding.divider5.visibility = View.GONE
                binding.divider6.visibility = View.GONE
                binding.or.visibility = View.GONE
                binding.scanBtn.visibility = View.GONE
                binding.predictTxt.visibility = View.VISIBLE
                binding.btnNextToMenu.visibility = View.GONE

                predictionCompleted = false

                Handler(Looper.getMainLooper()).postDelayed({
                    outputGenerator(selectedImageBitmap!!)

                    predictionCompleted = true

                    if (predictionCompleted) {
                        loadingProgressBar.visibility = View.GONE
                        waitTextView.visibility = View.GONE

                        binding.btnNextToMenu.visibility = View.VISIBLE
                    }
                }, 2000)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        try {
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } catch (e: Exception) {
            Log.e("TAG", "Error opening gallery: ${e.message}")
        }
    }

    // request camera permission
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePicturePreview.launch(null)
            } else {
                Toast.makeText(this, "Permission Denied!! Try Again", Toast.LENGTH_SHORT).show()
            }
        }

    // launch camera and take picture
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                selectedImageBitmap = bitmap
                binding.scanBtn.visibility = View.VISIBLE

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                imageView.setImageBitmap(bitmap)
                selectedImageBitmap = bitmap
                binding.scanBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun outputGenerator(bitmap: Bitmap) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val model = FaceModel.newInstance(this)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedBitmap)
        val byteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidenceThreshold = 0.5

        if (outputFeature0.floatArray[0] > confidenceThreshold) {
            val maxIndex = getMaxIndex(outputFeature0.floatArray)
            val predictedLabel = labels.getOrNull(maxIndex) ?: "Unknown"
            tvOutput.text = predictedLabel
        } else {
            tvOutput.text = "Cannot detect face"
        }

        model.close()

        if (predictionCompleted) {
            loadingProgressBar.visibility = View.GONE
            waitTextView.visibility = View.GONE
        }
    }

    private fun getMaxIndex(arr: FloatArray): Int {
        return arr.indices.maxByOrNull { arr[it] } ?: -1
    }

    private fun fadeInAnimation(view: View, duration: Long) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = android.view.animation.BounceInterpolator()
        fadeIn.duration = duration

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}
        })

        view.startAnimation(fadeIn)
    }
}

