package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.DataMealRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY = 2000;
    private static final MealRepository MEAL_REPOSITORY = new DataMealRepository();
    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = String.valueOf(request.getParameter("action"));
        String id = request.getParameter("id");
        switch (action) {
            case "delete":
                MEAL_REPOSITORY.deleteMeal(Integer.parseInt(id));
                log.debug("delete meal:{}", id);
                response.sendRedirect("meals");
                break;
            case "add":
                Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0);
                log.debug("Add new meal");
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                break;
            case "update":
                meal = MEAL_REPOSITORY.getMeal(Integer.parseInt(id));
                log.debug("Update meal:{}", id);
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                break;
            default:
                request.setAttribute("meals",
                        MealsUtil.filteredByStreams(MEAL_REPOSITORY.getListAllMeal(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                log.debug("Show all meals");
                request.getRequestDispatcher("meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(req.getParameter("datetime")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories")));
        String id = req.getParameter("id");
        if (id.isEmpty()) {
            MEAL_REPOSITORY.addMeal(meal);
            log.debug("Add new meal");
        } else {
            meal.setId(Integer.parseInt(id));
            MEAL_REPOSITORY.updateMeal(meal);
            log.debug("Update meal:{}", meal.getId());
        }
        resp.sendRedirect("meals");
    }
}