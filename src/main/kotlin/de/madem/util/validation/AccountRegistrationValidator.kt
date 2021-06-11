package de.madem.util.validation

import de.madem.model.api.NullableAccountRegistration

class AccountRegistrationValidator: Validator<NullableAccountRegistration?> {
    //#region Fields
    private val emailValidator = EMailValidator()
    //#endregion

    //#region Interface functions
    override fun isValid(param:  NullableAccountRegistration?): Boolean = with(param){
        return this != null
                && emailValidator.isValid(mail)
                && !username.isNullOrBlank()
                && !password.isNullOrBlank()
    }
    //#endregion
}