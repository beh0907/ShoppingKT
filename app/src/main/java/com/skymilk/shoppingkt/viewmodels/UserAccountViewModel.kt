package com.skymilk.shoppingkt.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.skymilk.shoppingkt.ShoppingApplication
import com.skymilk.shoppingkt.models.User
import com.skymilk.shoppingkt.utils.RegisterValidation
import com.skymilk.shoppingkt.utils.Resource
import com.skymilk.shoppingkt.utils.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) { // 앱의 Context 작업을 처리하기 위해 AndroidViewModel를 상속 받는다

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch { _user.emit(Resource.Loading()) }

        fireStore.collection("user")
            .document(auth.uid!!)
            .get()
            .addOnSuccessListener {

                val result = it.toObject(User::class.java)
                viewModelScope.launch { _user.emit(Resource.Success(result)) }

            }.addOnFailureListener {
                viewModelScope.launch { _user.emit(Resource.Error(it.message)) }
            }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        val inputValidation = validateEmail(user.email) is RegisterValidation.Success // 이미지 방식 체크
                && user.name.trim().isNotEmpty() // 이름 공백 체크

        if (!inputValidation) {
            viewModelScope.launch { _updateInfo.emit(Resource.Error("입력 정보를 확인해주세요.")) }
            return
        }

        viewModelScope.launch { _updateInfo.emit(Resource.Loading()) }

        if (imageUri == null) saveUserInfo(user, true)
        else saveUserInfo(user, imageUri)
    }

    //선택된 이미지가 없을 때
    private fun saveUserInfo(user: User, shouldOldImage: Boolean) {
        fireStore.runTransaction {
            val documentRef = fireStore.collection("user").document(auth.uid!!)

            if (shouldOldImage) { //기존 이미지 사용 여부
                val currentUser = it.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                it.set(documentRef, newUser)
            } else { // 이미지를 삭제시킬 수도 있다
                it.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Success(user)) }
        }.addOnFailureListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Error(it.message)) }
        }
    }

    //선택된 이미지가 있을 때 새로운 이미지를 적용한다
    private fun saveUserInfo(user: User, imageUri: Uri) {
        viewModelScope.launch {

            try {
                val imageBitmap: Bitmap = if (Build.VERSION.SDK_INT >= 29) {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(
                        getApplication<ShoppingApplication>().contentResolver,
                        imageUri
                    )
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap( //SDK 29부터는 getBitmap이 deprecated 처리 됨
                        getApplication<ShoppingApplication>().contentResolver,
                        imageUri
                    )
                }

                //선택한 이미지를 byteArray화 한다
                val byteArrayOutputSteam = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputSteam)

                //이미지를 파이어베이스에 저장한다
                val imageByteArray = byteArrayOutputSteam.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await() // 파이어 스토리지에 저장

                //스토리지에 저장된 이미지의 URL 주소를 유저 객체에 추가하고 저장한다
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInfo(user.copy(imagePath = imageUrl), false)

            } catch (e: Exception) {
                viewModelScope.launch { _updateInfo.emit(Resource.Error(e.message)) }
            }

        }
    }
}