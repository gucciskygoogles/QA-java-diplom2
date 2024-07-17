import api.client.OrderClient;
import api.client.UserClient;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import parktikum.DataCreator;
import parktikum.Finals;
import parktikum.Order;
import parktikum.User;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static parktikum.DataCreator.getValidIngredientIds;

public class OrderCreateAndGetTest {

    private OrderClient orderClient;
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = Finals.BASE_URI;
        orderClient = new OrderClient();
        UserClient userClient = new UserClient();

        User user = DataCreator.generateRandomUser();
        userClient.createUser(user);

        Response loginResponse = userClient.loginUser(user);
        token = loginResponse.path("accessToken");
    }

    @Test
    @Description("Тест Создание заказа с авторизациий")
    public void createOrderWithAuthorizationTest() {
        Order order = new Order(getValidIngredientIds(orderClient));
        Response response = orderClient.createOrderWithAuthorization(order, token);

        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Тест Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        Order order = new Order(getValidIngredientIds(orderClient));
        Response response = orderClient.createOrderWithoutAuthorization(order);

        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Тест Создание заказа с ингредиентами")
    public void createOrderWithIngredientsTest() {
        Order order = new Order(getValidIngredientIds(orderClient));
        Response response = orderClient.createOrder(order);

        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Тест Создание заказа с игредиентами")
    public void createOrderWithoutIngredientsTest() {
        Response response = orderClient.createOrderWithoutIngredients();

        response.then().statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Тест Создание заказа с некорректными ингредиентами")
    public void createOrderWithInvalidIngredientTest() {
        Response response = orderClient.createOrderWithInvalidIngredient();

        response.then().statusCode(500);
    }

    @Test
    @Description("Тест Получение заказов юзера")
    public void getUserOrdersWithAuthorizationTest() {
        Response response = orderClient.getUserOrders(token);

        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @Description("Тест Получение заказов юзера без регистрации")
    public void getUserOrdersWithoutAuthorizationTest() {
        Response response = orderClient.getUserOrdersWithoutAuthorization();

        response.then().statusCode(401)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }


}
