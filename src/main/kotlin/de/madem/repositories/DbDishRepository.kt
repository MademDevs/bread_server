package de.madem.repositories

import de.madem.model.DBDish
import de.madem.model.api.Dish
import de.madem.model.database.DBDishTable
import de.madem.model.database.DBRestaurantTable
import de.madem.model.database.DishContainsAdditives
import de.madem.model.database.DishHasFoodTag
import de.madem.modules.AppModule
import kotlinx.coroutines.flow.merge
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.ktorm.support.mysql.insertOrUpdate

class DbDishRepository(private val database : Database) {
    fun mergeDishesForRestaurant(dishes: List<Dish>, restaurantId: Int, shouldUpdateExisting: Boolean = true) : RepositoryResponse<List<Int>,Throwable>{
        val mergeIds = dishes.map { dish ->
            val mergeResponse = mergeDishForRestaurant(dish, restaurantId, shouldUpdateExisting)
            when(mergeResponse){
                is RepositoryResponse.Data -> return@map mergeResponse.value
                is RepositoryResponse.Error -> return@map null
            }
        }.filterNotNull()

        return if(mergeIds.size < dishes.size){
            RepositoryResponse.Error("Something went wrong during merge")
        }
        else{
            RepositoryResponse.Data(mergeIds)
        }
    }

    fun mergeDishForRestaurant(
        dish: Dish,
        restaurantId: Int,
        shouldUpdateExisting: Boolean = true
    ) : RepositoryResponse<Int, Throwable>{
        val existingDishesResponse = getDishesOfRestaurant(restaurantId)
        when(existingDishesResponse){
            is RepositoryResponse.Error -> return existingDishesResponse
            is RepositoryResponse.Data -> {
                val existingDish = existingDishesResponse.value.firstOrNull() { it.title.equals(dish.title, ignoreCase = true) }
                if (existingDish == null) {
                    val genId = database.insertAndGenerateKey(DBDishTable){
                        set(it.title, dish.title)
                        set(it.details, dish.details)
                        set(it.pictureURL, dish.pictureURL)
                        set(it.price, dish.price)
                        set(it.restaurantID, restaurantId)
                    } as Int

                    mergeDishAdditives(dish, genId)
                    mergeDishFoodTags(dish, genId)

                    return RepositoryResponse.Data(genId)
                }
                else if(shouldUpdateExisting){
                    updateDishById(existingDish.id, dish)
                }

                return RepositoryResponse.Data(existingDish.id)
            }
        }
    }

    fun getDishesOfRestaurant(restaurantId: Int) : RepositoryResponse<List<DBDish>,Throwable>{
        val fetched = database
            .sequenceOf(DBDishTable)
            .filter {
                (it.restaurantID eq restaurantId)
            }.toList()
        return RepositoryResponse.Data(fetched)
    }

    fun updateDishById(id: Int, dish: Dish) : RepositoryResponse<Boolean, Throwable>{
        val modCnt = database.update(DBDishTable){
            set(it.title, dish.title)
            set(it.price, dish.price)
            set(it.pictureURL, dish.pictureURL)
            set(it.details, dish.details)
            where {
                (it.id eq id)
            }
        }

        if(modCnt <= 0) {
            return RepositoryResponse.Error("Update failed!")
        }

        mergeDishAdditives(dish,id)
        mergeDishFoodTags(dish,id)

        return RepositoryResponse.Data(true)
    }

    //#region Help functions
    private fun mergeDishAdditives(dish: Dish, dishId: Int){
        dish.additives.forEach{additive ->
            val idRes = AppModule.databaseRepository.additives.mergeAdditive(additive)
            if(idRes is RepositoryResponse.Data){
                val id = idRes.value
                database.insertOrUpdate(DishContainsAdditives){
                    set(it.additiveID, id)
                    set(it.dishID, dishId)
                    onDuplicateKey {
                        set(it.additiveID, id)
                        set(it.dishID,dishId)
                    }
                }
            }


        }
    }

    private fun mergeDishFoodTags(dish: Dish,dishId: Int){
        dish.foodTags.forEach{foodTag ->
            val idRes = AppModule.databaseRepository.foodTags.mergeFoodTag(foodTag)
            if(idRes is RepositoryResponse.Data){
                val id = idRes.value
                database.insertOrUpdate(DishHasFoodTag){
                    set(it.foodTagID, id)
                    set(it.dishID, dishId)
                    onDuplicateKey {
                        set(it.dishID, dishId)
                        set(it.foodTagID, id)
                    }
                }
            }

        }
    }
    //#endregion
}