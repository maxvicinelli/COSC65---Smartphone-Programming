package edu.dartmouth.cs.myruns1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

    private SharedPreferences sharedPreferences;

    public Preference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUnits(String units) {
        sharedPreferences.edit().putString("unit", units).apply();
    }

    public String getUnits() {
        return sharedPreferences.getString("unit", "nan");
    }

    public String getProfileEmail() {
        return sharedPreferences.getString("email", "nan");
    }

    public void setProfileEmail(String email) {
        sharedPreferences.edit().putString("email", email).apply();
    }

    public String getProfilePassword() {
        return sharedPreferences.getString("password", "nan");
    }

    public void setProfilePassword(String password) {
        sharedPreferences.edit().putString("password", password).apply();
    }

    public void clearProfileInfo() {
        sharedPreferences.edit().remove("name").apply();
        sharedPreferences.edit().remove("gender").apply();
        sharedPreferences.edit().remove("email").apply();
        sharedPreferences.edit().remove("password").apply();
        sharedPreferences.edit().remove("phone").apply();
        sharedPreferences.edit().remove("major").apply();
        sharedPreferences.edit().remove("class").apply();
        sharedPreferences.edit().remove("picture").apply();
    }

    public void setProfileName(String name){
        sharedPreferences.edit().putString("name", name).apply();
    }

    public String getProfileName() {
        return sharedPreferences.getString("name", "nan");
    }

    public void setProfileGender(int gender) {
        sharedPreferences.edit().putInt("gender", gender).apply();
    }

    public int getProfileGender() {
        return sharedPreferences.getInt("gender", -1);
    }

    public void setProfileClass(String dartmouthClass) {
        sharedPreferences.edit().putString("dartmouthClass", dartmouthClass).apply();
    }

    public String getProfileClass() {
        return sharedPreferences.getString("dartmouthClass", "nan");
    }

    public void setProfileMajor(String major) {
        sharedPreferences.edit().putString("major", major).apply();
    }

    public String getProfileMajor() {
        return sharedPreferences.getString("major", "nan");
    }

    public void setProfilePhone(String phone) {
        sharedPreferences.edit().putString("phone", phone).apply();
    }

    public String getProfilePhone() {
        return sharedPreferences.getString("phone", "nan");
    }

    public void setProfilePic(String picPath) {
        sharedPreferences.edit().putString("picture", picPath).apply();
    }

    public String getProfilePic() {
        return sharedPreferences.getString("picture", "nan");
    }
}
