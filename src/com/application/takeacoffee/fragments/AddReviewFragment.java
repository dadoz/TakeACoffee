package com.application.takeacoffee.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import static com.application.adapters.ChoiceReviewFragment.createReviewsListView;


/**
 * Created by davide on 05/04/14.
 */
public class AddReviewFragment extends Fragment {
    private static final String TAG = "AddReviewFragment";
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;

    private static FragmentActivity mainActivityRef;

    private View addReviewView, addReviewButton;
    private boolean addReviewFromListView;

    public static AddReviewFragment create(int pageNumber, String coffeeMachineId) {
        Log.e(TAG, "static create of " + pageNumber);
        AddReviewFragment fragment = new AddReviewFragment();
        Bundle args = new Bundle();
        args.putInt(Common.ARG_PAGE, pageNumber);
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        fragment.setArguments(args);
        //mPageNumber = pageNumber;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)  {
        mainActivityRef = getActivity();
        addReviewView = inflater.inflate(R.layout.add_review_fragment_alternative, container, false);
        addReviewButton = addReviewView.findViewById(R.id.addReviewButtonId);

        //get data from application
        coffeeMachineApplication = ((CoffeeMachineDataStorageApplication) this.getActivity()
                .getApplication());

        //get args from fragment
        String coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        Boolean obj = (Boolean)this.getArguments().get(Common.ADD_REVIEW_FROM_LISTVIEW);
        if(obj == null) {
            obj = new Boolean(false);
        }
        int pageNumber = this.getArguments().getInt(Common.ARG_PAGE);


        addReviewFromListView = obj.booleanValue();
        (addReviewView.findViewById(R.id.addReviewUndoImageId)).setOnClickListener(new AddMoreTextAction());
        (addReviewView.findViewById(R.id.addMoreTextOnReviewButtonId)).setOnClickListener(new AddMoreTextAction());

//        SwipeDetector swipeDetector = new SwipeDetector(coffeeMachineId, reviewStatus);
   //     addReviewView.findViewById(R.id.containerAddReviewId).setOnTouchListener(swipeDetector);

        Log.d(TAG, "pageCounter" + pageNumber);
        Common.displayError("page " + pageNumber, mainActivityRef);
        Common.ReviewStatusEnum reviewStatus = Common.parseStatusFromPageNumber(pageNumber);
        initView(coffeeMachineId, reviewStatus);
        Common.setCustomFont(addReviewView, getActivity().getAssets());
        return addReviewView;
    }

    private void initView(final String coffeeMachineId, Common.ReviewStatusEnum statusValue) {
        //remove university map box on header
        setAddReviewHeader();
        swipeFragment(coffeeMachineId, statusValue);
    }

