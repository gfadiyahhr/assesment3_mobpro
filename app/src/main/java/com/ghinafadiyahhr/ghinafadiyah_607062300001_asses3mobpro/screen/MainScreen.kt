package com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit // Import ikon Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.BuildConfig
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.R
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.Mobil
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.model.User
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.ApiStatus
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.MobilApi
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.network.UserDataStore
import com.ghinafadiyahhr.ghinafadiyah_607062300001_asses3mobpro.ui.theme.Ghinafadiyah_607062300001_asses3mobproTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    val context = LocalContext.current
    val dataStore =UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showMobilDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf(false) }
    var mobilToDelete by remember { mutableStateOf<Mobil?>(null) }
    var mobilToEdit by remember { mutableStateOf<Mobil?>(null) } // State untuk mobil yang akan diedit

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap= getCroppedImage(context.contentResolver,it)
        if (bitmap != null) showMobilDialog = true
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title={
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        }
                        else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                mobilToEdit = null // Pastikan null untuk menambah data baru
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_mobil)
                )
            }
        }

    ){ innerPadding ->
        ScreenContent(
            viewModel = viewModel,
            userId = user.email,
            currentUserId = user.email,
            onDeleteClick = { mobil ->
                mobilToDelete = mobil
                showHapusDialog = true
            },
            onEditClick = { mobil -> // Implementasi onEditClick
                mobilToEdit = mobil
                showMobilDialog = true
                // Jika ingin menampilkan gambar lama saat edit, Anda perlu logika untuk memuat bitmap dari URL mobil.image
                // Untuk contoh ini, kami hanya membiarkan bitmap null jika tidak ada gambar baru yang dipilih.
            },
            modifier = Modifier.padding(innerPadding)
        )

        if (showDialog){
            ProfilDialog(
                user = user,
                onDismissRequest = {showDialog = false}){
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog= false
            }
        }

        // Dialog untuk menambah/mengedit mobil
        if (showMobilDialog)if (showMobilDialog) {
            MobilDialog(
                bitmap = bitmap,
                mobil = mobilToEdit,
                onDismissRequest = {
                    showMobilDialog = false
                    mobilToEdit = null
                    bitmap = null
                },
                onConfirmation = { nama, deskripsi, id ->
                    if (id == null) {
                        // Mode Tambah
                        bitmap?.let {
                            viewModel.saveData(user.email, nama, deskripsi, it)
                        }
                    } else {
                        // Mode Update
                        if (bitmap != null) {
                            // Jika ada gambar baru, upload dengan gambar
                            viewModel.updateData(id,user.email,  nama, deskripsi, bitmap!!)
                        } else {
                            // Jika tidak ada gambar baru, update tanpa gambar
//                            viewModel.updateDataWithoutImage(user.email, id, nama, deskripsi)
                        }
                    }
                    showMobilDialog = false
                    mobilToEdit = null
                    bitmap = null
                },
                onImageChangeRequest = {
                    // Launch image picker/camera when image is clicked
                    val options = CropImageContractOptions(
                        null,
                        CropImageOptions(
                            imageSourceIncludeGallery = true,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                }
            )
        }

        if (showHapusDialog) {
            HapusDialog(
                onDismissRequest = {
                    showHapusDialog = false
                    mobilToDelete = null
                },
                onConfirmation = {
                    mobilToDelete?.let { mobil ->
                        viewModel.deletaData(user.email, mobil.id)
                    }
                    showHapusDialog = false
                    mobilToDelete = null
                }
            )
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(
    viewModel: MainViewModel,
    userId: String,
    currentUserId: String,
    onDeleteClick: (Mobil) -> Unit,
    onEditClick: (Mobil) -> Unit, // Callback baru untuk edit
    modifier: Modifier = Modifier
) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize().padding(4.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(data) { mobil ->
                    ListItem(
                        mobil = mobil,
                        isOwner = mobil.Authorization == currentUserId && currentUserId.isNotEmpty(),
                        onDeleteClick = { onDeleteClick(mobil) },
                        onEditClick = { onEditClick(mobil) } // Kirim mobil ke callback edit
                    )

                    Log.d("DEBUG_MOBIL", "Mobil: ${mobil.nama}, MobilUserId: '${mobil.Authorization}', CurrentUserId: '$currentUserId', IsOwner: ${mobil.Authorization == currentUserId && currentUserId.isNotEmpty()}")
                }
            }
        }

        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(
    mobil: Mobil,
    isOwner: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit // Callback baru untuk edit
) {
    Box (
        modifier = Modifier.padding(4.dp).border(0.5.dp, Color.Gray),
        contentAlignment = Alignment.BottomCenter
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    if (mobil.image.startsWith("http", ignoreCase = true)) {
                        mobil.image
                    } else {
                        MobilApi.getMobilUrl(mobil.image)
                    }
                )
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, mobil.nama),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.broken_img),
            modifier = Modifier.fillMaxWidth()
                .padding(4.dp)
                .height(190.dp)
        )

        // Tombol hapus
        if (isOwner) { // Hanya tampilkan jika pemilik
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Posisi di kanan bawah
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Tombol edit
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Posisi di kanan atas
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .clickable { onEditClick() }, // Tambahkan clickable untuk edit
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, // Ikon edit
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary, // Warna ikon edit
                    modifier = Modifier.size(20.dp)
                )
            }
        }


        Column(
            modifier = Modifier.fillMaxWidth().padding(4.dp)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                .padding(4.dp)
        ) {
            Text(
                text = mobil.nama,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = mobil.deskripsi,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore){
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}



private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
){
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName?:""
            val email = googleId.id
            val photoUrl=googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama,email,photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SING-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Ghinafadiyah_607062300001_asses3mobproTheme {
        MainScreen()
    }
}