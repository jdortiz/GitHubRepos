package com.powwau.githubrepos;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GithubReposActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_repos);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_github_repos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        final static String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        EditText mEditTextUsername;
        TextView mTextViewRepos;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_github_repos, container, false);
            wireUpViews(rootView);
            prepareButton(rootView);
            return rootView;
        }

        private void wireUpViews(View rootView) {
            mEditTextUsername = (EditText)rootView.findViewById(R.id.edit_text_username);
            mTextViewRepos = (TextView)rootView.findViewById(R.id.text_view_repos);
        }

        private void prepareButton(View rootView) {
            Button buttonGetRepos = (Button)rootView.findViewById(R.id.button_get_repos);
            buttonGetRepos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = mEditTextUsername.getText().toString();
                    String message = String.format(getString(R.string.getting_repos_for_user), username);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    try {
                        URL query = constructURLQuery(username);
                        HttpURLConnection httpConnection = (HttpURLConnection)query.openConnection();
                        try {
                            String response = readFullResponse(httpConnection.getInputStream());
                            mTextViewRepos.setText(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            httpConnection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private URL constructURLQuery(String username) throws MalformedURLException {
            final String GITHUB_BASE_URL = "api.github.com";
            final String USERS_PATH = "users";
            final String REPOS_ENDPOINT = "repos";

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https").authority(GITHUB_BASE_URL).
                    appendPath(USERS_PATH).appendPath(username).
                    appendPath(REPOS_ENDPOINT);
            Uri uri = uriBuilder.build();
            Log.d(LOG_TAG, "Built URI: " + uri.toString());
            return new URL(uri.toString());
        }

        private String readFullResponse(InputStream inputStream) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String response = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            if (stringBuilder.length() > 0) {
                response = stringBuilder.toString();
            }
            return response;
        }
    }
}