    public void swipeFragment(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus) {
        //replace view
        switch (reviewStatus) {
            case GOOD:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_green_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.GOOD_STATUS_STRING);

                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green));
                break;
            case NOTSOBAD:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_yellow_lemon_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.NOTSOBAD_STATUS_STRING);
                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow_lemon));
                break;
            case WORST:
                hideMoreTextHeader();
                addReviewView.findViewById(R.id.headerAddReviewLayoutId).setBackgroundColor(
                        mainActivityRef.getResources().getColor(R.color.light_violet_soft));
                ((TextView)((ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId)).getChildAt(0))
                        .setText(Common.TERRIBLE_STATUS_STRING);
                addReviewView.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_violet));
                break;
        }

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //set MORE text
                if((addReviewView.findViewById(R.id.reviewTextIconTextViewId)).getTag() != null) {
//                    Common.hideKeyboard(mainActivityRef, (EditText)reviewEditText);
                    addReview(coffeeMachineId, reviewStatus, true);
                    CoffeeMachineActivity.hideAddReviewView();
                    return;
                }
                addReview(coffeeMachineId, reviewStatus, false);
            }
        });

    }

    public void addReview(final String coffeeMachineId, final Common.ReviewStatusEnum reviewStatus,
                                 boolean reviewWithText) {
        Bundle args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

        String reviewText = "Review for this machine - auto generated";

        if(reviewWithText) {
            //get text by editText
//            reviewText = ((EditText)reviewEditText).getText().toString();
            reviewText = ((TextView)addReviewView.findViewById(R.id.reviewTextIconTextViewId)).getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
        }

        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getProfilePicturePath(), reviewStatus);

        mainActivityRef.getFragmentManager().popBackStack();

        if(!addReviewFromListView) {
            createReviewsListView(mainActivityRef.getSupportFragmentManager(), reviewStatus, args);
        }
    }

    public class AddMoreTextAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            final ViewGroup parent = (ViewGroup)view.getParent().getParent();
            final ViewGroup parent = (ViewGroup)addReviewView.findViewById(R.id.reviewViewContainerLayoutId);
            final ViewGroup parentHeader = (ViewGroup)addReviewView.findViewById(R.id.headerAddReviewLayoutId).getParent();
            final LinearLayout addReviewIconView = (LinearLayout)(parent).getChildAt(0);
            final LinearLayout addTextOnReviewView = (LinearLayout)(parentHeader).getChildAt(1);

            if(addTextOnReviewView.getVisibility() == View.GONE) {
//                addReviewIconView.setVisibility(View.GONE);
                addTextOnReviewView.setVisibility(View.VISIBLE);
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.GONE);
/*
                (addTextOnReviewView.findViewById(R.id.addMoreTextOnReviewButtonId))
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String reviewText = ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)).getText().toString();

                                addReviewIconView.setVisibility(View.VISIBLE);
                                ((TextView)addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setText(reviewText);
                                (addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setTag(Common.SET_MORE_TEXT_ON_REVIEW);
                                addTextOnReviewView.setVisibility(View.GONE);
                                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
                                Common.hideKeyboard(mainActivityRef, ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)));
                            }
                        });*/

            } else if(addTextOnReviewView.getVisibility() == View.VISIBLE) {
/*                addReviewIconView.setVisibility(View.VISIBLE);
                addTextOnReviewView.setVisibility(View.GONE);*/
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);

                String reviewText = ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)).getText().toString();

//                addReviewIconView.setVisibility(View.VISIBLE);
                ((TextView)addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setText(reviewText);
                (addReviewIconView.findViewById(R.id.reviewTextIconTextViewId)).setTag(Common.SET_MORE_TEXT_ON_REVIEW);
                addTextOnReviewView.setVisibility(View.GONE);
                addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
                Common.hideKeyboard(mainActivityRef, ((EditText) addTextOnReviewView.findViewById(R.id.reviewEditTextId)));

            }
        }
    }

    public void hideMoreTextHeader() {
        View addMoreTextView = addReviewView.findViewById(R.id.addMoreTextLayoutId);
        View headerAddReviewView = addReviewView.findViewById(R.id.headerAddReviewLayoutId);
        if(addMoreTextView.getVisibility() == View.VISIBLE) {
            addMoreTextView.setVisibility(View.GONE);
            headerAddReviewView.setVisibility(View.VISIBLE);
            addReviewView.findViewById(R.id.addReviewButtonId).setVisibility(View.VISIBLE);
        }

    }

    public static void setAddReviewHeader() {
        View headerMapLayout = mainActivityRef.findViewById(R.id.headerMapLayoutId);
        View headerTabReviewLayout = mainActivityRef.findViewById(R.id.headerTabReviewLayoutId);

        headerMapLayout.setVisibility(View.GONE);
        headerTabReviewLayout.setVisibility(View.VISIBLE);
    }


