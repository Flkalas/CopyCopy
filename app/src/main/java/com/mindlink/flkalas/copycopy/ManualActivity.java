package com.mindlink.flkalas.copycopy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

public class ManualActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public void popupRecommandLoginWindow(View view){
        if(needLogin) {
            LoginDialogFragment ldf = new LoginDialogFragment();
            ldf.show(getFragmentManager(), "Sign");
        }else{
            Intent intent = new Intent();
            intent.putExtra("Return",false);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public static class LoginDialogFragment extends DialogFragment {
        public void returnDialog(boolean isLogin){
            Bundle extra = new Bundle();
            Intent intent = new Intent();

            extra.putBoolean("Return", isLogin);
            intent.putExtras(extra);

            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }

        @Override
        public  Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_login)
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            returnDialog(true);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            returnDialog(false);
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    boolean needLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        Intent intent = getIntent();
        needLogin = (intent.getExtras().getInt("REQUEST",2) == 2);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        CirclePageIndicator titleIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        titleIndicator.setViewPager(mViewPager);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_manual, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            TextView btnEndInit = (TextView) rootView.findViewById(R.id.btn_end_init);
            ImageView imgView = (ImageView) rootView.findViewById(R.id.img_manual);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    imgView.setImageResource(R.drawable.logo);
                    textView.setText(getString(R.string.manual_1_1)+" "+getString(R.string.app_name)+getString(R.string.manual_1_2));
                    btnEndInit.setVisibility(View.GONE);
                    break;
                case 2:
                    imgView.setImageResource(R.drawable.share);
                    textView.setText(getString(R.string.manual_2_1)+" "+getString(R.string.app_name)+getString(R.string.manual_2_2));
                    btnEndInit.setVisibility(View.GONE);
                    break;
                case 3:
                    imgView.setImageResource(R.drawable.copy_link);
                    textView.setText(getString(R.string.manual_3_1)+" "+getString(R.string.app_name)+getString(R.string.manual_3_2));
                    btnEndInit.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
