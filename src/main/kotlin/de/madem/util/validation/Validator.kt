package de.madem.util.validation

/**
 * This interface defines Validation functionality for Data of a certain type T
 */
interface Validator<T>{

    /**
     * This function validates a parameter.
     * @param param The parameter to be validated
     * @return Whether the parameter is valid (true) or not (false)
     * */
    fun isValid(param: T): Boolean
}