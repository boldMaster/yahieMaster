package com.yippie.android.library;

public class Utility
{
    private static final String PUBLIC_ACCESS_KEY = "abc123";

    /**
     * Function to get the public access key that enable us to access web api
     */
    public static String getPublicAccessKey()
    {
        return PUBLIC_ACCESS_KEY;
    }

    /**
     * This is an utility function that generate MD5 Hash for the passed in string
     * @param preMD5String - THis is a string to be encrypt by MD5 Hash
     */
    public static String MD5(String preMD5String)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(preMD5String.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Function to get current tiemstamp
     * @return current timestamp
     */
    public static Long getCurrentTimestamp()
    {
        return System.currentTimeMillis()/1000;
    }

}
