package com.skymilk.shoppingkt.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skymilk.shoppingkt.firebase.FirebaseCommon
import com.skymilk.shoppingkt.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //파이어베이스 인증 초기화
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    //파이어스토어 초기화
    @Provides
    @Singleton
    fun provideFirebaseFireStore() = FirebaseFirestore.getInstance()

    //파이어스토리지 초기화
    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance().reference

    @Provides
    fun provideIntroductionSharedPreferences(
        application: Application
    ) = application.getSharedPreferences(Constants.INTRODUCTION_SHARED_PREFERENCES, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        fireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ) = FirebaseCommon(fireStore, firebaseAuth)
}