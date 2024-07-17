import api.client.UserClient;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import parktikum.DataCreator;
import parktikum.Finals;
import parktikum.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataTest {

    private UserClient userClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = Finals.BASE_URI;
        userClient = new UserClient();
    }

    @Test
    @Description("Обновление данных юзера")
    public void changeUserData() {
        User user = DataCreator.generateRandomUser();
        userClient.createUser(user)
                .then()
                .body("success", equalTo(true));

        Response loginResponse = userClient.loginUser(user)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(user.getEmail()))
                .and()
                .body("user.name", equalTo(user.getName()))
                .extract()
                .response();

        String accessToken = loginResponse.jsonPath().getString("accessToken");

        String newEmail = User.generateRandomEmail();
        String newName = User.generateRandomName();

        userClient.updateUserInfo(accessToken, newEmail, newName)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(newEmail))
                .and()
                .body("user.name", equalTo(newName));
    }

    @Test
    @Description("Проверка смены данных без авторизации")
    public void changeUserDataWithoutAuthorizationTest() {
        User user = DataCreator.generateRandomUser();
        userClient.createUser(user)
                .then()
                .body("success", equalTo(true));

        userClient.loginUser(user)
                .then()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(user.getEmail()))
                .and()
                .body("user.name", equalTo(user.getName()));

        String newEmail = User.generateRandomEmail();
        String newName = User.generateRandomName();

        userClient.updateUserInfoWithoutAuthorization(newEmail, newName)
                .then()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
