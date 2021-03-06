package net.pandam.kakaobank;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import net.pandam.kakaobank.adapter.AlbumPagerAdapter;
import net.pandam.kakaobank.adapter.PhotoPagerAdapter;
import net.pandam.kakaobank.global.AppCompatBaseActivity;
import net.pandam.kakaobank.global.Constants;
import net.pandam.kakaobank.module.AlbumInfo;
import net.pandam.kakaobank.module.PhotosInfo;
import net.pandam.kakaobank.uitil.EncryptionApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatBaseActivity {

    MaterialSearchView searchView;
    private final int REQUEST_PERMISSION_STORAGE		= 1000;

    private static AQuery aq;
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private RecyclerView photoRecyclerView = null;
    private GridLayoutManager glManager = null;
    private boolean loading = true;
    int firstVisibleItem = 0;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int previousTotal = 0;
    private int pageno = 1;
    private int lastCount = 0;
    private ArrayList<PhotosInfo> dataSet = null;
    private Adapter mAdapter = null;
    private ArrayList<PhotosInfo> photosInfos;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);

        // 퍼미션 검사
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            return;
        }

        initialize();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initialize();
                else
                {
                    alert(R.string.no_permission);
                    finish();
                }
                break;
        }
    }

    private void initialize()
    {

        aq = new AQuery(this);
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                showModalProgress(true, getString(R.string.list_loading));
                viewPager.setCurrentItem(0);

                clearQuery();
                pref.edit().putString("query", query).commit();

                setSearchImage(0);

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

    private void clearQuery()
    {
        dataSet = new ArrayList<>();
        pageno = 1;
        lastCount = 0;

        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

        dataSet = new ArrayList<>();
        mAdapter = null;

        setRecyclerView();
    }

    private void setRecyclerView()
    {

        photoRecyclerView = (RecyclerView) viewPager.findViewById(R.id.rvMain);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        photoRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        glManager = new GridLayoutManager
                (getApplication(),
                        2,
                        GridLayoutManager.VERTICAL, false);
        photoRecyclerView.setLayoutManager(glManager);
    }

    private void setSearchImage(final int plus) {

        photosInfos = new ArrayList<PhotosInfo>();

        String Keyword = URLEncoder.encode(pref.getString("query",""));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("apikey", Constants.KEY);
        params.put("q", Keyword);
        params.put("output", "json");

        pageno = pageno + plus;

        aq.ajax(EncryptionApp.getValue(Constants.API_SEARCH_URL) + "?apikey=" + Constants.KEY + "&q=" + Keyword + "&result=20&pageno=" + pageno + "&output=json", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                if (jo != null) {
                    try {
                        JSONObject joChannel = jo.getJSONObject("channel");

                        if(joChannel != null) {
                            String result = joChannel.getString("result");

                            JSONArray jaItem = joChannel.getJSONArray("item");

                            for (int i = 0; i < jaItem.length(); i++) {
                                JSONObject joItem = jaItem.getJSONObject(i);

                                PhotosInfo pi = new PhotosInfo();
                                pi.thumbnail = joItem.getString("thumbnail");
                                pi.image = joItem.getString("image");
                                pi.title = pref.getString("query", "");

                                photosInfos.add(pi);
                            }
                            setItme();
                        }
                        else
                            notificationNoData();



                    } catch (Exception e) {
                        notificationNoData();
                    }
                }
                else
                    notificationNoData();

            }

            private void setItme() {
                if(photosInfos.size() <= 0)
                {
                    Toast.makeText(context, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                    aq.id(R.id.tvGuide).visible();
                    dataSet.clear();

                    mAdapter = new PhotoPagerAdapter(dataSet, viewPager);
                    photoRecyclerView.setAdapter(mAdapter);

                }
                else {

                    for (int i = 0; i < photosInfos.size(); i++) {
                        dataSet.add(photosInfos.get(i));
                    }


                    if(plus == 0) {
                        mAdapter = new PhotoPagerAdapter(dataSet, viewPager);
                        photoRecyclerView.setAdapter(mAdapter);
                    }

                    mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
                    aq.id(R.id.tvGuide).gone();


                    photoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            totalItemCount = glManager.getItemCount();
                            visibleItemCount = glManager.getChildCount();
                            firstVisibleItem = glManager.findFirstVisibleItemPosition();

                            if (loading) {
                                if (totalItemCount > previousTotal && lastCount == 0) {
                                    loading = false;
                                    previousTotal = totalItemCount;
                                }
                            }
                            if (!loading && (totalItemCount - visibleItemCount)
                                    <= (firstVisibleItem + 5)) {
                                // End has been reached
                                if(pageno < 3) {
                                    showModalProgress(true, getString(R.string.list_loading));
                                    setSearchImage(1);
                                    loading = true;
                                }
                                else {
                                    if(lastCount == 0)
                                    {
                                        Toast.makeText(context, getString(R.string.last_page), Toast.LENGTH_SHORT).show();
                                    }
                                    loading = false;

                                    lastCount++;
                                }
                            }
                        }
                    });
                }
                showModalProgress(false);
            }
        });
    }

    public void notificationNoData()
    {
        Toast.makeText(context, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
        aq.id(R.id.tvGuide).visible();
    }


    public static void setMyBox(Context context, View view)
    {
        RecyclerView albumSectionsPagerAdapter;
        Adapter mAdapter;

        ArrayList<AlbumInfo> dataSet;
        ArrayList<AlbumInfo> albumInfos = new ArrayList<AlbumInfo>();

        albumSectionsPagerAdapter = (RecyclerView) view.findViewById(R.id.rvPhoto);
        TextView tvNoData = (TextView) view.findViewById(R.id.tvNoData);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        albumSectionsPagerAdapter.setHasFixedSize(true);

        // use a linear layout manager
        albumSectionsPagerAdapter.setLayoutManager(new GridLayoutManager
                (context,
                        2,
                        GridLayoutManager.VERTICAL, false));

        // specify an adapter (see also next example)
        dataSet = new ArrayList<>();
        // Set up the ViewPager with the sections adapter.
        File folder = new File(Environment.getExternalStorageDirectory() + "/kakaobank/");
        File[] list = folder.listFiles();

        try {

            for(File file : list) {
                if (file.getName().endsWith(".png")) {
                    AlbumInfo ai = new AlbumInfo();
                    ai.image = file.getName();
                    albumInfos.add(ai);
                }
            }
            for(int i = 0; i < albumInfos.size(); i++) {
                dataSet.add(albumInfos.get(i));
            }

                mAdapter = new AlbumPagerAdapter(dataSet);
                albumSectionsPagerAdapter.setAdapter(mAdapter);

            if(albumInfos.size() <= 0)
                aq.id(tvNoData).visible();
            else
                aq.id(tvNoData).gone();
        }
        catch (Exception ex)
        {
            aq.id(tvNoData).visible();
        }


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

                    setMyBox(getContext(), rootView);

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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "이미지 리스트";
                case 1:
                    return "보관함";
            }
            return null;
        }
    }
}
