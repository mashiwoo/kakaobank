package net.pandam.kakaobank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.pandam.kakaobank.adapter.AlbumPagerAdapter;
import net.pandam.kakaobank.adapter.PhotoPagerAdapter;
import net.pandam.kakaobank.module.PhotosInfo;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    MaterialSearchView searchView;

    private AQuery aq;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aq = new AQuery(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                viewPager.setCurrentItem(0);

                RecyclerView photoSectionsPagerAdapter = null;

                final RecyclerView.Adapter[] mAdapter = new RecyclerView.Adapter[1];
                RecyclerView.LayoutManager mLayoutManager;

                final ArrayList<PhotosInfo> dataSet;
                final ArrayList<PhotosInfo> photosInfos = new ArrayList<PhotosInfo>();

                photoSectionsPagerAdapter = (RecyclerView) viewPager.findViewById(R.id.rvMain);

                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                photoSectionsPagerAdapter.setHasFixedSize(true);

                // use a linear layout manager
                photoSectionsPagerAdapter.setLayoutManager(new GridLayoutManager
                        (getApplication(),
                                2,
                                GridLayoutManager.VERTICAL, false));

                // specify an adapter (see also next example)
                dataSet = new ArrayList<>();

                //Do some magic
                String Keyword = URLEncoder.encode(query);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("apikey", "0711ca8469e9f84c424d5784792982ee");
                params.put("q", Keyword);
                params.put("output", "json");

                final RecyclerView finalMRecyclerView = photoSectionsPagerAdapter;
                aq.ajax("http://apis.daum.net/search/image?apikey=25f586d765608702325b1770e5e5c582&q=" + Keyword + "&output=json", params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jo, AjaxStatus status) {
                        if (jo != null) {
                            try {
                                JSONObject joChannel = jo.getJSONObject("channel");
                                String result = joChannel.getString("result");

                                JSONArray jaItem = joChannel.getJSONArray("item");

                                for (int i = 0; i < jaItem.length(); i++) {
                                    JSONObject joItem = jaItem.getJSONObject(i);

                                    PhotosInfo pi = new PhotosInfo();
                                    pi.thumbnail = joItem.getString("thumbnail");
                                    pi.image = joItem.getString("image");
                                    pi.title = query;

                                    photosInfos.add(pi);
                                }

                                setItme();


                            } catch (Exception e) {

                            }
                        }
                    }

                    private void setItme() {

                        for (int i = 0; i < photosInfos.size(); i++) {
                            dataSet.add(photosInfos.get(i));
                        }

                        mAdapter[0] = new PhotoPagerAdapter(dataSet);
                        finalMRecyclerView.setAdapter(mAdapter[0]);

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });



    }



    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            View rootView = null;
            AQuery aq = new AQuery(container);;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);

                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_photo, container, false);


                    RecyclerView albumSectionsPagerAdapter = null;

                    final RecyclerView.Adapter[] mAdapter = new RecyclerView.Adapter[1];
                    RecyclerView.LayoutManager mLayoutManager;

                    final ArrayList<PhotosInfo> dataSet;
                    final ArrayList<PhotosInfo> photosInfos = new ArrayList<PhotosInfo>();


                    albumSectionsPagerAdapter = (RecyclerView) rootView.findViewById(R.id.rvPhoto);

                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    albumSectionsPagerAdapter.setHasFixedSize(true);

                    // use a linear layout manager
                    albumSectionsPagerAdapter.setLayoutManager(new GridLayoutManager
                            (getContext(),
                                    2,
                                    GridLayoutManager.VERTICAL, false));

                    // specify an adapter (see also next example)
                    dataSet = new ArrayList<>();
                    // Set up the ViewPager with the sections adapter.
                    File folder = new File(Environment.getExternalStorageDirectory() + "/kakaobank/");
                    File[] list = folder.listFiles();
                    for(File file : list) {
                        if (file.getName().endsWith(".png")) {
                            PhotosInfo pi = new PhotosInfo();
                            pi.thumbnail = file.getName();
                            pi.image = file.getName();
                            pi.title = file.getName();
                            photosInfos.add(pi);
                        }
                    }
                    for(int i = 0; i < photosInfos.size(); i++) {
                        dataSet.add(photosInfos.get(i));
                    }

                    mAdapter[0] = new AlbumPagerAdapter(dataSet);
                    albumSectionsPagerAdapter.setAdapter(mAdapter[0]);
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
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TAB 1";
                case 1:
                    return "TAB 2";
            }
            return null;
        }
    }
}
