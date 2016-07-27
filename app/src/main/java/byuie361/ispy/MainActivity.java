package byuie361.ispy;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static ActionBar myBar;
    public static Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBar = getSupportActionBar();
        myContext = MainActivity.this;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab2));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        iSpyCon.cmd(2, "sudo sh kill_servers; sleep 3; (sudo sh start_servers >/dev/null 2>&1)\n");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSpyCon.cmd("sudo sh kill_servers");
        iSpyCon.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vid_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshBtn:
                ControlFragment.wv.setMinimumHeight(300);
                ControlFragment.wv.setInitialScale(170);
                ControlFragment.wv.setVerticalScrollBarEnabled(false);
                ControlFragment.wv.setHorizontalScrollBarEnabled(false);
                ControlFragment.wv.loadUrl("http://192.168.2.4:8080/stream_simple.html");
                ControlFragment.wv.setScrollX(295);
                break;
            case R.id.connectBtn:
                iSpyCon.cmd(1, "sudo sh kill_servers; sleep 3; (sudo sh start_servers >/dev/null 2>&1)\n");
                break;
        }
        return false;
    }

    public static void title_red() {
        if (myBar != null) {
            myBar.setSubtitle(Html.fromHtml("<font color='#FF0000' >Restarting servers</font><small>"));
        }
    }

    public static void title_green() {
        if (myBar != null) {
            myBar.setSubtitle(Html.fromHtml("<font color='#008000' >Servers online</font><small>"));
        }
    }

    public static void calSaved(){
        Toast.makeText(myContext, "Calibration Saved!", Toast.LENGTH_SHORT).show();
    }
}