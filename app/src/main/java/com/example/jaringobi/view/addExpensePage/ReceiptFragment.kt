package com.example.jaringobi.view.addExpensePage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.jaringobi.BuildConfig
import com.example.jaringobi.common.ImageData
import com.example.jaringobi.common.RequestBody
import com.example.jaringobi.common.RetrofitClient.ocrService
import com.example.jaringobi.databinding.FragmentReceiptBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceiptFragment : Fragment() {
    private lateinit var binding: FragmentReceiptBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.uploadButton.setOnClickListener {
            openImagePicker()
        }
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri = result.data?.data

                if (imageUri != null) {
                    Log.d("IMAGE", imageUri.toString())
                    requestOcr(imageUri)
                }
            }
        }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageLauncher.launch(intent)
    }

    private fun requestOcr(imageUri: Uri) {
        val format = getImageFormat(imageUri)
        val data = encodeImageToBase64(imageUri)

        val imageData =
            ImageData(
                format = format,
                data = data,
            )
        val requestBody =
            RequestBody(
                images = listOf(imageData),
            )

        Log.d("REQUEST_BODY", requestBody.toString())
        val call = ocrService.getOcr(BuildConfig.OCR_KEY, requestBody)
        call.enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>,
                ) {
                    if (response.isSuccessful) {
                        val ocrResponse = response.body()
                        Log.d("SUCCESS", response.body().toString())
                    }
                }

                override fun onFailure(
                    call: Call<JsonObject>,
                    throwable: Throwable,
                ) {
                    Log.d("FAIL", throwable.toString())
                }
            },
        )
    }

    private fun getImageFormat(imageUri: Uri): String =
        requireContext().contentResolver.getType(imageUri)?.let {
            it.split("/")[1]
        } ?: ""

    private fun encodeImageToBase64(imageUri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val bytes = ByteArray(inputStream?.available() ?: 0)

        inputStream?.read(bytes)
        inputStream?.close()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
