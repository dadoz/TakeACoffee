package com.application.takeacoffee.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.commons.Common;
import com.application.datastorage.CoffeeMachineDataStorageApplication;
import com.application.models.Review;
import com.application.models.User;
import com.application.takeacoffee.CoffeeMachineActivity;
import com.application.takeacoffee.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by davide on 08/04/14.
 */
public class ReviewListFragment extends Fragment {
    private static final String TAG = "ReviewListFragment";
    private static CoffeeMachineDataStorageApplication coffeeMachineApplication;
    private static FragmentActivity mainActivityRef = null;

    private Common.ReviewStatusEnum reviewStatus;
    private View reviewListView;
    private String coffeeMachineId;
    private boolean isTodayReview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        reviewListView = inflater.inflate(R.layout.review_list_fragment, container, false);
        View emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);

        //get data from
        coffeeMachineApplication = (CoffeeMachineDataStorageApplication) this.getActivity().getApplication();

        //get args from fragment
        coffeeMachineId = (String)this.getArguments().get(Common.COFFE_MACHINE_ID_KEY);
        isTodayReview = ((Boolean) this.getArguments().get(Common.IS_TODAY_REVIEW_KEY)).booleanValue();

        reviewStatus = Common.ReviewStatusEnum.valueOf(
                (String)this.getArguments().get(Common.REVIEW_STATUS_KEY));
        ArrayList<Review> reviewList = null;
        Calendar cal = Calendar.getInstance();
        Common.ReviewListTimestamp reviewListObj = null;

        long fromTimestamp;
        long  toTimestamp;
        if(isTodayReview) {
            cal.add(Calendar.DATE, -1);
            fromTimestamp = (cal.getTime()).getTime(); //yesterdayTimestamp
            toTimestamp = Common.DATE_NOT_SET;
            Log.e(TAG, "today review");
        } else {
            cal.add(Calendar.DATE, -1);
            toTimestamp = (cal.getTime()).getTime();//yesterdayTimestamp
            cal.add(Calendar.MONTH, -1); // 1 MONTH BACK
//            fromTimestamp = (cal.getTime()).getTime(); //prevMonthTimestamp
            fromTimestamp = Common.DATE_NOT_SET;
        }

        reviewList = ChoiceReviewContainerFragment.getReviewDataByTimestamp(coffeeMachineId, coffeeMachineApplication,
                reviewStatus, fromTimestamp, toTimestamp);
        reviewListObj = new Common.ReviewListTimestamp(fromTimestamp,
                toTimestamp, reviewList);

        if(reviewList == null || reviewListObj == null) {
            Log.d(TAG,"this is the getReviewData EMPTY");
            Common.setCustomFont(emptyView, this.getActivity().getAssets());
            return emptyView;
        }

        ListView listView = (ListView)reviewListView.findViewById(R.id.reviewsContainerListViewId);
        setDataWithListView(listView, reviewListObj, coffeeMachineId);

        setReviewListHeader(coffeeMachineId, (reviewListView
                .findViewById(R.id.reviewStatusTextViewId)), null, true);

        setHeader();
        setPreviousReviewsButtonAction();
        Common.setCustomFont(reviewListView, getActivity().getAssets());
        return reviewListView;
    }

    private void setPreviousReviewsButtonAction() {
    /* TEST **/
/*        ListView listView = (ListView)reviewListView.findViewById(R.id.reviewsContainerListViewId);
        ReviewListerAdapter adapter = (ReviewListerAdapter)listView.getAdapter();
        ArrayList<Review> reviewList = adapter.getList();
        reviewList.removeAll(reviewList);*/
        ListView listView = (ListView)reviewListView.findViewById(R.id.reviewsContainerListViewId);
        final ReviewListerAdapter adapter = (ReviewListerAdapter)listView.getAdapter();
        final View prevReviewsButtonId = reviewListView.findViewById(R.id.prevReviewsButtonId);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(adapter.getFromTimestamp());
        cal.add(Calendar.DATE, -1);
        long dayBeforeFromTimestamp = (cal.getTime()).getTime(); //millisec
        cal.add(Calendar.MONTH, -1); // 1 MONTH BACK
//        long prevMonthFromTimestamp = (cal.getTime()).getTime();
        long prevMonthFromTimestamp = Common.DATE_NOT_SET;

        final ArrayList<Review> prevReviewList = ChoiceReviewContainerFragment.getReviewDataByTimestamp(coffeeMachineId,
                coffeeMachineApplication, reviewStatus, prevMonthFromTimestamp, dayBeforeFromTimestamp);
        if(prevReviewList == null) {
            Log.e(TAG, "previous review list is empty");
            prevReviewsButtonId.setVisibility(View.GONE);
            return;
        }


        prevReviewsButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Review> reviewList = adapter.getList();
                reviewList.removeAll(reviewList);
                reviewList.addAll(prevReviewList);
                adapter.notifyDataSetChanged();
                prevReviewsButtonId.setVisibility(View.GONE);
            }
        });
    }

    public void setHeader() {
//        CoffeeMachineActivity.setHeaderByFragmentId(3, getFragmentManager(), null);
        CoffeeMachineActivity.setHeaderByFragmentId(1, getFragmentManager(), coffeeMachineId);
        mainActivityRef.findViewById(R.id.addReviewSwipeButtonId).setVisibility(View.INVISIBLE);

    }

    private void setReviewListHeader(final String coffeeMachineId, final View reviewStatusText,
                                 final View reviewStatusAddImageView, boolean setTextLabel) {
        setReviewListHeaderBackgroundLabel(reviewStatusText, setTextLabel);

        if(reviewStatusAddImageView != null) {
            reviewStatusAddImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "add review on " + reviewStatus);
                    Bundle args = new Bundle();
                    args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
                    args.putBoolean(Common.ADD_REVIEW_FROM_LISTVIEW, true);
                }
            });
        }
    }

    public void setReviewListHeaderBackgroundLabel(final View reviewStatusText, boolean setLabel) {
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
                colorViewStatus = getResources().getColor(R.color.light_yellow_lemon);
                break;
            case WORST:
                labelStatus = "Terrible Reviews";
                colorViewStatus = getResources().getColor(R.color.light_violet);
                break;
        }

        reviewStatusText.setBackgroundColor(colorViewStatus);
        if(setLabel) {
            ((TextView)reviewStatusText).setText(labelStatus);
        }
    }

    public void setDataWithListView(ListView listView, Common.ReviewListTimestamp reviewListObj,
                                    final String coffeeMachineId) {
        ReviewListerAdapter reviewListenerAdapter = new ReviewListerAdapter(mainActivityRef, R.layout.review_template,
                reviewListObj, coffeeMachineId);
        reviewListenerAdapter.notifyDataSetChanged();
        listView.setAdapter(reviewListenerAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final LinearLayout mainItemView = (LinearLayout) view.findViewById(R.id.mainItemViewId);
                final View extraMenuItemView =  view.findViewById(R.id.extraMenuItemViewId);

                if(extraMenuItemView.getVisibility() == View.VISIBLE) {
                    ((ReviewListerAdapter)adapterView.getAdapter()).initExtraMenuAction(extraMenuItemView, mainItemView,
                        (ReviewListerAdapter)adapterView.getAdapter(), getFragmentManager());
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int position, long id) {
                User user = coffeeMachineApplication.getRegisteredUser();
                Review reviewObj = (Review) adapterView.getItemAtPosition(position);

                final View mainItemView =  view.findViewById(R.id.mainItemViewId);
                final View extraMenuItemView =  view.findViewById(R.id.extraMenuItemViewId);

                //check my post and add action
                if (mainItemView.getVisibility() == View.VISIBLE &&
                        reviewObj.getUserId() == user.getId()) {
                    try {
                        Common.vibrate(getActivity(), Common.VIBRATE_TIME);

                        mainItemView.setVisibility(View.GONE);
                        extraMenuItemView.setVisibility(View.VISIBLE);
                        setReviewListHeaderBackgroundLabel(extraMenuItemView, false);

                        int prevSelectedItemPosition = ((ReviewListerAdapter) adapterView.getAdapter())
                                .getSelectedItemIndex();

                        //DESELECT prev item
                        if (prevSelectedItemPosition != Common.ITEM_NOT_SELECTED) {
                            int index = prevSelectedItemPosition - adapterView.getFirstVisiblePosition();
                            View v = adapterView.getChildAt(index);
                            v.findViewById(R.id.mainItemViewId).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.extraMenuItemViewId).setVisibility(View.GONE);
                            adapterView.getAdapter().getView(prevSelectedItemPosition, v, adapterView);
                        }
                        ((ReviewListerAdapter) adapterView.getAdapter()).setSelectedItemIndex(position);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

        });
    }

    private static boolean getEditReviewFragment(String reviewId, String coffeeMachineId,
                                                 Common.ReviewStatusEnum reviewStatus,
                                                 FragmentManager fragmentManager) {
        //change fragment
        Bundle args = new Bundle();
        args.putString(Common.REVIEW_ID, reviewId);
        args.putString(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
        args.putString(Common.REVIEW_STATUS_KEY, reviewStatus.name());

        EditReviewFragment reviewsFrag = new EditReviewFragment();
        reviewsFrag.setArguments(args);

        fragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.fade_in,
//                        R.anim.fade_out)
                .replace(R.id.coffeeMachineContainerLayoutId, reviewsFrag)
                .addToBackStack("back")
                .commit();
        return true;
    }

    public static void alertDialogDeleteReview(final String coffeeMachineId, final ReviewListerAdapter reviewListerAdapter,
                                        final Review reviewSelectedItem) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivityRef);
        dialogBuilder.setTitle("Delete Review");
        dialogBuilder.setMessage("Are you sure you want to delete your review?");

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String reviewListId = coffeeMachineApplication
                        .getReviewListIdByCoffeeMachine(coffeeMachineId);
                coffeeMachineApplication
                        .removeReviewById(reviewListId, reviewSelectedItem);
                (reviewListerAdapter).remove(reviewSelectedItem);
                reviewListerAdapter.setSelectedItemIndex(Common.ITEM_NOT_SELECTED);
                (reviewListerAdapter).notifyDataSetChanged();
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

    /****ADAPTER****/
   public class ReviewListerAdapter extends ArrayAdapter<Review>{
        private final long toTimestamp;
        private final long fromTimestamp;
        private ArrayList<Review> reviewList;
        public int selectedItemIndex = Common.ITEM_NOT_SELECTED;

        public String coffeeMachineId;
        public ReviewListerAdapter(Context context, int resource, Common.ReviewListTimestamp reviewListObj,
                                   String coffeeMachineId) {
            super(context, resource, reviewListObj.getReviewsList());
            this.reviewList = reviewListObj.getReviewsList();
            this.fromTimestamp = reviewListObj.getFromTimestamp();
            this.toTimestamp = reviewListObj.getToTimestamp();
            this.coffeeMachineId = coffeeMachineId;
        }

        public ArrayList<Review> getList(){
            return this.reviewList;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Review reviewObj = reviewList.get(position);
            User user = coffeeMachineApplication.getRegisteredUser();

            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.review_template, parent, false);
            View mainItemView = rowView.findViewById(R.id.mainItemViewId);
            View extraMenuItemView = rowView.findViewById(R.id.extraMenuItemViewId);

            //set data to template
            ((TextView)rowView.findViewById(R.id.reviewUsernameTextId)).setText(reviewObj.getUsername());
            ((TextView)rowView.findViewById(R.id.reviewDateTextId)).setText(reviewObj.getFormattedTimestamp());
            ((TextView)rowView.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());

            //set profile pic
            Common.drawProfilePictureByPath((ImageView) rowView.findViewById(R.id.profilePicReviewTemplateId),
                    reviewObj.getProfilePicPath(), getResources().getDrawable(R.drawable.user_icon));

            //set background color
/*            if(position % 2 != 0) {
                mainItemView.setBackgroundColor(getResources().getColor(R.color.middle_grey));
            }*/

            //set extra menu visibility
            extraMenuItemView.setVisibility(View.GONE);

            //OVERRIDE data to get loggedUser data
            if(user != null && reviewObj.getUserId().equals(user.getId())) {
                ((TextView)rowView.findViewById(R.id.reviewUsernameTextId)).setText(user.getUsername());
                Common.drawProfilePictureByPath((ImageView) rowView.findViewById(
                                R.id.profilePicReviewTemplateId), user.getProfilePicturePath(),
                        getResources().getDrawable(R.drawable.user_icon));

                if(selectedItemIndex == position) {
                    //set extra menu visibility
                    mainItemView.setVisibility(View.GONE);
                    extraMenuItemView.setVisibility(View.VISIBLE);
                    setReviewListHeaderBackgroundLabel(extraMenuItemView, false);
                }
            }

            Common.setCustomFont(rowView, getActivity().getAssets());
            return rowView;
        }

/*        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }*/

        public void setSelectedItemIndex(int position) {
            this.selectedItemIndex = position;
        }

        public int getSelectedItemIndex() {
            return selectedItemIndex;
        }

        public void initExtraMenuAction(final View extraMenuItemView, final View mainItemView,
                                        final ReviewListerAdapter reviewListerAdapter,
                                        final FragmentManager fragmentManager) {
            extraMenuItemView.findViewById(R.id.modifyReviewEditLayoutId)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getEditReviewFragment(reviewList.get(selectedItemIndex).getId(), coffeeMachineId,
                                    reviewStatus, fragmentManager);
                        }
                    });
            extraMenuItemView.findViewById(R.id.modifyReviewDeleteLayoutId)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //DELETE ACTION
                            alertDialogDeleteReview(coffeeMachineId, reviewListerAdapter, reviewList.get(selectedItemIndex));
