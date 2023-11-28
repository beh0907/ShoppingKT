package com.skymilk.shoppingkt.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
}