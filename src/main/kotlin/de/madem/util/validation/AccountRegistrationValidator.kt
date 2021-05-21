package de.madem.util.validation

import de.madem.model.api.AccountRegistration

class AccountRegistrationValidator: Validator<AccountRegistration> {
    //#region Fields
    private val emailValidator = EMailValidator()
    //#endregion

    //#region Interface functions
    override fun isValid(param:  AccountRegistration): Boolean = with(param){
        return emailValidator.isValid(mail)
                && username.isNotBlank()
                && password.isNotBlank()
    }
    //#endregion
}