//                Common.displayError("delete item", mainActivityRef);
                        }
                    });
            extraMenuItemView.findViewById(R.id.modifyReviewBackImageViewId)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainItemView.setVisibility(View.VISIBLE);
                            extraMenuItemView.setVisibility(View.GONE);
                            //adapterView.setTag(null);
                            reviewListerAdapter.setSelectedItemIndex(Common.ITEM_NOT_SELECTED);
                        }
                    });

        }

        public long getToTimestamp() {
            return toTimestamp;
        }

        public long getFromTimestamp() {
            return fromTimestamp;
        }

   }

/*
    public static void addReviewByReviewList(final String coffeeMachineId, Common.ReviewStatusEnum reviewStatus,
                                             AdapterView<?> adapterView) {
        String reviewText = "Review for this machine - auto generated";

        //add data to list
        User loggedUser = coffeeMachineApplication.coffeeMachineData.getRegisteredUser();
        if(loggedUser == null) {
            Common.displayError("You must be logged in before add review!", mainActivityRef);
            return;
        }

        Review review = coffeeMachineApplication.coffeeMachineData.addReviewByCoffeeMachineId(coffeeMachineId,
                loggedUser.getId(), loggedUser.getUsername(), reviewText,
                loggedUser.getProfilePicturePath(), reviewStatus);

        if(review != null) {
            ((ReviewListerAdapter)adapterView.getAdapter()).add(review);
            ((ReviewListerAdapter)adapterView.getAdapter()).notifyDataSetChanged();
        }
    }
*/

