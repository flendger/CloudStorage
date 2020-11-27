package services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    @Test
    void createProfile() {
        //1: already exist exception
        Assertions.assertThrows(CreateProfileFailedException.class, () -> AuthService.createProfile("l1", "p4"));

        //2: profile created
        UserProfile up = null;
        String login;
        do {
            login = "l" + ((int) (Math.random()*100000));
        } while (AuthService.findUserByName(login) != null);

        try {
            up = AuthService.createProfile(login, "p5");
            System.out.printf("User [%s] created.%n", up.user);
        } catch (CreateProfileFailedException e) {
            e.printStackTrace();
        }
        Assertions.assertNotNull(up);
    }
}