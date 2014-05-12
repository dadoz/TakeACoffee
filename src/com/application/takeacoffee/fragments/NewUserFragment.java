package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by davide on 01/04/14.
 */
public class NewUserFragment extends Fragment{
    private static final int PHOTO_CODE = 100;
    private static final String TAG = "NewUserFragment";

    private CoffeeMachineDataStorageApplication coffeeMachineApplication;

    private static File customDir;

    private Activity mainActivityRef;
    private SharedPreferences sharedPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = this.getActivity();

        coffeeMachineApplication = (CoffeeMachineDataStorageApplication)this.getActivity().getApplication();

        customDir = this.getActivity()
                .getApplication().getApplicationContext()
                .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;

        sharedPref = mainActivityRef.getPreferences(0);

        final View userView = inflater.inflate(R.layout.new_user_fragment, container, false);

        final EditText usernameEditText = (EditText) userView.findViewById(R.id.usernameNewUserEditTextId);

        User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();

        ImageView profilePic = (ImageView)userView .findViewById(R.id.profilePicImageViewId);
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
        //get change button and change to save button
//        LinearLayout headerchangeUserButton = (LinearLayout)mainActivityRef.findViewById(R.id.loggedUserButtonId);
//        headerchangeUserButton.setText("SAVE");
//        headerchangeUserButton.setBackground((getResources().getDrawable(R.drawable.button_rounded_shape_yellow)));

        //unbind loggedUserButtonId action
        mainActivityRef.findViewById(R.id.loggedUserButtonId).setOnClickListener(null);
        userView.findViewById(R.id.saveUserButtonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check and save
//                usernameEditText = (EditText) userView.findViewById(R.id.usernameNewUserEditTextId);
                String username = usernameEditText.getText().toString();
                Log.e(TAG, "u clicked save - " + username + " -");
                if (username == null || username.matches("")) {
                    Common.displayError("no username set - please insert your one", view.getContext());
                    return;
                }

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
                getFragmentManager().popBackStack();
            }
        });

        Common.setCustomFont(userView , getActivity().getAssets());
        return userView ;
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

        //reset button on prev status
        resetChangeButton();

    }

    public void resetChangeButton() {
        //set button to change user instead of new (with new bind)
//        ((Button)mainActivityRef.findViewById(R.id.loggedUserButtonId)).setText("change");
//        (mainActivityRef.findViewById(R.id.loggedUserButtonId)).setBackground((getResources().getDrawable(R.drawable.button_rounded_shape)));
        (mainActivityRef.findViewById(R.id.loggedUserButtonId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.displayError("change username", view.getContext());
                CoffeeMachineActivity.addChangeUserFragment(mainActivityRef.getFragmentManager());
            }
        });
    }

    public void setProfilePic() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PHOTO_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = null;
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap pickedImage = BitmapFactory.decodeStream(imageStream);
                        Log.d(TAG, "I got image as profilePic");
                        ImageView profilePic = (ImageView)this.getActivity().findViewById(R.id.profilePicImageViewId);
                        Bitmap profileImage = getRoundedRectBitmap(pickedImage, Common.PROFILE_PIC_CIRCLE_MASK_SIZE);

                        //store image in storage and get back URL
                        String profileImagePath = saveImageInStorage(profileImage);
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

                         profilePic.setImageBitmap(profileImage);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this.getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
        }
    }

    public static String saveImageInStorage(Bitmap profileImage) {
        File profilePicFile = new File(customDir, Common.PROFILE_PIC_FILE_NAME); //Getting a file within the dir.
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(profilePicFile);
            profileImage.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try{
                out.close();
            } catch(Throwable ignore) {
                return null;
            }
        }
//        Log.e(TAG, profilePicFile.getAbsolutePath());

        return profilePicFile.getAbsolutePath();
    }
    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int size) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, 0, 0, paint);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return result;
    }


    public static Bitmap getRoundedBitmap(int size, int color) {
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(size/2, size/2, size/2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bmp, 0, 0, paint);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return bmp;
    }
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

/*            Paint paint1 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setColor(colorArray[1]);
            canvas.drawArc(rectF, 45, 315, true, paint1); //rect starAngle sweepAngle useCenter paint
            paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
*/
            canvas.drawBitmap(bmp, 0, 0, paintCustom);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
        return bmp;
    }

}
