package de.madem.util.exceptions

class DataAlreadyExistingException(data: Any) : Exception("$data already exists!")
class InvalidIdException(id: Any) : Exception("The given id of '$id' is invalid!")