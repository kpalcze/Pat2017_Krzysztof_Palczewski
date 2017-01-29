package zadania.krzysztofpalczewski;

import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by K on 2017-01-29.
 */

public class LoginModel {


    protected boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        boolean isEmailValid = pattern.matcher(email).matches();
        return isEmailValid;
    }

    protected boolean isPasswordValid(String password) {
        char[] c_password = password.toCharArray();
        boolean isAtLeast8 = c_password.length >= 8;
        boolean hasDigit = false;
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;

        //searches for digit
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= '0' && c_password[i] <= '9') {
                hasDigit = true;
                break;
            }
        }

        //searches for lowercase letter
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= 97 && c_password[i] <= 122) {
                hasLowerCase = true;
                break;
            }
        }

        //searches for uppercase letter
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= 65 && c_password[i] <= 90) {
                hasUpperCase = true;
                break;
            }
        }

        if (isAtLeast8 && hasDigit && hasLowerCase && hasUpperCase) {
            return true;
        }
        else {
            return false;
        }
    }
}
