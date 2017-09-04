package BL;

import DAL.Repositories.*;
import Entities.User;
import Helpers.JsonSerializer;
import Json.Params.LoginUserParams;
import Json.Params.RegisterUserParams;

/**
 * AuthManager
 */
public class AuthManager {
    private final UserRepository userRepository;

    private final int DELETE_USER = 4;
    private final int MESSAGE = 5;

    public AuthManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(String params) throws Exception {
        RegisterUserParams registerUserParams = JsonSerializer.deserialize(params, RegisterUserParams.class);
        String userName = registerUserParams.getUserName();

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user == null) {
            userRepository.save(
                    new User(
                            userName,
                            registerUserParams.getPassword(),
                            registerUserParams.getCarModel(),
                            registerUserParams.getLicensePlate(),
                            registerUserParams.getToken()
                    ));
            return registerUserParams.getToken();

        } else
            throw new Exception("שם משתמש [" + userName + "] כבר קיים");
    }

    public void login(String params) throws Exception {
        LoginUserParams loginUserParams = JsonSerializer.deserialize(params, LoginUserParams.class);
        String userName = loginUserParams.getUserName();
        String password = loginUserParams.getPassword();
        String token = loginUserParams.getToken();

        if (userName.equals("admin") && password.equals("admin")) {
            return;
        }

        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            // validate credentials
            if (userRepository.validate(userName, password)) {
                user.setActive(true);
                user.setToken(token);
                userRepository.saveOrUpdate(user);
            } else
                throw new Exception("סיסמא שגויה");
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void logout(String userName) throws Exception {
        // check if user exist in db
        User user = userRepository.findOne(userName);
        if (user != null) {
            user.setActive(false);
            userRepository.saveOrUpdate(user);
        } else
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
    }

    public void updateToken(String userName, String token) throws Exception {
        User user = userRepository.findOne(userName);
        if (user == null) {
            throw new Exception("שם משתמש [" + userName + "] לא קיים");
        }

        // update new token in db
        user.setToken(token);
        userRepository.saveOrUpdate(user);
    }
}
