package com.application.takeacoffee.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.application.datastorage.DataStorageSingleton;
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
    private static DataStorageSingleton coffeeApp;
    private static FragmentActivity mainActivityRef = null;

    private Common.ReviewStatusEnum reviewStatus;
    private View reviewListView;
    private long coffeeMachineId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        mainActivityRef = getActivity();
        reviewListView = inflater.inflate(R.layout.review_list_fragment, container, false);
        View emptyView = inflater.inflate(R.layout.empty_data_status_layout, container, false);

        coffeeApp = DataStorageSingleton.getInstance(getActivity().getApplicationContext());

        coffeeMachineId = this.getArguments().getLong(Common.COFFE_MACHINE_ID_KEY);
        reviewStatus = Common.ReviewStatusEnum.valueOf(this.getArguments().
                getString(Common.REVIEW_STATUS_KEY));
        long fromTimestamp = this.getArguments().
                getLong(Common.FROM_TIMESTAMP_KEY);
        long toTimestamp = this.getArguments().
                getLong(Common.TO_TIMESTAMP_KEY);

        ArrayList<Review> reviewList = coffeeApp.getReviewListByTimestamp(coffeeMachineId,
                reviewStatus, fromTimestamp, toTimestamp);
        Common.ReviewListTimestamp reviewListObj = new Common.ReviewListTimestamp(fromTimestamp,
                toTimestamp, reviewList);
        //end of refactor

        if(reviewList == null) {
            Log.d(TAG,"this is the getReviewData EMPTY"); //THIS SHOULDNT BE DISPLAYED
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

        //TODO REFACTOR it
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(adapter.getFromTimestamp());
        cal.add(Calendar.DATE, -1);
        long dayBeforeFromTimestamp = (cal.getTime()).getTime(); //millisec
        cal.add(Calendar.MONTH, -1); // 1 MONTH BACK
//        long prevMonthFromTimestamp = (cal.getTime()).getTime();
        long prevMonthFromTimestamp = Common.DATE_NOT_SET;

        final ArrayList<Review> prevReviewList = coffeeApp.getReviewListByTimestamp(coffeeMachineId,
                reviewStatus, prevMonthFromTimestamp, dayBeforeFromTimestamp);
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

    private void setReviewListHeader(final long coffeeMachineId, final View reviewStatusText,
                                 final View reviewStatusAddImageView, boolean setTextLabel) {
        setReviewListHeaderBackgroundLabel(reviewStatusText, setTextLabel);

/*        if(reviewStatusAddImageView != null) {
            reviewStatusAddImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "add review on " + reviewStatus);
                    Bundle args = new Bundle();
                    args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
                    args.putBoolean(Common.ADD_REVIEW_FROM_LISTVIEW, true);
                }
            });
        }*/
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
                                    final long coffeeMachineId) {
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
                //User user = coffeeApp.getRegisteredUser();
                Review reviewObj = (Review) adapterView.getItemAtPosition(position);

                final View mainItemView =  view.findViewById(R.id.mainItemViewId);
                final View extraMenuItemView =  view.findViewById(R.id.extraMenuItemViewId);

                //check my post and add action
                if (mainItemView.getVisibility() == View.VISIBLE &&
                        coffeeApp.isRegisteredUser() &&
                        reviewObj.getUserId() == coffeeApp.getRegisteredUserId()) {
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

    private static boolean getEditReviewFragment(long reviewId, long coffeeMachineId,
                                                 Common.ReviewStatusEnum reviewStatus,
                                                 FragmentManager fragmentManager) {
        //change fragment
        Bundle args = new Bundle();
        args.putLong(Common.REVIEW_ID, reviewId);
        args.putLong(Common.COFFE_MACHINE_ID_KEY, coffeeMachineId);
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

    public static void alertDialogDeleteReview(final long coffeeMachineId,
                                               final ReviewListerAdapter reviewListerAdapter,
                                               final Review reviewSelectedItem) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mainActivityRef);
        dialogBuilder.setTitle("Delete Review");
        dialogBuilder.setMessage("Are you sure you want to delete your review?");

        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                coffeeApp.removeReviewById(coffeeMachineId, reviewSelectedItem);
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

        public long coffeeMachineId;
        public ReviewListerAdapter(Context context, int resource, Common.ReviewListTimestamp reviewListObj,
                                   long coffeeMachineId) {
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
            //User user = coffeeApp.getRegisteredUser();

            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.review_template, parent, false);
            View mainItemView = rowView.findViewById(R.id.mainItemViewId);
            View extraMenuItemView = rowView.findViewById(R.id.extraMenuItemViewId);

            User userOnReview = coffeeApp.getUserById(reviewObj.getUserId());
            //set data to template
            ((TextView)rowView.findViewById(R.id.reviewUsernameTextId)).setText(
                    userOnReview.getUsername());
            ((TextView)rowView.findViewById(R.id.reviewDateTextId)).setText(reviewObj.getFormattedTimestamp());
            ((TextView)rowView.findViewById(R.id.reviewCommentTextId)).setText(reviewObj.getComment());

            //set profile pic
            //TODO please remove this shit -
            if(userOnReview.getProfilePicturePath() != null) {
                Bitmap bitmap = Common.getRoundedBitmapByFile(userOnReview.getProfilePicturePath(),
                        BitmapFactory.decodeResource(mainActivityRef.getResources(), R.drawable.user_icon));
                ((ImageView) rowView.findViewById(R.id.profilePicReviewTemplateId)).setImageBitmap(bitmap);
            }

            //set background color
/*            if(position % 2 != 0) {
                mainItemView.setBackgroundColor(getResources().getColor(R.color.middle_grey));
            }*/

            //set extra menu visibility
            extraMenuItemView.setVisibility(View.GONE);

            //OVERRIDE data to get loggedUser data
            if(coffeeApp.isRegisteredUser() && (reviewObj.getUserId() == coffeeApp.getRegisteredUserId())) {
                ((TextView)rowView.findViewById(R.id.reviewUsernameTextId)).setText(coffeeApp.getRegisteredUsername());
                Bitmap bitmap = Common.getRoundedBitmapByFile(coffeeApp.getRegisteredProfilePicturePath(),
                        BitmapFactory.decodeResource( mainActivityRef.getResources(), R.drawable.user_icon));
                ((ImageView) rowView.findViewById(R.id.profilePicReviewTemplateId)).setImageBitmap(bitmap);

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
}
