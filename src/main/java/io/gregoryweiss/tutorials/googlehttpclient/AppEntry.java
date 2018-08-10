package io.gregoryweiss.tutorials.googlehttpclient;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppEntry {

    private static HttpTransport TRANSPORT;

    private static HttpRequestFactory REQ_FACTORY;

    private static HttpTransport transport()
    {
        if (null == TRANSPORT)
        {
            TRANSPORT = new NetHttpTransport();
        }
        return TRANSPORT;
    }

    private static HttpRequestFactory requestFactory()
    {
        if (null == REQ_FACTORY)
        {
            REQ_FACTORY = transport().createRequestFactory();
        }
        return REQ_FACTORY;
    }

    private static final String TEST_URL = "http://httpclient.requestcatcher.com/test";

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static void main(String[] args) throws IOException, NoSuchFieldException, SecurityException {

        getRequestWithQueryParameters();

        postRequestFormUrlEncoded();

        postSimpleJsonData();

        postComplexJsonData();

        parsePublicApiJsonResponse();
    }

    /**
     * Submit a GET request with query parameters
     *
     * @throws IOException
     */
    private static void getRequestWithQueryParameters() throws IOException
    {
        GenericUrl url = new GenericUrl(TEST_URL);
        url.put("arg1", true);
        url.put("arg2", 45);
        HttpRequest request = requestFactory().buildGetRequest(url);
        @SuppressWarnings("unused")
        HttpResponse response = request.execute();
    }

    /**
     * Submit an x-www-form-urlencoded POST request (like submitting an ordinary HTML form)
     *
     * @throws IOException
     */
    private static void postRequestFormUrlEncoded() throws IOException
    {
        GenericUrl url = new GenericUrl(TEST_URL);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("arg1", true);
        data.put("arg2", 45);
        HttpContent content = new UrlEncodedContent(data);
        requestFactory().buildPostRequest(url, content).execute();
    }

    /**
     * Submit a POST request with simple JSON data in the payload (i.e., where
     * the values of all properties are primitives).
     *
     * @throws IOException
     */
    private static void postSimpleJsonData() throws IOException
    {
        GenericUrl url = new GenericUrl(TEST_URL);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("arg1", true);
        data.put("arg2", 45);
        HttpContent content = new JsonHttpContent(JSON_FACTORY, data);
        requestFactory().buildPostRequest(url, content).execute();
    }

    /**
     * Submit a POST request with JSON data in the payload where the value of one
     * of the properties is an object (not just a primitive type).
     *
     * @throws IOException
     */
    private static void postComplexJsonData() throws IOException
    {
        GenericUrl url = new GenericUrl(TEST_URL);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("arg1", true);
        data.put("arg2", 45);
        CustomObj customObj = new CustomObj();
        customObj.id = 27;
        customObj.name = "Gregory L Weiss";
        data.put("arg3", customObj);
        HttpContent content = new JsonHttpContent(JSON_FACTORY, data);
        requestFactory().buildPostRequest(url, content).execute();
    }


    /**
     * Parse the JSON response of the public Github API.
     *
     * @throws IOException
     */
    private static void parsePublicApiJsonResponse() throws IOException
    {
        GenericUrl url = new GenericUrl("https://api.github.com/users");
        url.put("per_page", 5);
        HttpRequest request = requestFactory().buildGetRequest(url);

        // Set the parser to use for parsing the returned JSON data
        request.setParser(new JsonObjectParser(JSON_FACTORY));

        // Use GSON's TypeToken to let the parser know to expect a List<GithubUser>
        Type type = new TypeToken<List<GithubUser>>() {}.getType();

        @SuppressWarnings("unchecked")
        List<GithubUser> users = (List<GithubUser>) request.execute().parseAs(type);
        if (null != users && !users.isEmpty())
        {
            System.out.println("GitHubUser 0: " + users.get(0));
        }
    }

    /**
     * A custom DTO created as an example. In a real DTO class you would typically have getters & setters.
     */
    private static class CustomObj
    {
        @Key
        private int id;
        @Key
        private String name;
    }
}
