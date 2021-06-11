package de.madem.util.validation

/**This class is able to validate an EMail Address*/
class EMailValidator : Validator<String?> {
    //#region Fields
    private val validationRegex = Regex("[a-zA-Z0-9]+([\\.\\-_][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([\\.\\-_][a-zA-Z0-9]+)*\\.[a-z]+")
    //#endregion

    //#region Interface Functions
    override fun isValid(param: String?): Boolean = !(param.isNullOrEmpty()) && validationRegex.matches(param)
    //#endregion

}