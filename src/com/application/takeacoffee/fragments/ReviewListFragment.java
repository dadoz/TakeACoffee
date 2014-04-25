package com.application.takeacoffee.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.view.Window;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.R;

import java.util.ArrayList;

import static com.application.commons.Common.ReviewStatusEnum.GOOD;
import static com.application.takeacoffee.CoffeeMachineActivity.setProfilePicFromStorage;

/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment {
    private static final String TAG = "ReviewListFragment";
    private CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private View reviewModifiedViewStorage = null;
    private Review reviewModifiedObjStorage = null;
    private Activity mainActivityRef = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        View reviewListView = inflater.inflate(R.layout.review_list_fragment, container, false);
        View modifyReviewView = inflater.inflate(R.layout.modify_review_template, container, false);
        View editReviewView = inflater.inflate(R.layout.edit_review_template, container, false);
        View emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);

        //get data from application
        coffeeMachineApplication = (CoffeeMachineDataStorageApplication) this.getActivity().getApplication();

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        Common.ReviewStatusEnum reviewStatus = Common.ReviewStatusEnum.valueOf(
                (String)this.getArguments().get(Common.REVIEW_STATUS_KEY));
        ArrayList<Review> reviewList = ReviewsFragment.getReviewData(coffeeMachineId, coffeeMachineApplication,
                reviewStatus);

        Bundle args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

        if(reviewList == null) {
            Log.d(TAG,"this is the getReviewData EMPTY");
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            //ReviewsFragment.setHeaderReview(getFragmentManager(), args,
            //        coffeeMachineApplication, coffeeMachineId, emptyView);
            return emptyView;
        }

        setReviewStatus(reviewListView, reviewStatus);

        initModifyReviewView(modifyReviewView, editReviewView, reviewList, coffeeMachineId);
        setDataToReviewList(reviewListView, modifyReviewView, reviewList);

        Common.setCustomFont(reviewListView, getActivity().getAssets());
        Common.setCustomFont(editReviewView, getActivity().getAssets());
        Common.setCustomFont(modifyReviewView, getActivity().getAssets());
        return reviewListView;
        //return null;
    }

    private void setReviewStatus(final View reviewListView, final Common.ReviewStatusEnum reviewStatus) {
        //set changes icon
        String labelStatus = " - ";
        int colorViewStatus = 0;
        switch (reviewStatus) {
            case GOOD:
                labelStatus = "Good Reviews";
                colorViewStatus = getResources().getColor(R.color.light_green);
                break;
            case NOTSOBAD:
                labelStatus = "Not so bad Reviews";
                colorViewStatus = getResources().getColor(R.color.light_yellow);
                break;
            case WORST:
                labelStatus = "Terrible Reviews";
                colorViewStatus = getResources().getColor(R.color.light_black);
                ((TextView)reviewListView.findViewById(R.id.reviewStatusTextViewId)).setTextColor(
                        getResources().getColor(R.color.light_grey));
                break;
        }

        ((TextView)reviewListView.findViewById(R.id.reviewStatusTextViewId)).setText(labelStatus);
        (reviewListView.findViewById(R.id.reviewStatusTextViewId)).setBackgroundColor(colorViewStatus);

    }

    private void initEditReviewView(final View editReviewView) {
        //set profile pic
        String profilePicPath = reviewModifiedObjStorage.getProfilePicPath(); // TODO to must be refactored
        if(profilePicPath != null) {
            setProfilePicFromStorage((ImageView)editReviewView.findViewById(R.id.profilePicReviewTemplateId));
        }

        String reviewComment = ((TextView)reviewModifiedViewStorage.findViewById(R.id.reviewCommentTextId)).getText().toString();
        ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).setText(reviewComment);

        editReviewView.findViewById(R.id.saveReviewButtonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "hey u want to save your review");
                String reviewCommentNew = ((EditText)editReviewView.findViewById(R.id.reviewCommentEditTextId)).getText().toString();
                reviewModifiedObjStorage.setComment(reviewCommentNew);
                ViewGroup parent = (ViewGroup)view.getParent().getParent();
                int childIndexPosition = parent.indexOfChild(editReviewView);
                parent.removeView(editReviewView);
                ((TextView)reviewModifiedViewStorage.findViewById(R.id.reviewCommentTextId)).setText(reviewCommentNew);
                parent.addView(reviewModifiedViewStorage, childIndexPosition);

            }
        });
    }

    private void setModifyReviewViewStatusIcon(final View modifyReviewView) {
        //set changes icon
        int drawable = R.drawable.good_circle_icon;
        switch (reviewModifiedObjStorage.getStatus()) {
            case GOOD:
                drawable = R.drawable.good_circle_icon;
                break;
            case NOTSOBAD:
                drawable = R.drawable.bad_circle_icon;
                break;
            case WORST:
                drawable = R.drawable.worst_circle_icon;
                break;
        }
        ((ImageView)modifyReviewView.findViewById(R.id.changeTypeIconImageViewId))
                .setImageDrawable(getResources().getDrawable(drawable));
        (modifyReviewView.findViewById(R.id.changeTypeIconImageViewId)).getLayoutParams().height = (int)getResources()
                .getDimension(R.dimen.button_height);
        (modifyReviewView.findViewById(R.id.changeTypeIconImageViewId)).getLayoutParams().width = (int)getResources()
                .getDimension(R.dimen.button_height);
    }

    private void initModifyReviewView(final View modifyReviewView, final View editReviewView, final ArrayList<Review> reviewList, final String coffeeMachineId) {
        (modifyReviewView.findViewById(R.id.modifyReviewEditLayoutId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EDIT ACTION
//                Common.displayError("hey u want to edit your review", getActivity());
                //reviewModfiedBackStorage

                ViewGroup parent = (ViewGroup) view.getParent().getParent();
                int childIndexPosition = parent.indexOfChild(modifyReviewView);
                parent.removeView(modifyReviewView);
                parent.addView(editReviewView, childIndexPosition);
                initEditReviewView(editReviewView);
            }

        });
        (modifyReviewView.findViewById(R.id.modifyReviewDeleteLayoutId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DELETE ACTION
//                Common.displayError("hey u want to delete your review", getActivity());
                alertDialogDeleteReview(modifyReviewView, reviewList, coffeeMachineId);
            }
        });
        (modifyReviewView.findViewById(R.id.modifyReviewChangeTypeLayoutId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CHANGE TYPE ACTION
                choiceTypeOfReviewDialog(modifyReviewView, coffeeMachineId);
//                (modifyReviewView, reviewList, coffeeMachineId);
            }
        });
        (modifyReviewView.findViewById(R.id.modifyReviewBackImageViewId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //undo ACTION
                LinearLayout parent = (LinearLayout)modifyReviewView.getParent();
                int childIndexPosition = parent.indexOfChild(modifyReviewView);
                parent.removeView(modifyReviewView);
                if(reviewModifiedViewStorage != null) {
                    parent.addView(reviewModifiedViewStorage, childIndexPosition);

                    reviewModifiedViewStorage = null; //clean stored view
                }

            }
        });

    }

    public void alertDialogDeleteReview(final View modifyReviewView, final ArrayList<Review> reviewList,
                                        final String coffeeMachineId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Delete Review");
        dialogBuilder.setMessage("Are you sure you want to delete your review?");

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LinearLayout parent = (LinearLayout)modifyReviewView.getParent();
                parent.removeView(modifyReviewView);
                reviewList.remove(reviewModifiedObjStorage);
                coffeeMachineApplication.coffeeMachineData
                        .removeReviewByCoffeeMachineId(coffeeMachineId, reviewModifiedObjStorage);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        Dialog dialog = dialogBuilder.create();
        dialog.show();

    }


    private void choiceTypeOfReviewDialog(final View modifyReviewView, final String coffeeMachineId) {
        // custom dialog
        final Dialog dialog = new Dialog(mainActivityRef);
        dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        View choiceTypeReviewDialog = mainActivityRef.getLayoutInflater().inflate(R.layout.choice_type_review_dialog, null, false);
        Common.setCustomFont(choiceTypeReviewDialog, mainActivityRef.getAssets());
        dialog.setContentView(choiceTypeReviewDialog);
        //set action to dialog components
        final ArrayList<Review> reviewList = coffeeMachineApplication.coffeeMachineData.getReviewListByCoffeMachineId(coffeeMachineId);
        final int index = reviewList.indexOf(reviewModifiedObjStorage);

        choiceTypeReviewDialog.findViewById(R.id.choiceGoodReviewLayoutId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewModifiedObjStorage.setFeedback(GOOD);
                        (reviewList.get(index)).setFeedback(GOOD);
                        setModifyReviewViewStatusIcon(modifyReviewView);
                        dialog.dismiss();
                    }
                });
        choiceTypeReviewDialog.findViewById(R.id.choiceNotBadReviewLayoutId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewModifiedObjStorage.setFeedback(Common.ReviewStatusEnum.NOTSOBAD);
                        (reviewList.get(index)).setFeedback(Common.ReviewStatusEnum.NOTSOBAD);
                        setModifyReviewViewStatusIcon(modifyReviewView);

                        dialog.dismiss();
                    }
                });
        choiceTypeReviewDialog.findViewById(R.id.choiceWorstReviewLayoutId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewModifiedObjStorage.setFeedback(Common.ReviewStatusEnum.WORST);
                        (reviewList.get(index)).setFeedback(Common.ReviewStatusEnum.WORST);
                        setModifyReviewViewStatusIcon(modifyReviewView);
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    public void setDataToReviewList(View customView, final View modifyReviewView, ArrayList<Review> reviewList) {
        //TODO replace with a listView please
        try {
            int reviewCounter = 0;
            User user = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
            for(final Review reviewObj : reviewList) {

                final LayoutInflater inflater = (LayoutInflater)this.getActivity().
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout layoutContainer;
                layoutContainer = (LinearLayout)(customView).findViewById(R.id.reviewsContainerLayoutId);

                final View reviewTemplate = inflater.inflate(R.layout.review_template, null);
                if(reviewCounter % 2 != 0) {
                    reviewTemplate.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }
                layoutContainer.addView(reviewTemplate);
                //set data to template
                ((TextView)reviewTemplate.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
                ((TextView)reviewTemplate.findViewById(R.id.reviewDateTextId)).setText(reviewObj.getFormattedTimestamp());
                ((TextView)reviewTemplate.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());

                String profilePicPath = reviewObj.getProfilePicPath(); // TODO to must be refactored
                if(profilePicPath != null) {
                    //set profile pic
                    setProfilePicFromStorage((ImageView)reviewTemplate.findViewById(R.id.profilePicReviewTemplateId));
                }
                //check my post and add action
                Log.e(TAG, "this is one of my userId" + reviewObj.getUserId() + user.getId());
                if(reviewObj.getUserId() == user.getId()) {
                    Log.e(TAG, "this is one of my post");
                    reviewTemplate.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
//                            Common.displayError("add rm or change post", getActivity());
                            try {
                                Common.vibrate(getActivity(), Common.VIBRATE_TIME);
                                LinearLayout parent = (LinearLayout)view.getParent();
                                reviewModifiedViewStorage = reviewTemplate; //store temporary my replaced view
                                reviewModifiedObjStorage = reviewObj;
                                //parent.startViewTransition(reviewTemplate);
                                int childIndexPosition = parent.indexOfChild(reviewTemplate);
                                parent.removeView(reviewTemplate);
                                parent.addView(modifyReviewView, childIndexPosition); // add modify view - set changes :D
                                setModifyReviewViewStatusIcon(modifyReviewView);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                }
                reviewCounter++;
            }
        } catch (Exception e) {
            Log.e(TAG, "error - review list not available");
            e.printStackTrace();
        }
    }

}