//    private static LinearLayout addReviewAction, addReviewTextBarContainer;
//    private static LinearLayout addReviewAction;
//    private static ScrollView addReviewViewContainerLayout;
//    private static ImageView addReviewWithoutTextImageView, addReviewWithMoreTextImageView;
    /*private static LinearLayout addMoreTextOnReviewLayout;
    private static View choiceTypeReviewLayout;
    private static TextView goodReviewTab, notSoBadReviewTab, worstReviewTab;//, addTextbarTextHeader;
    private static EditText reviewEditText;


    public static void initView(final String coffeeMachineId) {



        //change fragment
        args = new Bundle();
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);

//        addReviewButton = (Button)addReviewView.findViewById(R.id.addReviewButtonId);
        addReviewButton = (LinearLayout)addReviewView.findViewById(R.id.addReviewButtonId);
        reviewEditText = (EditText)addReviewView.findViewById(R.id.reviewEditTextId);
        reviewEditText.setVisibility(View.GONE);

//        addReviewViewContainerLayout = (ScrollView)addReviewView.findViewById(R.id.addReviewViewContainerLayoutId);
        addMoreTextOnReviewLayout = (LinearLayout)addReviewView.findViewById(R.id.addMoreTextOnReviewLayoutId);

        choiceTypeReviewLayout = addReviewView.findViewById(R.id.choiceTypeReviewLayoutId);
//        addReviewTextBarContainer = (LinearLayout)addReviewView.findViewById(R.id.addReviewTextBarContainerId);
//        addReviewAction = (LinearLayout)addReviewView.findViewById(R.id.addReviewActionId);
//        addReviewWithoutTextImageView = (ImageView)addReviewView.findViewById(R.id.addReviewWithoutTextImageViewId);
//        addReviewWithMoreTextImageView = (ImageView)addReviewView.findViewById(R.id.addReviewWithMoreTextImageViewId);
        //TAB
        goodReviewTab = (TextView)addReviewView.findViewById(R.id.goodTabTextId);
        notSoBadReviewTab = (TextView)addReviewView.findViewById(R.id.notBadTabTextId);
        worstReviewTab = (TextView)addReviewView.findViewById(R.id.worstTabTextId);

        initView(coffeeMachineId);




//        addReviewTextBarContainer.setVisibility(View.GONE);
        //init reviewStatus
        choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.GOOD);

        goodReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_green));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_black));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.GOOD);

            }
        });
        notSoBadReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_yellow));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_black));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.NOTSOBAD);
            }
        });
        worstReviewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReviewButton.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.light_black));
                ((TextView)addReviewButton.getChildAt(0)).setTextColor(mainActivityRef
                        .getResources().getColor(R.color.light_grey));
                choiceTypeReviewLayout.setTag(Common.ReviewStatusEnum.WORST);
            }
        });


        addMoreTextOnReviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reviewEditText.isActivated()) {
                    //NO MORE TEXT
                    reviewEditText.setActivated(false);
                    reviewEditText.setVisibility(View.GONE);
                } else {
                    //set MORE TEXT
                    reviewEditText.setActivated(true);
                    reviewEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set MORE text
                if(reviewEditText != null && reviewEditText.isActivated()) {
                    Common.hideKeyboard(mainActivityRef, reviewEditText);
                    addReview(coffeeMachineId, true);
                    return;
                }
                addReview(coffeeMachineId, false);
            }
        });

    }


    public static void addReview(final String coffeeMachineId, boolean reviewWithText) {
        String reviewText = "Review for this machine - auto generated";

        if(reviewWithText) {
            //get text by editText
            reviewText = reviewEditText.getText().toString();
            if(reviewText.equals(new String("")))  {
                Common.displayError("you must insert your text review!", mainActivityRef);
                return;
            }
        }
        Common.ReviewStatusEnum reviewStatus = Common.ReviewStatusEnum.valueOf(choiceTypeReviewLayout
                .getTag().toString());
//                Log.d(TAG, "reviewStatus " + Common.ReviewStatusEnum.valueOf(reviewStatusValue));
        mainActivityRef.getFragmentManager().popBackStack();

        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                coffeeMachineApplication.coffeeMachineData.getRegisteredUser().getProfilePicturePath(), reviewStatus);
        createReviewsListView(mainActivityRef.getFragmentManager(), reviewStatus, args);

    }*/


  /*  public static void swipeFragment(SwipeDetector.Action action, int swipeCounter) {
        if(action == SwipeDetector.Action.LR) {
            Log.d(TAG, "caught left swipe action - " + swipeCounter);
            removeReview(swipeCounter + 1);
            initAddReview(swipeCounter);
        } else if(action == SwipeDetector.Action.RL) {
            Log.d(TAG, "caught right swipe action");
            removeReview(swipeCounter - 1);
            initAddReview(swipeCounter);
        }
    }*/

}