/*
    private static void initExtraMenuAction(final ReviewListerAdapter reviewListerAdapter, final Review review,
                                     final LinearLayout extraMenuItemView, final LinearLayout mainItemView,
                                     final String coffeeMachineId,
                                     final FragmentManager fragmentManager) {
        extraMenuItemView.findViewById(R.id.modifyReviewEditLayoutId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getEditReviewFragment(review.getId(), coffeeMachineId, fragmentManager);
                    }
                });
        extraMenuItemView.findViewById(R.id.modifyReviewDeleteLayoutId)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DELETE ACTION
                alertDialogDeleteReview(coffeeMachineId, reviewListerAdapter, review);
//                Common.displayError("delete item", mainActivityRef);
            }
        });
        extraMenuItemView.findViewById(R.id.modifyReviewBackImageViewId)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mainItemView.setVisibility(View.VISIBLE);
                        extraMenuItemView.setVisibility(View.GONE);
                        //adapterView.setTag(null);
                        reviewListerAdapter.setSelectedItemIndex(Common.ITEM_NOT_SELECTED);
                    }
                });

/*        (extraMenuItemView.findViewById(R.id.modifyReviewChangeTypeLayoutId)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CHANGE TYPE ACTION
                choiceTypeOfReviewDialog(modifyReviewView, coffeeMachineId);
//                (modifyReviewView, reviewList, coffeeMachineId);
            }
        });
    }*/
}
