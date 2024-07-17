import api.client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import parktikum.DataCreator;
import parktikum.Finals;
import parktikum.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private UserClient userClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = Finals.BASE_URI;
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Логин юзера")
    @Description("Логирование нового пользователя")
    public void loginUserTest() {
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
    }

    @Test
    @DisplayName("Логин юзера")
    @Description("Логирование пользователя с неверным паролем")
    public void loginUserWithWrongPasswordTest() {
        User user = DataCreator.generateRandomUser();
        userClient.createUser(user)
                .then()
                .body("success", equalTo(true));

        user.setPassword(User.generateRandomPassword());

        userClient.loginUser(user)
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин юзера")
    @Description("Логирование пользователя с неверным паролем")
    public void loginUserWithWrongNameTest() {
        User user = DataCreator.generateRandomUser();
        userClient.createUser(user)
                .then()
                .body("success", equalTo(true));

        user.setEmail(User.generateRandomPassword());

        userClient.loginUser(user)
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }


}
