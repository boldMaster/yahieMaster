package com.yippie.android.library;

/**
 * This is a singleton HttpClient class.
 * Every new HttpClient instance takes time to create and used more memory.
 * With Singleton HttpClient, this allow us to reuse the connection and allow the application
 * to perform HttpRequest more faster.
 */

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class HttpClient
{
    private static DefaultHttpClient client;

    /**
     * This is a function to return existing Default Http CLient instance to reuse.
     * @return client (DefaultHttpClient) - This is the existing constructed Default Http Client instances.
     */
    public synchronized static DefaultHttpClient getThreadSafeClient()
    {
        // If DefaultHttpClient been constructed, we should return it instead of creating new client.
        if (client != null)
        {
            return client;
        }

        client = new DefaultHttpClient();

        // As the client is globally used by all HttpRequest in the application, we have to use Thread Safe Connection Manager to handle the multiple threads.
        ClientConnectionManager mgr = client.getConnectionManager();

        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);

        return client;
    }
}
