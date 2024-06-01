package com.example.jaringobi.view.addExpensePage

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.jaringobi.BuildConfig
import com.example.jaringobi.common.ImageData
import com.example.jaringobi.common.OcrResponse
import com.example.jaringobi.common.RequestBody
import com.example.jaringobi.common.RetrofitClient.ocrService
import com.example.jaringobi.data.db.AppDatabase
import com.example.jaringobi.data.db.ExpenseEntity
import com.example.jaringobi.databinding.FragmentReceiptBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class ReceiptFragment : Fragment() {
    private lateinit var binding: FragmentReceiptBinding

    private lateinit var db: AppDatabase

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

        val dialog =
            Dialog(requireContext()).apply {
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(ProgressBar(requireContext()))
                setCanceledOnTouchOutside(false)
                show()
            }

        val call = ocrService.getOcr(BuildConfig.OCR_KEY, requestBody)
        call.enqueue(
            object : Callback<OcrResponse> {
                override fun onResponse(
                    call: Call<OcrResponse>,
                    response: Response<OcrResponse>,
                ) {
                    dialog.dismiss()
                    if (response.isSuccessful) {
                        db = AppDatabase.getInstance(requireContext())

                        val ocrResponse = response.body()!!
                        Log.d("SUCCESS", ocrResponse.toString())

                        val year = ocrResponse.images[0].receipt.result.paymentInfo.date.formatted.year.takeLast(2)
                        val month = ocrResponse.images[0].receipt.result.paymentInfo.date.formatted.month
                        val date = ocrResponse.images[0].receipt.result.paymentInfo.date.formatted.day
                        val store = ocrResponse.images[0].receipt.result.storeInfo.name.formatted.value
                        val cost = ocrResponse.images[0].receipt.result.totalPrice.price.formatted.value

                        val decimalFormat = DecimalFormat("#,###")
                        val formattedCost = decimalFormat.format(cost.toInt())

                        val entity =
                            ExpenseEntity(
                                date = "$year-$month-$date",
                                store = store,
                                cost = "$formattedCost 원",
                            )

                        // 코루틴
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val expenseDAO = db.getExpenseDAO()
                                Log.d("INSERT", entity.toString())
                                expenseDAO.insertExpense(entity)
                            } catch (exception: Exception) {
                                Log.e("DATABASE_ERROR", exception.toString())
                                Toast.makeText(requireContext(), "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Toast.makeText(requireContext(), "지출내역이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<OcrResponse>,
                    throwable: Throwable,
                ) {
                    dialog.dismiss()
                    Log.d("FAIL", throwable.toString())
                    Toast.makeText(requireContext(), "요청에 실패하였습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
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
