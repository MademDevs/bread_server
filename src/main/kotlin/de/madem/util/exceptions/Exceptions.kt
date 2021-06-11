package de.madem.util.exceptions

import de.madem.model.api.Restaurant

class DataAlreadyExistingException(data: Any) : Exception("$data already exists!")
class InvalidIdException(id: Any) : Exception("The given id of '$id' is invalid!")

class RestaurantAlreadyExistingException(restaurant : Restaurant)
    : Exception("Restaurant '${restaurant.title}' is already existing in Database!")