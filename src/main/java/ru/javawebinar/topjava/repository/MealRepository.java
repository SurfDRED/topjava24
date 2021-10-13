package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    List<Meal> getListAllMeal();

    Meal updateMeal(Meal meal);

    Meal addMeal(Meal meal);

    Meal getMeal(int id);

    void deleteMeal(int id);
}