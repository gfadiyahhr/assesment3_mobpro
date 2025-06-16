package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.screen

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.R
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.Mobil
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.MobilApi

@Composable
fun MobilDialog(
    bitmap: Bitmap?,
    mobil: Mobil? = null,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String?) -> Unit,
    onImageChangeRequest: () -> Unit
) {
    var nama by remember { mutableStateOf(mobil?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(mobil?.deskripsi ?: "") }
    val context = LocalContext.current

    // Fungsi untuk mendapatkan URL gambar yang benar
    val imageUrl = remember(mobil?.image) {
        if (mobil?.image?.startsWith("http", ignoreCase = true) == true) {
            mobil.image
        } else {
            mobil?.image?.let { MobilApi.getMobilUrl(it) } ?: ""
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable(onClick = onImageChangeRequest)
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Gambar Mobil",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Gambar Mobil",
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(R.drawable.loading_img),
                            error = painterResource(R.drawable.broken_img)
                        )
                    }
                }

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Mobil") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismissRequest) {
                        Text("Batal")
                    }
                    Button(
                        onClick = { onConfirmation(nama, deskripsi, mobil?.id) },
                        enabled = nama.isNotBlank() && deskripsi.isNotBlank()
                    ) {
                        Text(if (mobil == null) "Simpan" else "Update")
                    }
                }
            }
        }
    }
}