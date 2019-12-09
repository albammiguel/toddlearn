package com.g12.toddlearn;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.g12.toddlearn.app.UsersDB;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Dialog myDialog; //for the pop-up
    private Realm DB;
    private TextView email;
    private TextView password;
    private RealmResults<UsersDB> userList;
    private UsersDB currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        //for the db, you have to do it in every activity that has some relationship with the DB.
        DB = Realm.getDefaultInstance();

        userList = DB.where(UsersDB.class).findAll();

        //for see the content on the DB.
        for(UsersDB u: userList){
            Log.i("DB", u.toString());
        }



    }

    public void goToSelGame(View view) {
        Intent i= new Intent(this,GameSelectionActivity.class);

        if(currentUser != null) {
            i.putExtra("childID", currentUser.getChild().getId());
        } else{
            UsersDB defaultUser = null;
            for(UsersDB u: userList){
                defaultUser = u;
                break;
            }
            i.putExtra("childID", defaultUser.getChild().getId());
        }

        startActivity(i);
    }

    public void showPopUp(View view){

        myDialog.setContentView(R.layout.login_pop_up);
        myDialog.show();
    }

    public void closePopUp(View view){ myDialog.dismiss(); }

    public void goToRegister(View view) {
        startActivity(new Intent(this,RegistrationActivity.class));
    }


    private boolean emailsEquals(String email1,String email2) {
        Pattern address = Pattern.compile("([\\w\\.]+)@([\\w\\.]+\\.\\w+)");
        Matcher match1 = address.matcher(email1);
        Matcher match2 = address.matcher(email2);
        if (!match1.find() || !match2.find()) return false; //Not an e-mail address? Already false
        if (!match1.group(2).equalsIgnoreCase(match2.group(2)))
            return false; //Not same serve? Already false
        switch (match1.group(2).toLowerCase()) {
            case "gmail.com":
                String gmail1 = match1.group(1).replace(".", "");
                String gmail2 = match2.group(1).replace(".", "");
                return gmail1.equalsIgnoreCase(gmail2);
            default:
                return match1.group(1).equalsIgnoreCase(match2.group(1));
        }
    }

    public void enterSettings(View view) {
        email = myDialog.findViewById(R.id.email_input_login);
        password = myDialog.findViewById(R.id.password_input_login);


        if (!email.getText().toString().isEmpty() & !password.getText().toString().isEmpty()) {
            for (UsersDB u : userList) {
                boolean checkEmail =  emailsEquals(u.getEmail(), email.getText().toString());
                boolean checkPassword = u.getPassword().equals(password.getText().toString());
                if (checkEmail) {
                    if(checkPassword) {
                        Intent i = new Intent(MainActivity.this, SettingMenuActivity.class);
                        currentUser = u;
                        i.putExtra("userID", currentUser.getId());
                        startActivity(i);
                        myDialog.dismiss();
                    } else {
                        Toast.makeText(myDialog.getContext(), "Wrong password",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(myDialog.getContext(), "Wrong email",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(myDialog.getContext(), "Please, fill the gaps",
                    Toast.LENGTH_LONG).show();
        }
    }
}

