package com.example.tomas.beavents;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LLP-student on 3/26/2015.
 */
public class SettingsActivity extends BaseActivity  {
    private static List<String> IGNORE_KEYS=Arrays.asList(new String[]{"user_interests","saved_events","created_events"});
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        super.onCreateDrawer();
        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(R.id.content_frame, mPrefsFragment);
        mFragmentTransaction.commit();


//        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//
//            }
//        };
//        SharedPreferences prefs =
//                PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.registerOnSharedPreferenceChangeListener(listener);
    }


    public static class PrefsFragment extends PreferenceFragment  implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.preferences);
            update_remove_interest_values();
        }

        private void update_remove_interest_values() {
            MultiSelectListPreference interest_preference = (MultiSelectListPreference) findPreference("remove_interest_preference");
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            String all_interests=prefs.getString("user_interests","none");
            Gson gson = new Gson();
            if(all_interests.equals("none")){
                CharSequence entries[]=new CharSequence[]{};
                interest_preference.setEntries(entries);
                interest_preference.setEntryValues(entries);
            } else{
                System.out.println("ALL INTERESTS: "+all_interests);
                String[] entries = gson.fromJson(all_interests, String[].class);
                interest_preference.setEntries(entries);
                interest_preference.setEntryValues(entries);

            }
        }

        private boolean setInterests(SharedPreferences prefs, Set<String> courseInterests, Set<String> categoryInterests){
            Set<String> interests=new HashSet<>();
            if(courseInterests!=null) interests.addAll(courseInterests);
            if(categoryInterests!=null) interests.addAll(categoryInterests);

            String all_interests=prefs.getString("user_interests","none");
            Gson gson = new Gson();
            if(all_interests.equals("none")){
                String[] temp = new String[interests.size()];
                temp = interests.toArray(temp);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_interests", gson.toJson(temp));
                editor.commit();
                return true;
            }
            else {
                String[] interest_list = gson.fromJson(all_interests, String[].class);
                List<String> interest_array = new ArrayList(Arrays.asList(interest_list));
                if(interest_array.containsAll(interests) && interests.containsAll(interest_array)) return false;


                String[] temp = new String[interests.size()];
                temp = interests.toArray(temp);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_interests", gson.toJson(temp));
                editor.commit();


                return true;
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
        {
            if(IGNORE_KEYS.contains(key)) return;

            if(!prefs.getAll().containsKey(key)) return;

            switch(key){
                case "notification_on_off_preference": //TODO
                case "notification_saved_only_preference": //TODO
                    break;
                case "notification_time_preference": //TODO
                    Preference preference = findPreference(key);
                    preference.setSummary(((ListPreference) preference).getEntry());
                    break;
                case "add_course_interest_preference":
                case "add_category_interest_preference":
                    if(setInterests(prefs, prefs.getStringSet("add_course_interest_preference", null), prefs.getStringSet("add_category_interest_preference", null))){
                        update_remove_interest_values();
                        clear_interest_preference_values(prefs);
                    }
                    break;
                case "remove_interest_preference":
                    if(removeInterest(prefs,prefs.getStringSet(key,null))) {
                        clear_interest_preference_values(prefs);
                        update_remove_interest_values();
                    }
                    break;
                default:
                    System.out.println("ERROR: Key not found ("+key+")");

            }
            System.out.println("Preferences Changed: "+key);
            System.out.println(prefs.getAll());


        }
        private boolean removeInterest(SharedPreferences prefs, Set<String> interests){
            System.out.println("length: "+interests.size());
            if(interests==null || interests.size()==0) return false;

            String all_interests=prefs.getString("user_interests","none");
            Gson gson = new Gson();
            if(all_interests.equals("none")){
                return false;
            }
            else {
                String[] interest_list = gson.fromJson(all_interests, String[].class);
                List<String> interest_array = new ArrayList(Arrays.asList(interest_list));
                for (String interest:interests){
                    if (interest_array.contains(interest)) {
                        System.out.println(interest);
                        System.out.println(interest_array);
                        interest_array.remove(interest);
                    }
                }

                String[] temp = new String[interest_array.size()];
                temp = interest_array.toArray(temp);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_interests", gson.toJson(temp));
                editor.commit();
            }

            return true;
        }

        private void clear_interest_preference_values(SharedPreferences prefs) {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            MultiSelectListPreference remove_interest_preference = (MultiSelectListPreference) findPreference("remove_interest_preference");
            MultiSelectListPreference category_interest_preference = (MultiSelectListPreference ) findPreference("add_category_interest_preference");
            MultiSelectListPreference course_interest_preference = (MultiSelectListPreference ) findPreference("add_course_interest_preference");

            String all_interests=prefs.getString("user_interests","none");
            Gson gson = new Gson();
            if(all_interests.equals("none")){
                category_interest_preference.setValues(new HashSet<String>());
                course_interest_preference.setValues(new HashSet<String>());
            }
            else {
                String[] interest_list = gson.fromJson(all_interests, String[].class);
                Set<String> interest_array = new HashSet<>(Arrays.asList(interest_list));
                Set<String> new_category_preferences = new HashSet<>();
                Set<String> new_course_preferences = new HashSet<>();

                CharSequence[] course_pref=course_interest_preference.getEntryValues();
                for(int i=0;i<course_pref.length;i++){
                    String pref=course_pref[i].toString();
                    if(interest_array.contains(pref))new_course_preferences.add(pref);
                }

                CharSequence[] category_pref=category_interest_preference.getEntryValues();
                for(int i=0;i<category_pref.length;i++){
                    String pref=category_pref[i].toString();
                    if(interest_array.contains(pref))new_category_preferences.add(pref);
                }

                category_interest_preference.setValues(new_category_preferences);
                course_interest_preference.setValues(new_course_preferences);
            }

            remove_interest_preference.setValues(new HashSet<String>());



            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("remove_interest_preference");
            //editor.remove("add_category_interest_preference");
            //editor.remove("add_course_interest_preference");
            editor.commit();

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }


    }


}
