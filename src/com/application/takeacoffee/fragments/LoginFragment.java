package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;
import com.facebook.SessionState;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Created by davide on 01/04/14.
 */
public class LoginFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final int PHOTO_CODE = 100;
    private static final String TAG = "LoginFragment";

    private CoffeeMachineDataStorageApplication coffeeMachineApplication;

    private static File customDir;

    private FragmentActivity mainActivityRef;
    private SharedPreferences sharedPref;

    private View loginView;

    //FACEBOOK uiLifecycle
    private UiLifecycleHelper uiHelper;

    //GOOGLE PLUS
    private static final int REQUEST_CODE_RESOLVE_ERR = 1;
    // ...
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private ProgressDialog mConnectionProgressDialog;

    private Bundle savedInstance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstance){
        mainActivityRef = this.getActivity();
        this.savedInstance = savedInstance;

        coffeeMachineApplication = (CoffeeMachineDataStorageApplication)this.getActivity().getApplication();

        customDir = this.getActivity()
                .getApplication().getApplicationContext()
                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;

        sharedPref = mainActivityRef.getPreferences(0);

        loginView = inflater.inflate(R.layout.login_fragment, container, false);

        final EditText usernameEditText = (EditText) loginView.findViewById(R.id.usernameNewUserEditTextId);

        User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();

        ImageView profilePic = (ImageView) loginView .findViewById(R.id.profilePicImageViewId);
        if(user != null) {
            Common.drawProfilePictureByPath(profilePic, user.getProfilePicturePath(),
                    getResources().getDrawable(R.drawable.user_icon));
            Log.e(TAG, "failed to load profile pic from storage - load the guest one");
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfilePic();
            }
        });
        //set old username on editText
        if (user != null) {
            usernameEditText.setText(user.getUsername());
        }

        //unbind loggedUserButtonId action
        mainActivityRef.findViewById(R.id.loggedUserButtonId).setOnClickListener(null);
        loginView.findViewById(R.id.saveUserButtonId).setOnClickListener(this);


        /**FB INTEGRATION**/
        loginView.findViewById(R.id.facebookLoginButtonId).setOnClickListener(this);
        /**GOOGLEPLUS INTEGRATION**/
        loginView.findViewById(R.id.googlePlusLoginButtonId).setOnClickListener(this);

        setHeader();
        Common.setCustomFont(loginView, getActivity().getAssets());
        return loginView ;
    }

    public void setHeader() {
        CoffeeMachineActivity.hideAllItemsOnHeaderBar();
    }

    public void updateUserBox(String username) {
        Bitmap bitmap;
        //set profile pic image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(user != null && user.getProfilePicturePath() != null) {
            bitmap = BitmapFactory.decodeFile(user.getProfilePicturePath(), options);
        } else {
            Log.e(TAG, "failed to load profile pic from storage - load the guest one");
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user_icon);
        }

        ((ImageView)mainActivityRef.findViewById(R.id.loggedUserImageViewId)).setImageBitmap(bitmap);
        //set username
        ((TextView)mainActivityRef.findViewById(R.id.loggedUserTextId)).setText(username);
    }

    public void setProfilePic() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_CODE);
    }

    public void setAndsaveProfilePictureByPicture(Bitmap pickedImage, ImageView profilePicView) {
        Bitmap profileImage = Common.getRoundedRectBitmap(pickedImage, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);

        //store image in storage and get back URL
        String profileImagePath = Common.saveImageInStorage(profileImage, customDir);
        if(profileImagePath == null) {
            Log.e(TAG, "image not stored in storage");
        } else {
            //store url in application
            Log.d(TAG, "[PROFILE PIC] path file - " + profileImagePath);
            User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();

            if(user != null) {
                user.setProfilePicturePath(profileImagePath);
            } else {
                coffeeMachineApplication.coffeeMachineData.initRegisteredUserByProfilePicturePath(profileImagePath);
            }
//                            coffeeMachineApplication.coffeeMachineData.setProfilePicturePath(profileImagePath);
            sharedPref.edit().putString(Common.SHAREDPREF_PROFILE_PIC_FILE_NAME, profileImagePath).commit();
        }

        profilePicView.setImageBitmap(profileImage);

        if(mConnectionProgressDialog != null) {
            mConnectionProgressDialog.dismiss();
        }
    }

    /*
    public static Bitmap getPieChartBitmap(int size, ArrayList<PieChart> pieChartList) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bmp);
            RectF rectF = new RectF(0, 0, size, size);
            canvas.drawARGB(0, 0, 0, 0);
            Paint paintCustom = new Paint();
            paintCustom.setAntiAlias(true);
            paintCustom.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            int startAngle = 0;
            for(PieChart pieChartObj : pieChartList) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
//                Integer sweepAngle = pieChartMap.get(color);
                paint.setColor(pieChartObj.getColor());
                canvas.drawArc(rectF, startAngle, pieChartObj.getData(), true, paint); //rect starAngle sweepAngle useCenter paint
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                //start angle update
                startAngle += pieChartObj.getData();
            }
            canvas.drawBitmap(bmp, 0, 0, paintCustom);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return bmp;
    }
*/

    public class LoggedUserButtonAction implements View.OnClickListener {
        android.support.v4.app.FragmentManager fragmentManager;
        public LoggedUserButtonAction(android.support.v4.app.FragmentManager fragManag) {
            this.fragmentManager = fragManag;
        }

        @Override
        public void onClick(View view) {
            fragmentManager.beginTransaction()
                .replace(R.id.coffeeMachineContainerLayoutId, new LoginFragment(), Common.NEW_USER_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
        }
    }


    /***** FACEBOOK LOGIN *****/


    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Signing in...");

        Log.e(TAG, "status fb closed: " + state.isClosed() + " - open: " + state.isOpened());
            if (state.isOpened()) {
                Log.i(TAG, "Logged in... Facebook");
                pd.show();

                //get fb userId
                Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        if(response == null || response.getGraphObject() == null ||
                                session != Session.getActiveSession() || user == null) {
                            Log.e(TAG, "error - response null from FB stateChange"); //IMPROVEMENT check if there is internet connection
                            Common.displayError("failed to get data from facebook", getActivity());
                            session.close();
                            pd.cancel();
                            return;
                        }

                        String userId = user.getId();
                        String username = user.getFirstName();

                        ((EditText) mainActivityRef.findViewById(R.id.usernameNewUserEditTextId)).setText(username);
                        Log.e(TAG, "userId" + userId + " -- " + username);

                        //profile pic
                        Bundle params = new Bundle();
                        params.putBoolean("redirect", false);
                        params.putString("height", "300");
                        params.putString("type", "normal");
                        params.putString("width", "300");
                        new Request(session,
                                "/me/picture",
                                params,
                                HttpMethod.GET,
                                new Request.Callback() {

                                    @Override
                                    public void onCompleted(Response response) {
                                        Log.e(TAG, "response" + response);

                                        if(response == null || response.getGraphObject() == null ||
                                                session != Session.getActiveSession()) {
                                            Log.e(TAG, "error - response null from FB stateChange"); //IMPROVEMENT check if there is internet connection
                                            Common.displayError("failed to get data from facebook", getActivity());
                                            session.close();
                                            pd.cancel();
                                            return;
                                        }

                                        JSONObject dataJSON = (JSONObject) response.getGraphObject().getProperty("data");

                                        try {

                                            Log.e(TAG, "url of my profile pic -" + dataJSON.getString("url"));
                                            URL profilePicURL = new URL(dataJSON.getString("url"));
                                            Bitmap profilePicture = new ProfilePictureAsyncTask().execute(profilePicURL).get();
                                            ImageView profilePicView = (ImageView) mainActivityRef.findViewById(R.id.profilePicImageViewId);
                                            setAndsaveProfilePictureByPicture(profilePicture, profilePicView);
                                            /***LOGOUT FROM FB**/
                                            session.close();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                        session.close();
                                        pd.cancel();
                                    }
                                }
                        ).executeAsync();
                    }
                });
                Request.executeBatchAsync(request);

            } else if (state.isClosed()) {
                Log.i(TAG, "Logged out...");
            }
    }

    private class ProfilePictureAsyncTask extends AsyncTask<URL, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... urls) {
            Bitmap profilePicture;
            try {
                profilePicture = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return profilePicture;
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    /***** GOOGLE PLUS *****/

    private void googlePlusOnCreate() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(getActivity());
        mConnectionProgressDialog.setMessage("Signing in...");

    }


    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getName().getGivenName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

//                txtName.setText(personName);
//                txtEmail.setText(email);

                ((EditText) loginView.findViewById(R.id.usernameNewUserEditTextId)).setText(personName);


                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + Common.PROFILE_PIC_SIZE;

                ImageView imgProfilePic = (ImageView) loginView.findViewById(R.id.profilePicImageViewId);
                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getActivity(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mConnectionProgressDialog.dismiss();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            setAndsaveProfilePictureByPicture(result, bmImage);
            if(mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
            }
//            bmImage.setImageBitmap(result);
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Login successful - google plus");
//        mConnectionProgressDialog.dismiss();
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mConnectionProgressDialog.isShowing()) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            }
        }
        mConnectionResult = connectionResult;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        googlePlusOnCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                //mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.googlePlusLoginButtonId) {
            if(mGoogleApiClient != null) {
                mGoogleApiClient.connect();
                if(!mGoogleApiClient.isConnected()) {
                    if (mConnectionResult == null) {
                        mConnectionProgressDialog.show();
                    } else {
                        try {
                            mConnectionResult.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLVE_ERR);
                        } catch (IntentSender.SendIntentException e) {
                            // Try connecting again.
                            mConnectionResult = null;
                            mGoogleApiClient.connect();
                        }
                    }
                }
            }
        } else if (view.getId() == R.id.facebookLoginButtonId) {
            Fragment currentFragment = getFragmentManager().getFragments().get(
                    getFragmentManager().getFragments().size() - 1);
            uiHelper.onCreate(savedInstance);
            Session session = Session.getActiveSession();
            if (session!= null && !session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(currentFragment)
                        .setCallback(callback));
            } else {
                Session.openActiveSession(getActivity(), currentFragment, true, callback);
            }

        } else if (view.getId() == R.id.saveUserButtonId) {
            //check and save
            final EditText usernameEditText = (EditText) loginView.findViewById(R.id.usernameNewUserEditTextId);
            String username = usernameEditText.getText().toString();
            Log.e(TAG, "u clicked save - " + username + " -");
            if (username == null || username.matches("")) {
                Common.displayError("no username set - please insert your one", view.getContext());
                return;
            }
            //rebind loggedButtonId
            mainActivityRef.findViewById(R.id.loggedUserButtonId).setOnClickListener(
                    new LoggedUserButtonAction(mainActivityRef.getSupportFragmentManager()));

            //hide keyboard
            InputMethodManager imm = (InputMethodManager) mainActivityRef.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);

            updateUserBox(username);
            sharedPref.edit().putString(Common.SHAREDPREF_REGISTERED_USERNAME, username).commit();
            User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
            if (user == null) {
                coffeeMachineApplication.coffeeMachineData.initRegisteredUserByUsername(username);
            } else {
                user.setUsername(username);
            }

//                Log.e(TAG, getFragmentManager().getBackStackEntryCount() + " frag size");
            if(getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                getFragmentManager().beginTransaction()
                        .replace(R.id.coffeeMachineContainerLayoutId, new CoffeeMachineFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PHOTO_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        InputStream imageStream = null;
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap pickedImage = BitmapFactory.decodeStream(imageStream);
                        Log.d(TAG, "I got image as profilePic");
                        ImageView profilePic = (ImageView)this.getActivity().findViewById(R.id.profilePicImageViewId);
                        setAndsaveProfilePictureByPicture(pickedImage, profilePic);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this.getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_RESOLVE_ERR:
                Log.i(TAG, "requestCode == REQUEST_CODE_RESOLVE_ERR. responseCode = " + resultCode);
                if(resultCode == Activity.RESULT_OK) {
                    if(mGoogleApiClient != null) {
                        if (!mGoogleApiClient.isConnecting()) {
                            mGoogleApiClient.connect();
                        }
                    }
                }
                break;
        }
    }

}
