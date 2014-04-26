package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.takeacoffee.R;

import java.util.ArrayList;

import static com.application.takeacoffee.fragments.NewUserFragment.getRoundedBitmap;

/**
 * Created by davide on 3/16/14.
 */
public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewFragment";
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private Bundle args;
    private static Activity mainActivityRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);

        //change fragment
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);


        ArrayList<Review> reviewList = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSET);
        if(reviewList == null) {
            Log.d(TAG,"this is the getReviewData EMPTY");
            View emptyView = inflater.inflate(R.layout.empty_data_layout, container, false);
            //set review header (coffee machine name)
            setHeaderReview(getFragmentManager(), args, coffeeMachineApplication, coffeeMachineId, emptyView);
            //set custom font
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }
        Log.d(TAG,"this is the getReviewData filled :D");

        //data are stored in reviewList
        View reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);
        initView(reviewsLayoutView);

        //set review header (coffee machine name)
        setHeaderReview(getFragmentManager(), args, coffeeMachineApplication, coffeeMachineId, reviewsLayoutView);

        //set custom font
        Common.setCustomFont(reviewsLayoutView, this.getActivity().getAssets());
        return reviewsLayoutView;
    }

    static void setHeaderReview(final FragmentManager fragmentManager,
                                final Bundle args, CoffeeMachineDataStorageApplication coffeeMachineApplication,
                                String coffeeMachineId, View reviewsLayoutView) {
        String coffeeMachineName = coffeeMachineApplication.coffeeMachineData
                .getCoffeMachineById(coffeeMachineId).getName();
        if(coffeeMachineName != null) {
            ((TextView)reviewsLayoutView.findViewById(R.id.coffeeMachineNameReviewTextId))
                    .setText(coffeeMachineName);
        }

        //add review button
//        Bitmap bmp = getRoundedBitmap(80, mainActivityRef.getResources().getColor(R.color.middle_grey));
//        Bitmap bmp3 = Common.overlayBitmaps(bmp, BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.croice));
//        ((ImageView) reviewsLayoutView.findViewById(R.id.addReviewImageViewId)).setImageBitmap(bmp3);
        (reviewsLayoutView.findViewById(R.id.addReviewImageViewId))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFragment(fragmentManager, args);
                    }
                });
    }

    private static void changeFragment(FragmentManager fragmentManager, Bundle args) {
        AddReviewFragment addReviewFragment = new AddReviewFragment();
        addReviewFragment.setArguments(args);
        //add fragment content to add user
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                .replace(R.id.coffeeMachineContainerLayoutId, addReviewFragment)
                .addToBackStack("back")
                .commit();
    }

    public static ArrayList<Review> getReviewData(String coffeeMachineId,
                                                  CoffeeMachineDataStorageApplication coffeeMachineApplication,
                                                  Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId != null) {
            //check if coffeMachineId exist -
            // I DONT KNOW if its better to use this fx instead of impl method on list
            ArrayList<Review> reviewList = coffeeMachineApplication.coffeeMachineData.
                    getReviewListByCoffeMachineId(coffeeMachineId);
            if(reviewList == null || reviewList.size() == 0) {
                Log.e(TAG,"error - no one coffeeMachine owned by this ID");
                return null;
            }

            if(reviewStatus != Common.ReviewStatusEnum.NOTSET) {
                ArrayList<Review> reviewListSortedByStatus = new ArrayList<Review>();
                //TODO to be refactored
                for(Review review : reviewList) {
                    if(reviewStatus == review.getStatus()) {
                        reviewListSortedByStatus.add(review);
                    }
                }
                if(reviewListSortedByStatus.size() == 0) {
                    return null;
                }
                return reviewListSortedByStatus;
            }
            return reviewList;

        }
        Log.e(TAG, "error - no coffeMachineId found");
        return null;
    }

    private void initView(final View reviewLayoutView) {
        Animation anim = AnimationUtils.loadAnimation(mainActivityRef, R.anim.zoom_in);
        anim.setDuration(Common.ANIMATION_GROW_TIME);
        ImageView goodReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.goodReviewButtonId);
//        goodReviewButton.startAnimation(anim);
        goodReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_green)));
        goodReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked good");
//                createReviewsListDialog();
                createReviewsListView(getFragmentManager(), Common.ReviewStatusEnum.GOOD, args);
            }
        });
        ImageView badReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.badReviewButtonId);
//        badReviewButton.startAnimation(anim);
        badReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_yellow)));
        badReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked bad");
//                createReviewsListDialog();
                createReviewsListView(getFragmentManager(), Common.ReviewStatusEnum.NOTSOBAD, args);
            }
        });
        ImageView worstReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.worstReviewButtonId);
//        worstReviewButton.startAnimation(anim);
        worstReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_black)));
        worstReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"clicked worst");
//                createReviewsListDialog();
                createReviewsListView(getFragmentManager(), Common.ReviewStatusEnum.WORST, args);
            }
        });

        //load more review
        ImageView prevReviewsImageView = (ImageView)reviewLayoutView.findViewById(R.id.prevReviewsButtonId);
        prevReviewsImageView.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.middle_grey)));
/*
        LinearLayout linearLayoutContainer = (LinearLayout)reviewLayoutView.findViewById(R.id.tableRowReviewCheckerContainerId);
        TextIconCustom customImageView = new TextIconCustom(reviewLayoutView.getContext(), 100, Color.WHITE, "2", 100, getResources().getColor(R.color.green_marine));
        customImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayoutContainer.addView(customImageView, 300, 300);
*/


/*        Button addReviewBtn = (Button)findViewById(R.id.addReviewButtonId);
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(!coffeMachineApplication.coffeeMachineData.getRegisteredUserStatus()){
                    Log.e(TAG, ">>> please insert username at least");

                    DialogFragment dialog = new RegisterUserDialogFragment();
                    dialog.show(getFragmentManager(), TAG);
                } else {
                    Intent intent = new Intent(ReviewsActivity.this,AddReviewActivity.class);
                    startActivityForResult(intent, ADD_REVIEW_RESULT);
                }
            }
        });
*/
    }

    public static void createReviewsListView(FragmentManager fragManager, Common.ReviewStatusEnum reviewStatus, Bundle args) {
        //customDialog.setContentView(R.layout.dialog_review_layout);
        ReviewListFragment fragmentObj = new ReviewListFragment();
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
        fragmentObj.setArguments(args);

        fragManager.beginTransaction()
                .setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                .addToBackStack("back")
                .commit();
    }

/*
    public void createReviewsListDialog() {
        final Dialog customDialog = new Dialog(getActivity());
        customDialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_review_layout);
        setDataToReviewDialog(customDialog, false);
        View dialogView = customDialog.findViewById(R.id.dialogReviewLayoutId);
        Common.setCustomFont(dialogView,this.getActivity().getAssets());
        Log.e(TAG,"clicked worst");

        customDialog.show();
/*        Button dismissDialogButton = (Button)customDialog.findViewById(R.id.dismissDialogButtonId);
        dismissDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
    }
*/


}

