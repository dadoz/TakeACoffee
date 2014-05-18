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

        //check empty listview
        ArrayList<Review> reviewList = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSET);
        if(reviewList == null) {
            //EMPTY listview
            View emptyView = inflater.inflate(R.layout.empty_data_layout, container, false);
            //set review header (coffee machine name)
            setHeaderReview(getFragmentManager(), args, coffeeMachineApplication, coffeeMachineId, emptyView);
            //set custom font
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }

        //data are stored in reviewList
        View reviewsLayoutView = inflater.inflate(R.layout.reviews_fragment, container, false);
        initView(reviewsLayoutView, coffeeMachineId);

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
        (reviewsLayoutView.findViewById(R.id.addReviewImageViewId))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addReviewByFragment(fragmentManager, args);
                    }
                });
    }

    public static void addReviewByFragment(FragmentManager fragmentManager, Bundle args) {
        AddReviewFragment addReviewFragment = new AddReviewFragment();
        addReviewFragment.setArguments(args);
        //add fragment content to add user
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, addReviewFragment,
                        Common.ADD_REVIEW_FRAGMENT_TAG)
                .addToBackStack("back")
                .commit();
    }

    public static ArrayList<Review> getReviewData(String coffeeMachineId,
                                                  CoffeeMachineDataStorageApplication coffeeMachineApplication,
                                                  Common.ReviewStatusEnum reviewStatus) {
        if(coffeeMachineId != null) {
            //check if coffeMachineId exist -
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

    private void initView(final View reviewLayoutView, String coffeeMachineId) {
        boolean isListViewEmpty = false;
        Animation anim = AnimationUtils.loadAnimation(mainActivityRef, R.anim.zoom_in);
        anim.setDuration(Common.ANIMATION_GROW_TIME);

        /****GOOD*****/
        ImageView goodReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.goodReviewButtonId);
        ArrayList<Review> reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.GOOD);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            goodReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            goodReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_green_sample));
        }
        goodReviewButton.startAnimation(anim);
//        goodReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_green)));
        goodReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                Common.ReviewStatusEnum.GOOD));

        /****NOTSOBAD*****/
        isListViewEmpty = false;
        ImageView badReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.badReviewButtonId);
        badReviewButton.startAnimation(anim);
        reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.NOTSOBAD);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            badReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            badReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_yellow_sample));
        }
        //      badReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_yellow)));
        badReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                                                                             Common.ReviewStatusEnum.NOTSOBAD));

        /****WORST*****/
        isListViewEmpty = false;
        ImageView worstReviewButton = (ImageView)reviewLayoutView.findViewById(R.id.worstReviewButtonId);
        //worstReviewButton.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.light_black)));
        reviewListTemp = getReviewData(coffeeMachineId, coffeeMachineApplication,
                Common.ReviewStatusEnum.WORST);
        if(reviewListTemp == null || reviewListTemp.size() == 0) {
            worstReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_empty));
            isListViewEmpty = true;
        } else {
            worstReviewButton.setImageDrawable(getResources().getDrawable(R.drawable.coffe_cup_icon_violet_sample));
        }
        worstReviewButton.startAnimation(anim);
        worstReviewButton.setOnClickListener(new ReviewListButtonListener(isListViewEmpty,
                Common.ReviewStatusEnum.WORST));


        /***LOAD MORE REVIEW**/
        ImageView prevReviewsImageView = (ImageView)reviewLayoutView.findViewById(R.id.prevReviewsButtonId);
        prevReviewsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.displayError("load more reviews", mainActivityRef);
            }
        });
    }

    public static void createReviewsListView(FragmentManager fragManager, Common.ReviewStatusEnum reviewStatus, Bundle args) {
        //customDialog.setContentView(R.layout.dialog_review_layout);
        ReviewListFragment fragmentObj = new ReviewListFragment();
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());
        fragmentObj.setArguments(args);

        fragManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, fragmentObj)
                .addToBackStack("back")
                .commit();
    }


    public class ReviewListButtonListener implements View.OnClickListener {
        private boolean isListViewEmpty;
        private Common.ReviewStatusEnum reviewStatus;

        public ReviewListButtonListener(boolean isListViewEmpty, Common.ReviewStatusEnum reviewStatus) {
            this.isListViewEmpty = isListViewEmpty;
            this.reviewStatus = reviewStatus;
        }

        @Override
        public void onClick(View view) {
            if(isListViewEmpty) {
                Common.displayError("still no one review", mainActivityRef);
//                addReviewWithTextDialog();
            } else {
                createReviewsListView(getFragmentManager(), this.reviewStatus, args);
            }
        }
    }

/*    public Dialog addReviewWithTextDialog() {
        final Dialog dialog = new Dialog(mainActivityRef);
        dialog.setContentView(R.layout.dialog_add_text_on_review);
        dialog.findViewById(R.id.addReviewDialogButtonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewText = ((EditText)dialog.findViewById(R.id.textReviewDialogEditTextId))
                        .getText().toString();
                if(reviewText.trim() != "") {
                    Common.displayError("this is my text" + reviewText, mainActivityRef);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        return dialog;
    };*/
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
//        prevReviewsImageView.setImageBitmap(getRoundedBitmap(Common.ICON_SMALL_SIZE, getResources().getColor(R.color.middle_grey)));
//        prevReviewsImageView.setImageDrawable(getResources().getDrawable(R.drawable.loading_icon));
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

