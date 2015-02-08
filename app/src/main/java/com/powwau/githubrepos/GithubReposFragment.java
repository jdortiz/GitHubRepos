package com.powwau.githubrepos;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class GithubReposFragment extends Fragment {
    private final static String LOG_TAG = GithubReposFragment.class.getSimpleName();
    EditText mEditTextUsername;
    TextView mTextViewRepos;

    public GithubReposFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_github_repos, container, false);
        mEditTextUsername = (EditText)rootView.findViewById(R.id.edit_text_username);
        mTextViewRepos = (TextView)rootView.findViewById(R.id.text_view_repos);
        Button buttonGetRepos = (Button)rootView.findViewById(R.id.button_get_repos);
        buttonGetRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEditTextUsername.getText().toString();
                String message = String.format(getString(R.string.getting_repos_for_user), username);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                new FetchReposTask().execute(username);
            }
        });
        return rootView;
    }


    private class FetchReposTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String username;
            String response = "";
            if (params.length > 0) {
                username = params[0];
            } else {
                username = "octocat";
            }
            try {
                URL query = constructURLQuery(username);
                HttpURLConnection httpConnection = (HttpURLConnection)query.openConnection();
                try {
                    response = readFullResponse(httpConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    httpConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
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

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(LOG_TAG, response);
            mTextViewRepos.setText(response);
        }
    }
}
