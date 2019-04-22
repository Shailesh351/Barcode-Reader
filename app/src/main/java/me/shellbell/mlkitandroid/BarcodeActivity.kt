package me.shellbell.mlkitandroid

import android.app.ActionBar
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_base_camera.*
import kotlinx.android.synthetic.main.layout_barcode.*

class BarcodeActivity : BaseCameraActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomSheet(R.layout.layout_barcode)
        makeAppbarTitleCenter()
        setLongPressToCopy()
    }

    private fun makeAppbarTitleCenter() {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_appbar)
    }

    private fun setLongPressToCopy() {
        codeData.setOnLongClickListener { textView ->
            val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", codeData.text)
            clipboardManager.primaryClip = clipData
            Toast.makeText(baseContext, "Barcode copied in ClipBoard", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onClick(v: View) {
        fabProgressCircle.show()
        cameraView.captureImage { image ->
            getQRCodeDetails(image.bitmap)
            runOnUiThread {
                showPreview()
                imagePreview.setImageBitmap(image.bitmap)
            }
        }
    }

    private fun getQRCodeDetails(bitmap: Bitmap) {
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

        detector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener {

                if (it.isEmpty()) {
                    codeData.text = "No barcode detected"
                }

                for (firebaseBarcode in it) {
                    //Toast.makeText(baseContext, firebaseBarcode.displayValue, Toast.LENGTH_SHORT).show()
                    codeData.text = firebaseBarcode.displayValue
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(baseContext, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                fabProgressCircle.hide()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

}
