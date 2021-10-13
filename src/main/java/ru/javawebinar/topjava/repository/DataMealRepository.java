package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DataMealRepository implements MealRepository {
    private final AtomicInteger id = new AtomicInteger(0);
    private final Map<Integer, Meal> meals;

    public DataMealRepository() {
        meals = new ConcurrentHashMap<>();
        Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        ).forEach(this::addMeal);
    }

    @Override
    public List<Meal> getListAllMeal() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal updateMeal(Meal meal) {
        return meals.replace(meal.getId(), meal);
    }

    @Override
    public Meal addMeal(Meal meal) {
        meal.setId(id.getAndIncrement());
        return meals.put(meal.getId(), meal);
    }

    @Override
    public Meal getMeal(int id) {
        return meals.get(id);
    }

    @Override
    public void deleteMeal(int id) {
        meals.remove(id);
    }
}