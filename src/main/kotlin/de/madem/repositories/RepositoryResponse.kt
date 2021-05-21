package de.madem.repositories

sealed class RepositoryResponse<out D, out E> {
    data class Error(val error: Throwable) : RepositoryResponse<Nothing,Throwable>(){
        constructor(msg: String) : this(Exception(msg))
    }
    data class Data<T>(val value: T) : RepositoryResponse<T, Nothing>()
}