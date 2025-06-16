
package com.managerusaha.app.fragment.minor

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.managerusaha.app.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CamFragment : Fragment() {

    companion object {
        private const val REQUEST_CAMERA = 1001
    }

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var camera: androidx.camera.core.Camera? = null
    private var fromMode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.prev_cam)
        fromMode = arguments?.getString("from") ?: ""
        cameraExecutor = Executors.newSingleThreadExecutor()

        view.findViewById<ImageView>(R.id.btn_flash).setOnClickListener {
            toggleFlash()
        }

        // check permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Izin kamera diperlukan untuk scan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview UseCase
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Analysis UseCase untuk barcode
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
            )
            val analysis = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { proxy -> processImageProxy(scanner, proxy) }
            }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, selector, preview, analysis)
            } catch (e: Exception) {
                Log.e("CamFragment", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlash() {
        camera?.let {
            val state = it.cameraInfo.torchState.value
            it.cameraControl.enableTorch(state != androidx.camera.core.TorchState.ON)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { list ->
                    list.firstOrNull()?.rawValue?.let { value ->
                        Log.d("CamFragment", "Barcode: $value")
                        deliverResult(value)
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }

    private fun deliverResult(value: String) {
        // Jangan pakai requireActivity() karena bisa NPE kalau fragment sudah detached
        val fm = activity?.supportFragmentManager ?: return
        fm.setFragmentResult(
            "scan_result",
            Bundle().apply { putString("rawvalue", value) }
        )
        fm.popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}

