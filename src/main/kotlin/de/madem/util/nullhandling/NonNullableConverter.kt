package de.madem.util.nullhandling

/**
 * This interface contains functionality to convert data containing Null-Values to other data with no Null data
 * */
interface NonNullableConverter<NonNull> {
    /**
     * This function returns a not null representation if the instance that implemented this interface.
     * */
    fun notNullable(): NonNull
}