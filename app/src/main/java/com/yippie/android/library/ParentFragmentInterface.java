package com.yippie.android.library;

import android.app.Fragment;

public interface ParentFragmentInterface
{
    public void addFragment(Fragment fragment);
    public void backToFragment(String fragmentName);
    public void clearFragmentStack();
    public void switchScreen(Boolean isBack, Integer action);
}
