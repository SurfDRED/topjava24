package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.mealsAdmin.forEach(meal -> save(meal, 1));
        MealsUtil.mealsUser.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, integer -> new ConcurrentHashMap<>()).put(meal.getId(), meal);
            log.info("save {}", meal);
            return meal;
        }
        log.info("save {}", meal);
        return getMapMeal(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete meal {}", id);
        return getMapMeal(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get meal {}", id);
        return getMapMeal(userId).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll meals without filter");
        return prepare(getMapMeal(userId), meal -> true);
    }

    @Override
    public Collection<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAll meals with filter");
        return prepare(getMapMeal(userId), meal -> DateTimeUtil.isBetweenClosed(meal.getDate(), startDate, endDate));
    }

    private List<Meal> prepare(Map<Integer, Meal> map, Predicate<Meal> predicate) {
        return map.values().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getMapMeal(int userId) {
        return repository.getOrDefault(userId, Collections.EMPTY_MAP);
    }
}