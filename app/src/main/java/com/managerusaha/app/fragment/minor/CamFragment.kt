package com.managerusaha.app.fragment.minor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.annotation.SuppressLint
import android.widget.Button
import android.widget.ImageView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.managerusaha.app.MainActivity
import com.managerusaha.app.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var from = ""
    private var camera: androidx.camera.core.Camera? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.prev_cam)
        from = arguments?.getString("from")?: ""
        val btnFlash = view.findViewById<ImageView>(R.id.btn_flash)
        btnFlash.setOnClickListener {
            toggleFlash()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val barcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
            )

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(barcodeScanner, imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("BarcodeScanner", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun toggleFlash() {
        val currentTorchState = camera?.cameraInfo?.torchState?.value
        val enable = currentTorchState != androidx.camera.core.TorchState.ON
        camera?.cameraControl?.enableTorch(enable)
        Log.d("CamFragment", "Torch state toggled: $enable")
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(scanner: com.google.mlkit.vision.barcode.BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        Log.d("BarcodeScanner", "Barcode detected: $rawValue")
                        when(from){
                            "stokkeluar","stokmasuk","" -> navigateto(rawValue)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("BarcodeScanner", "Barcode scan failed", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun navigateto(rawValue: String?) {
        val result = Bundle()
        result.putString("rawvalue", rawValue)
        parentFragmentManager.setFragmentResult("scan_result", result)

        // Balik ke fragment sebelumnya
        parentFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}