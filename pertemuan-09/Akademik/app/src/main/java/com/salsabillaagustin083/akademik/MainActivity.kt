package com.salsabillaagustin083.akademik

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.salsabillaagustin083.akademik.databinding.ActivityMainBinding
import com.salsabillaagustin083.akademik.api.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTestConnection.setOnClickListener {
            cekKoneksiApi()
        }
    }

    private fun cekKoneksiApi() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnTestConnection.isEnabled = false
        updateStatus("Status: Menghubungkan ke server...", Color.DKGRAY)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getDaftarMahasiswa()
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    val jumlahData = body?.data?.size ?: 0
                    updateStatus(
                        "Status: Terhubung (HTTP ${response.code()})\n" +
                            "Pesan: ${body?.message ?: "OK"} ($jumlahData data ditemukan)",
                        Color.parseColor("#2E7D32")
                    )
                } else {
                    updateStatus(
                        "Status: Gagal merespons (HTTP ${response.code()} -" + "${response.message()})",
                        Color.parseColor("#C62828")
                    )
                }

                } catch (e: SocketTimeoutException) {
                    updateStatus(
                        "Status: Timeout — Server terlalu lama merespons. Periksa IP & port server.",
                        Color.parseColor("#C62828")
                    )
                } catch (e: IOException) {
                    updateStatus(
                    "Status: Gagal terhubung ke jaringan(${e.localizedMessage})",
                    Color.parseColor("#C62828")
                    )
                } catch (e: HttpException) {
                    updateStatus(
                        "Status: Kesalahan HTTP (${e.code()})",
                        Color.parseColor("#C62828")
                    )
                } catch (e: Exception) {
                    updateStatus(
                        "Status: Terjadi kesalahan (${e.localizedMessage})",
                        Color.parseColor("#C62828")
                    )
                } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnTestConnection.isEnabled = true
                }
            }
        }

        private fun updateStatus(pesan: String, warna: Int) {
            binding.tvConnectionStatus.text = pesan
            binding.tvConnectionStatus.setTextColor(warna)
        }
}