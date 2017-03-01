package com.romerock.modules.android.rateus;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.img_logo_romerock)
    ImageView img_logo_romerock;
    @BindView(R.id.follow_twitter)
    ImageView followTwitter;
    @BindView(R.id.follow_gitHub)
    ImageView followGitHub;
    @BindView(R.id.follow_facebook)
    ImageView followFacebook;
    @BindView(R.id.rateUsDetect)
    TextView rateUsDetect;
    @BindView(R.id.btn_detect)
    Button btnDetect;
    @BindView(R.id.content_main)
    RelativeLayout contentMain;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.ratingBarMain)
    RatingBar ratingBarMain;
    @BindView(R.id.ratingBarEmpty)
    RatingBar ratingBarEmpty;
    @BindView(R.id.txtNotRateYet)
    TextView txtNotRateYet;
    private AlertDialog alertDialog;
    private SharedPreferences sharedPref;
    private String rateUs;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        sharedPref = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        WebView view = new WebView(this);
        view.setVerticalScrollBarEnabled(false);
        view.setBackgroundColor(getResources().getColor(R.color.drawable));
        ((RelativeLayout) findViewById(R.id.relContent)).addView(view);
        view.loadData(getString(R.string.thank_you), "text/html; charset=utf-8", "utf-8");
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        rateUsDetect.setTypeface(font);
        if (!sharedPref.contains(getString(R.string.preferences_rate_bar))) {
            ratingBarEmpty.setVisibility(View.VISIBLE);
            ratingBarMain.setVisibility(View.GONE);
            txtNotRateYet.setVisibility(View.VISIBLE);
        } else {
            ratingBarEmpty.setVisibility(View.GONE);
            ratingBarMain.setVisibility(View.VISIBLE);
            ratingBarMain.setRating(sharedPref.getFloat(getString(R.string.preferences_rate_bar),0));
            txtNotRateYet.setVisibility(View.GONE);
        }
    }

    public void popUp() {
        AlertDialog.Builder builder;
        LayoutInflater inflater;
        builder = new AlertDialog.Builder(this);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setCancelable(true);
        View view = inflater.inflate(R.layout.pop_up, null);
        final RatingBar rate = (RatingBar) view.findViewById(R.id.ratingBar);
        view.findViewById(R.id.popUpRate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rate.getRating() < 4) {
                    sendMail(getString(R.string.pop_up_rate_subject), "");
                } else {

                    Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getString(R.string.example_app_in_play)));
                    startActivity(rateIntent);
                }
                SharedPreferences.Editor ed = sharedPref.edit();
                ed.putFloat(getString(R.string.preferences_rate_bar),(int)rate.getRating()).commit();
                ratingBarEmpty.setVisibility(View.GONE);
                ratingBarMain.setVisibility(View.VISIBLE);
                ratingBarMain.setRating(rate.getRating());
                txtNotRateYet.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.popUpLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        TextView txtTittleDetect = (TextView) view.findViewById(R.id.txtTittleDetect);
        font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        txtTittleDetect.setTypeface(font);
        builder.setView(view);
        builder.create();
        alertDialog = builder.show();
    }

    public void sendMail(String subject, String content) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"no_reply@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, content);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, getString(R.string.choose_email_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @OnClick({R.id.img_logo_romerock, R.id.follow_twitter, R.id.follow_gitHub, R.id.follow_facebook, R.id.btn_detect})
    public void onClick(View view) {
        String url = "";
        switch (view.getId()) {
            case R.id.img_logo_romerock:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.romerock_page))));
                break;
            case R.id.follow_facebook:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook_profile)));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook))));
                }
                break;
            case R.id.follow_gitHub:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_git_hub))));
                break;
            case R.id.follow_twitter:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter_profile)));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter))));
                }
                break;
            case R.id.btn_detect:
                popUp();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if change rateUs

    }
}
